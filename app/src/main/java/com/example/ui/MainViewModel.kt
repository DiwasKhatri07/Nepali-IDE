package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAssistRepository
import com.example.ai.AiResult
import com.example.data.*
import com.example.editor.CodeThemeColorScheme
import com.example.editor.EditorThemes
import com.example.interpreter.PythonInterpreter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class UiState(
    val projects: List<ProjectEntity> = emptyList(),
    val activeProject: ProjectEntity? = null,
    val projectFiles: List<CodeFileEntity> = emptyList(),
    val openTabs: List<CodeFileEntity> = emptyList(),
    val activeTabId: Long? = null,
    val activeFileContent: String = "",
    val activeLanguage: String = "py",
    val isDirty: Boolean = false,
    val autoSaveStatus: String = "Saved", // Saved, Saving..., Unsaved
    val autoSaveIntervalSec: Int = 30, // 30s auto save
    val isZenMode: Boolean = false,
    val isPresentationMode: Boolean = false,
    val undoStack: List<String> = emptyList(),
    val redoStack: List<String> = emptyList(),
    val currentTheme: CodeThemeColorScheme = EditorThemes.SophisticatedDark,
    val terminalOutput: String = "Code IDE Terminal Ready.\nType code or tap 'Run' to execute.",
    val isFileExplorerOpen: Boolean = false,
    val isAiDrawerOpen: Boolean = false,
    val isCommandPaletteOpen: Boolean = false,
    val isSnippetPickerOpen: Boolean = false,
    val isSearchReplaceOpen: Boolean = false,
    val isVersionHistoryOpen: Boolean = false,
    val fileVersions: List<FileVersionEntity> = emptyList(),
    val isGitPanelOpen: Boolean = false,
    val commitHistory: List<Pair<String, Long>> = listOf("Initial commit - Repository initialized" to System.currentTimeMillis() - 86400000),
    val isJsonFormatterOpen: Boolean = false,
    val isCsvViewerOpen: Boolean = false,
    val isDashboardOpen: Boolean = false,
    val isCodeAnalyzerOpen: Boolean = false,
    val isDatabaseInspectorOpen: Boolean = false,
    val isReplOpen: Boolean = false,
    val projectTodos: List<Pair<String, Boolean>> = listOf("Implement core logic" to true, "Write unit tests" to false, "Optimize algorithm performance" to false),
    val isPreviewMode: Boolean = false,
    val isSplitPreview: Boolean = false,
    val searchQuery: String = "",
    val replaceQuery: String = "",
    val cursorLine: Int = 1,
    val cursorCol: Int = 1,
    val snippets: List<SnippetEntity> = emptyList(),
    val aiIsLoading: Boolean = false,
    val aiChatMessages: List<Pair<String, String>> = emptyList(),
    val toastMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = WorkspaceRepository(db.projectDao(), db.codeFileDao(), db.snippetDao(), db.fileVersionDao())
    private val aiRepository = AiAssistRepository()

    private var autoSaveJob: kotlinx.coroutines.Job? = null

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.projects.collectLatest { projects ->
                _uiState.value = _uiState.value.copy(projects = projects)
                if (_uiState.value.activeProject == null && projects.isNotEmpty()) {
                    selectProject(projects.first())
                }
            }
        }

        viewModelScope.launch {
            repository.snippets.collectLatest { snippets ->
                _uiState.value = _uiState.value.copy(snippets = snippets)
            }
        }
    }

    fun selectProject(project: ProjectEntity) {
        _uiState.value = _uiState.value.copy(activeProject = project, openTabs = emptyList(), activeTabId = null)
        viewModelScope.launch {
            repository.getFilesForProject(project.id).collectLatest { files ->
                _uiState.value = _uiState.value.copy(projectFiles = files)
                // Auto-open first file if no tabs open
                if (_uiState.value.openTabs.isEmpty() && files.isNotEmpty()) {
                    val mainFile = files.firstOrNull { !it.isFolder }
                    mainFile?.let { openTab(it) }
                }
            }
        }
    }

    fun openTab(file: CodeFileEntity) {
        val currentTabs = _uiState.value.openTabs.toMutableList()
        if (currentTabs.none { it.id == file.id }) {
            currentTabs.add(file)
        }
        val lang = file.extension.lowercase()
        _uiState.value = _uiState.value.copy(
            openTabs = currentTabs,
            activeTabId = file.id,
            activeFileContent = file.content,
            activeLanguage = lang,
            isDirty = false,
            autoSaveStatus = "Saved",
            undoStack = listOf(file.content),
            redoStack = emptyList(),
            isFileExplorerOpen = false
        )
        loadVersionsForActiveFile(file.id)
    }

    private fun loadVersionsForActiveFile(fileId: Long) {
        viewModelScope.launch {
            repository.getVersionsForFile(fileId).collectLatest { versions ->
                _uiState.value = _uiState.value.copy(fileVersions = versions)
            }
        }
    }

    fun closeTab(fileId: Long) {
        val currentTabs = _uiState.value.openTabs.toMutableList()
        currentTabs.removeAll { it.id == fileId }
        val nextTab = currentTabs.lastOrNull()
        if (nextTab != null) {
            _uiState.value = _uiState.value.copy(
                openTabs = currentTabs,
                activeTabId = nextTab.id,
                activeFileContent = nextTab.content,
                activeLanguage = nextTab.extension.lowercase(),
                isDirty = false
            )
        } else {
            _uiState.value = _uiState.value.copy(
                openTabs = emptyList(),
                activeTabId = null,
                activeFileContent = "",
                isDirty = false
            )
        }
    }

    fun updateContent(newContent: String) {
        val currentState = _uiState.value
        if (newContent == currentState.activeFileContent) return

        val undoStack = currentState.undoStack.toMutableList()
        if (undoStack.size > 50) undoStack.removeAt(0)
        undoStack.add(currentState.activeFileContent)

        _uiState.value = currentState.copy(
            activeFileContent = newContent,
            isDirty = true,
            autoSaveStatus = "Unsaved changes",
            undoStack = undoStack,
            redoStack = emptyList()
        )

        scheduleAutoSave()
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            kotlinx.coroutines.delay(2500)
            if (_uiState.value.isDirty) {
                saveActiveFile(isAutoSave = true)
            }
        }
    }

    fun undo() {
        val state = _uiState.value
        if (state.undoStack.isEmpty()) return
        val previousContent = state.undoStack.last()
        val newUndo = state.undoStack.dropLast(1)
        val newRedo = state.redoStack + state.activeFileContent

        _uiState.value = state.copy(
            activeFileContent = previousContent,
            undoStack = newUndo,
            redoStack = newRedo,
            isDirty = true
        )
    }

    fun redo() {
        val state = _uiState.value
        if (state.redoStack.isEmpty()) return
        val nextContent = state.redoStack.last()
        val newRedo = state.redoStack.dropLast(1)
        val newUndo = state.undoStack + state.activeFileContent

        _uiState.value = state.copy(
            activeFileContent = nextContent,
            undoStack = newUndo,
            redoStack = newRedo,
            isDirty = true
        )
    }

    fun saveActiveFile(isAutoSave: Boolean = false) {
        val state = _uiState.value
        val activeId = state.activeTabId ?: return
        val activeFile = state.projectFiles.firstOrNull { it.id == activeId } ?: return

        _uiState.value = _uiState.value.copy(autoSaveStatus = "Saving...")

        viewModelScope.launch {
            val updated = activeFile.copy(content = state.activeFileContent)
            repository.updateFile(updated)
            val snapshotMsg = if (isAutoSave) "Auto-save snapshot" else "Manual save snapshot"
            repository.saveFileVersion(activeId, state.activeFileContent, snapshotMsg)

            _uiState.value = _uiState.value.copy(
                isDirty = false,
                autoSaveStatus = "Saved",
                toastMessage = if (!isAutoSave) "Saved ${activeFile.name}" else null
            )
        }
    }

    fun createNewFile(name: String) {
        val activeProj = _uiState.value.activeProject ?: return
        val ext = if (name.contains(".")) name.substringAfterLast(".").lowercase() else "txt"
        val initialContent = when (ext) {
            "html" -> "<!-- New HTML File -->\n<!DOCTYPE html>\n<html>\n<head><title>$name</title></head>\n<body>\n    <h1>New Page</h1>\n</body>\n</html>"
            "py" -> "# New Python Script\ndef main():\n    print('Hello World')\n\nif __name__ == '__main__':\n    main()"
            "css" -> "/* New Stylesheet */\nbody {\n    margin: 0;\n}"
            "js" -> "// New JavaScript File\nconsole.log('Script loaded');"
            else -> "New file"
        }

        viewModelScope.launch {
            val newFileId = repository.createFile(activeProj.id, name, ext, initialContent)
            val created = repository.getFileById(newFileId)
            created?.let { openTab(it) }
            showToast("Created $name")
        }
    }

    fun deleteFile(file: CodeFileEntity) {
        viewModelScope.launch {
            repository.deleteFile(file)
            closeTab(file.id)
            showToast("Deleted ${file.name}")
        }
    }

    fun createProject(name: String, description: String = "") {
        viewModelScope.launch {
            val newProjId = repository.createProject(name, description)
            showToast("Created project $name")
        }
    }

    fun deleteActiveProject() {
        val proj = _uiState.value.activeProject ?: return
        viewModelScope.launch {
            repository.deleteProject(proj)
            _uiState.value = _uiState.value.copy(
                activeProject = null,
                openTabs = emptyList(),
                activeTabId = null
            )
            showToast("Deleted project ${proj.name}")
        }
    }

    fun runCode() {
        val state = _uiState.value
        val lang = state.activeLanguage
        val code = state.activeFileContent

        if (lang == "py" || lang == "python") {
            _uiState.value = _uiState.value.copy(terminalOutput = "Running Python script...\n")
            viewModelScope.launch {
                val result = PythonInterpreter.execute(code)
                val outputText = buildString {
                    append("=== Python Execution Output (${result.executionTimeMs}ms) ===\n")
                    if (result.stdout.isNotBlank()) append(result.stdout).append("\n")
                    if (result.stderr.isNotBlank()) append("[ERROR]\n").append(result.stderr).append("\n")
                    append("==========================================")
                }
                _uiState.value = _uiState.value.copy(terminalOutput = outputText)
            }
        } else if (lang == "html" || lang == "htm") {
            _uiState.value = _uiState.value.copy(isPreviewMode = true)
            showToast("Rendering HTML Preview")
        } else {
            _uiState.value = _uiState.value.copy(terminalOutput = "Language '$lang' preview active.")
        }
    }

    fun updateCursorPosition(line: Int, col: Int) {
        _uiState.value = _uiState.value.copy(cursorLine = line, cursorCol = col)
    }

    fun selectTheme(theme: CodeThemeColorScheme) {
        _uiState.value = _uiState.value.copy(currentTheme = theme)
        showToast("Theme set to ${theme.name}")
    }

    fun toggleFileExplorer() {
        _uiState.value = _uiState.value.copy(isFileExplorerOpen = !_uiState.value.isFileExplorerOpen)
    }

    fun toggleAiDrawer() {
        _uiState.value = _uiState.value.copy(isAiDrawerOpen = !_uiState.value.isAiDrawerOpen)
    }

    fun toggleCommandPalette() {
        _uiState.value = _uiState.value.copy(isCommandPaletteOpen = !_uiState.value.isCommandPaletteOpen)
    }

    fun toggleSnippetPicker() {
        _uiState.value = _uiState.value.copy(isSnippetPickerOpen = !_uiState.value.isSnippetPickerOpen)
    }

    fun toggleSearchReplace() {
        _uiState.value = _uiState.value.copy(isSearchReplaceOpen = !_uiState.value.isSearchReplaceOpen)
    }

    fun togglePreviewMode() {
        _uiState.value = _uiState.value.copy(isPreviewMode = !_uiState.value.isPreviewMode)
    }

    fun insertSnippet(snippet: SnippetEntity) {
        val current = _uiState.value.activeFileContent
        val newContent = current + "\n" + snippet.code
        updateContent(newContent)
        toggleSnippetPicker()
        showToast("Inserted ${snippet.title}")
    }

    // AI Features
    fun requestAiInlineCompletion() {
        val state = _uiState.value
        _uiState.value = _uiState.value.copy(aiIsLoading = true)
        viewModelScope.launch {
            val result = aiRepository.getInlineCompletion(state.activeFileContent, "", state.activeLanguage)
            _uiState.value = _uiState.value.copy(aiIsLoading = false)
            when (result) {
                is AiResult.Success -> {
                    val newCode = state.activeFileContent + result.data
                    updateContent(newCode)
                    showToast("AI completion applied")
                }
                is AiResult.Error -> showToast(result.message)
            }
        }
    }

    fun requestAiExplanation() {
        val state = _uiState.value
        _uiState.value = _uiState.value.copy(aiIsLoading = true, isAiDrawerOpen = true)
        viewModelScope.launch {
            val result = aiRepository.explainCode(state.activeFileContent, state.activeLanguage)
            _uiState.value = _uiState.value.copy(aiIsLoading = false)
            when (result) {
                is AiResult.Success -> addAiChatMessage("Assistant", "### Code Explanation:\n\n${result.data}")
                is AiResult.Error -> addAiChatMessage("Assistant", "Error: ${result.message}")
            }
        }
    }

    fun requestAiBugCheck() {
        val state = _uiState.value
        _uiState.value = _uiState.value.copy(aiIsLoading = true, isAiDrawerOpen = true)
        viewModelScope.launch {
            val result = aiRepository.detectBugs(state.activeFileContent, state.activeLanguage)
            _uiState.value = _uiState.value.copy(aiIsLoading = false)
            when (result) {
                is AiResult.Success -> addAiChatMessage("Assistant", "### Bug & Static Analysis Report:\n\n${result.data}")
                is AiResult.Error -> addAiChatMessage("Assistant", "Error: ${result.message}")
            }
        }
    }

    fun requestAiRefactoring() {
        val state = _uiState.value
        _uiState.value = _uiState.value.copy(aiIsLoading = true, isAiDrawerOpen = true)
        viewModelScope.launch {
            val result = aiRepository.suggestRefactoring(state.activeFileContent, state.activeLanguage)
            _uiState.value = _uiState.value.copy(aiIsLoading = false)
            when (result) {
                is AiResult.Success -> addAiChatMessage("Assistant", "### Code Refactoring Suggestions:\n\n${result.data}")
                is AiResult.Error -> addAiChatMessage("Assistant", "Error: ${result.message}")
            }
        }
    }

    fun requestAiDocstring() {
        val state = _uiState.value
        _uiState.value = _uiState.value.copy(aiIsLoading = true)
        viewModelScope.launch {
            val result = aiRepository.generateDocstrings(state.activeFileContent, state.activeLanguage)
            _uiState.value = _uiState.value.copy(aiIsLoading = false)
            when (result) {
                is AiResult.Success -> {
                    updateContent(result.data)
                    showToast("Docstrings generated")
                }
                is AiResult.Error -> showToast(result.message)
            }
        }
    }

    fun sendAiUserMessage(message: String) {
        if (message.isBlank()) return
        addAiChatMessage("User", message)
        val state = _uiState.value
        _uiState.value = _uiState.value.copy(aiIsLoading = true)
        viewModelScope.launch {
            val result = aiRepository.chatWithAi(state.aiChatMessages, state.activeFileContent, state.activeLanguage)
            _uiState.value = _uiState.value.copy(aiIsLoading = false)
            when (result) {
                is AiResult.Success -> addAiChatMessage("Assistant", result.data)
                is AiResult.Error -> addAiChatMessage("Assistant", "Error: ${result.message}")
            }
        }
    }

    private fun addAiChatMessage(role: String, message: String) {
        val messages = _uiState.value.aiChatMessages.toMutableList()
        messages.add(role to message)
        _uiState.value = _uiState.value.copy(aiChatMessages = messages)
    }

    fun performSearchReplace(find: String, replace: String, replaceAll: Boolean) {
        if (find.isEmpty()) return
        val content = _uiState.value.activeFileContent
        val newContent = if (replaceAll) {
            content.replace(find, replace)
        } else {
            content.replaceFirst(find, replace)
        }
        updateContent(newContent)
        showToast("Replaced occurrence")
    }

    fun toggleZenMode() {
        _uiState.value = _uiState.value.copy(isZenMode = !_uiState.value.isZenMode)
        showToast(if (_uiState.value.isZenMode) "Zen Mode Active (Tap top icon to exit)" else "Exited Zen Mode")
    }

    fun togglePresentationMode() {
        _uiState.value = _uiState.value.copy(isPresentationMode = !_uiState.value.isPresentationMode)
        showToast(if (_uiState.value.isPresentationMode) "Presentation Mode On" else "Presentation Mode Off")
    }

    fun toggleVersionHistory() {
        _uiState.value = _uiState.value.copy(isVersionHistoryOpen = !_uiState.value.isVersionHistoryOpen)
    }

    fun restoreVersion(version: FileVersionEntity) {
        updateContent(version.content)
        toggleVersionHistory()
        showToast("Restored snapshot from ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(version.timestamp))}")
    }

    fun toggleGitPanel() {
        _uiState.value = _uiState.value.copy(isGitPanelOpen = !_uiState.value.isGitPanelOpen)
    }

    fun commitGitChanges(message: String) {
        if (message.isBlank()) return
        val currentHistory = _uiState.value.commitHistory.toMutableList()
        currentHistory.add(0, message to System.currentTimeMillis())
        saveActiveFile(isAutoSave = false)
        _uiState.value = _uiState.value.copy(commitHistory = currentHistory)
        showToast("Committed: '$message'")
    }

    fun toggleJsonFormatter() {
        _uiState.value = _uiState.value.copy(isJsonFormatterOpen = !_uiState.value.isJsonFormatterOpen)
    }

    fun formatJson() {
        try {
            val raw = _uiState.value.activeFileContent
            val jsonObject = org.json.JSONObject(raw)
            val formatted = jsonObject.toString(4)
            updateContent(formatted)
            showToast("JSON formatted successfully")
        } catch (e: Exception) {
            try {
                val jsonArray = org.json.JSONArray(_uiState.value.activeFileContent)
                val formatted = jsonArray.toString(4)
                updateContent(formatted)
                showToast("JSON Array formatted successfully")
            } catch (ex: Exception) {
                showToast("Invalid JSON syntax")
            }
        }
    }

    fun toggleCsvViewer() {
        _uiState.value = _uiState.value.copy(isCsvViewerOpen = !_uiState.value.isCsvViewerOpen)
    }

    fun requestAiUnitTest() {
        val state = _uiState.value
        _uiState.value = _uiState.value.copy(aiIsLoading = true, isAiDrawerOpen = true)
        viewModelScope.launch {
            val result = aiRepository.generateUnitTests(state.activeFileContent, state.activeLanguage)
            _uiState.value = _uiState.value.copy(aiIsLoading = false)
            when (result) {
                is AiResult.Success -> addAiChatMessage("Assistant", "### Generated Unit Tests:\n\n```${state.activeLanguage}\n${result.data}\n```")
                is AiResult.Error -> addAiChatMessage("Assistant", "Error: ${result.message}")
            }
        }
    }

    fun toggleDashboard() {
        _uiState.value = _uiState.value.copy(isDashboardOpen = !_uiState.value.isDashboardOpen)
    }

    fun toggleCodeAnalyzer() {
        _uiState.value = _uiState.value.copy(isCodeAnalyzerOpen = !_uiState.value.isCodeAnalyzerOpen)
    }

    fun toggleDatabaseInspector() {
        _uiState.value = _uiState.value.copy(isDatabaseInspectorOpen = !_uiState.value.isDatabaseInspectorOpen)
    }

    fun toggleRepl() {
        _uiState.value = _uiState.value.copy(isReplOpen = !_uiState.value.isReplOpen)
    }

    fun addTodoItem(task: String) {
        val current = _uiState.value.projectTodos.toMutableList()
        current.add(0, task to false)
        _uiState.value = _uiState.value.copy(projectTodos = current)
        showToast("Task added to project backlog")
    }

    fun toggleTodoItem(index: Int) {
        val current = _uiState.value.projectTodos.toMutableList()
        if (index in current.indices) {
            val (task, isDone) = current[index]
            current[index] = task to !isDone
            _uiState.value = _uiState.value.copy(projectTodos = current)
        }
    }

    fun showToast(msg: String) {
        _uiState.value = _uiState.value.copy(toastMessage = msg)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
