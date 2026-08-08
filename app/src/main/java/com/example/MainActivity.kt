package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interpreter.HtmlPreviewView
import com.example.ui.MainViewModel
import com.example.ui.components.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeIdeApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeIdeApp(viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Handle Toast notifications
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                FileExplorerDrawer(
                    projects = uiState.projects,
                    activeProject = uiState.activeProject,
                    files = uiState.projectFiles,
                    theme = uiState.currentTheme,
                    onSelectProject = { viewModel.selectProject(it) },
                    onSelectFile = { file ->
                        viewModel.openTab(file)
                    },
                    onCreateFile = { name -> viewModel.createNewFile(name) },
                    onDeleteFile = { file -> viewModel.deleteFile(file) },
                    onCreateProject = { name, desc -> viewModel.createProject(name, desc) },
                    onCloseDrawer = {}
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (!uiState.isZenMode) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = uiState.activeProject?.name ?: "Code IDE",
                                    color = uiState.currentTheme.textColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                val activeTab = uiState.openTabs.firstOrNull { it.id == uiState.activeTabId }
                                activeTab?.let {
                                    Text(
                                        text = "${it.name}${if (uiState.isDirty) " *" else ""}",
                                        color = uiState.currentTheme.keywordColor,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { viewModel.toggleFileExplorer() },
                                modifier = Modifier.testTag("file_explorer_toggle")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Explorer",
                                    tint = uiState.currentTheme.textColor
                                )
                            }
                        },
                        actions = {
                            // Command Palette
                            IconButton(
                                onClick = { viewModel.toggleCommandPalette() },
                                modifier = Modifier.testTag("command_palette_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = "Command Palette",
                                    tint = uiState.currentTheme.keywordColor
                                )
                            }

                            // Python Interactive Console REPL
                            IconButton(
                                onClick = { viewModel.toggleRepl() },
                                modifier = Modifier.testTag("python_repl_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = "Python REPL Console",
                                    tint = uiState.currentTheme.keywordColor
                                )
                            }

                            // Project Dashboard & Tasks
                            IconButton(
                                onClick = { viewModel.toggleDashboard() },
                                modifier = Modifier.testTag("project_dashboard_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = "Dashboard",
                                    tint = uiState.currentTheme.textColor
                                )
                            }

                            // Git VCS Quick Access
                            IconButton(
                                onClick = { viewModel.toggleGitPanel() },
                                modifier = Modifier.testTag("git_panel_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Commit,
                                    contentDescription = "Git VCS",
                                    tint = uiState.currentTheme.textColor
                                )
                            }

                            // Preview Toggle
                            IconButton(
                                onClick = { viewModel.togglePreviewMode() },
                                modifier = Modifier.testTag("preview_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (uiState.isPreviewMode) Icons.Default.Code else Icons.Default.Visibility,
                                    contentDescription = "Toggle Preview",
                                    tint = if (uiState.isPreviewMode) uiState.currentTheme.keywordColor else uiState.currentTheme.textColor
                                )
                            }

                            // AI Assistant Copilot
                            IconButton(
                                onClick = { viewModel.toggleAiDrawer() },
                                modifier = Modifier.testTag("ai_assistant_button")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (uiState.aiIsLoading) {
                                            Badge(containerColor = uiState.currentTheme.keywordColor) {
                                                Text("AI")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Assistant",
                                        tint = uiState.currentTheme.keywordColor
                                    )
                                }
                            }

                            // Save Icon
                            IconButton(
                                onClick = { viewModel.saveActiveFile() },
                                modifier = Modifier.testTag("save_file_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "Save",
                                    tint = if (uiState.isDirty) uiState.currentTheme.keywordColor else uiState.currentTheme.textColor.copy(alpha = 0.6f)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = uiState.currentTheme.lineNumberBg
                        )
                    )
                }
            },
            bottomBar = {
                if (!uiState.isZenMode) {
                    Column {
                        // Keyboard Developer Toolbar
                        KeyboardToolbar(
                            theme = uiState.currentTheme,
                            onInsertSymbol = { symbol ->
                                val current = uiState.activeFileContent
                                viewModel.updateContent(current + symbol)
                            },
                            onUndo = { viewModel.undo() },
                            onRedo = { viewModel.redo() },
                            onRunCode = { viewModel.runCode() },
                            onAiComplete = { viewModel.requestAiInlineCompletion() }
                        )

                        // VS Code Status Bar with Auto-Save Badge
                        StatusBarView(
                            projectName = uiState.activeProject?.name,
                            language = uiState.activeLanguage,
                            cursorLine = uiState.cursorLine,
                            cursorCol = uiState.cursorCol,
                            autoSaveStatus = uiState.autoSaveStatus,
                            theme = uiState.currentTheme,
                            onGitClick = { viewModel.toggleGitPanel() },
                            onVersionClick = { viewModel.toggleVersionHistory() },
                            onZenToggle = { viewModel.toggleZenMode() }
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (!uiState.isZenMode) innerPadding else PaddingValues(0.dp))
                    .background(uiState.currentTheme.background)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Main Editor or HTML Preview Canvas
                    Column(modifier = Modifier.weight(1f)) {

                        // Tab Bar (hidden in Zen Mode if requested)
                        if (!uiState.isZenMode) {
                            EditorTabRow(
                                openTabs = uiState.openTabs,
                                activeTabId = uiState.activeTabId,
                                isDirty = uiState.isDirty,
                                theme = uiState.currentTheme,
                                onSelectTab = { viewModel.openTab(it) },
                                onCloseTab = { viewModel.closeTab(it) },
                                onNewFileClick = { viewModel.toggleFileExplorer() }
                            )
                        } else {
                            // Zen Mode Exit Floating Indicator
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(uiState.currentTheme.lineNumberBg)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Zen Focus Mode Active",
                                    fontSize = 11.sp,
                                    color = uiState.currentTheme.keywordColor,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { viewModel.toggleZenMode() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Exit Zen", tint = Color.White)
                                }
                            }
                        }

                        // Search & Replace Bar if open
                        if (uiState.isSearchReplaceOpen) {
                            SearchReplaceBar(
                                theme = uiState.currentTheme,
                                onPerformReplace = { find, replace, all ->
                                    viewModel.performSearchReplace(find, replace, all)
                                },
                                onClose = { viewModel.toggleSearchReplace() }
                            )
                        }

                        // Content Body
                        if (uiState.isPreviewMode && (uiState.activeLanguage == "html" || uiState.activeLanguage == "htm")) {
                            HtmlPreviewView(
                                htmlContent = uiState.activeFileContent,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            CodeEditorView(
                                code = uiState.activeFileContent,
                                language = uiState.activeLanguage,
                                theme = uiState.currentTheme,
                                isPresentationMode = uiState.isPresentationMode,
                                onCodeChange = { viewModel.updateContent(it) },
                                onCursorPositionChange = { l, c -> viewModel.updateCursorPosition(l, c) },
                                modifier = Modifier.weight(1f)
                            )

                            // Terminal Panel at bottom
                            if (uiState.terminalOutput.isNotBlank() && !uiState.isZenMode) {
                                Surface(
                                    color = uiState.currentTheme.lineNumberBg,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 140.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "TERMINAL OUTPUT",
                                            color = uiState.currentTheme.lineNumberText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = uiState.terminalOutput,
                                            color = uiState.currentTheme.textColor,
                                            fontSize = 12.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // AI Copilot End Drawer
                    if (uiState.isAiDrawerOpen) {
                        AiAssistantDrawer(
                            theme = uiState.currentTheme,
                            isLoading = uiState.aiIsLoading,
                            chatMessages = uiState.aiChatMessages,
                            onSendUserMessage = { viewModel.sendAiUserMessage(it) },
                            onExplainCode = { viewModel.requestAiExplanation() },
                            onBugCheck = { viewModel.requestAiBugCheck() },
                            onRefactor = { viewModel.requestAiRefactoring() },
                            onDocstring = { viewModel.requestAiDocstring() },
                            onClose = { viewModel.toggleAiDrawer() }
                        )
                    }
                }
            }
        }
    }

    // Command Palette Dialog
    if (uiState.isCommandPaletteOpen) {
        CommandPaletteDialog(
            theme = uiState.currentTheme,
            onRunCode = { viewModel.runCode() },
            onAiExplanation = { viewModel.requestAiExplanation() },
            onAiBugCheck = { viewModel.requestAiBugCheck() },
            onAiRefactor = { viewModel.requestAiRefactoring() },
            onAiDocstring = { viewModel.requestAiDocstring() },
            onAiUnitTest = { viewModel.requestAiUnitTest() },
            onTogglePreview = { viewModel.togglePreviewMode() },
            onOpenGit = { viewModel.toggleGitPanel() },
            onOpenVersionHistory = { viewModel.toggleVersionHistory() },
            onToggleZen = { viewModel.toggleZenMode() },
            onTogglePresentation = { viewModel.togglePresentationMode() },
            onFormatJson = { viewModel.formatJson() },
            onOpenCsv = { viewModel.toggleCsvViewer() },
            onOpenDashboard = { viewModel.toggleDashboard() },
            onOpenAnalyzer = { viewModel.toggleCodeAnalyzer() },
            onOpenDatabase = { viewModel.toggleDatabaseInspector() },
            onOpenRepl = { viewModel.toggleRepl() },
            onSelectTheme = { viewModel.selectTheme(it) },
            onOpenSnippets = { viewModel.toggleSnippetPicker() },
            onOpenSearch = { viewModel.toggleSearchReplace() },
            onDismiss = { viewModel.toggleCommandPalette() }
        )
    }

    // Snippet Picker Dialog
    if (uiState.isSnippetPickerOpen) {
        SnippetPickerDialog(
            snippets = uiState.snippets,
            theme = uiState.currentTheme,
            onSelectSnippet = { viewModel.insertSnippet(it) },
            onDismiss = { viewModel.toggleSnippetPicker() }
        )
    }

    // Project Dashboard & Tasks Dialog
    if (uiState.isDashboardOpen) {
        ProjectDashboardDialog(
            projectName = uiState.activeProject?.name,
            projectFiles = uiState.projectFiles,
            activeContent = uiState.activeFileContent,
            todos = uiState.projectTodos,
            onAddTodo = { viewModel.addTodoItem(it) },
            onToggleTodo = { viewModel.toggleTodoItem(it) },
            onDismiss = { viewModel.toggleDashboard() }
        )
    }

    // Code Analyzer & Linter Dialog
    if (uiState.isCodeAnalyzerOpen) {
        CodeAnalyzerDialog(
            fileContent = uiState.activeFileContent,
            language = uiState.activeLanguage,
            onDismiss = { viewModel.toggleCodeAnalyzer() }
        )
    }

    // SQLite Database Inspector Dialog
    if (uiState.isDatabaseInspectorOpen) {
        DatabaseInspectorDialog(
            onDismiss = { viewModel.toggleDatabaseInspector() }
        )
    }

    // Python Interactive Console REPL Dialog
    if (uiState.isReplOpen) {
        PythonReplDialog(
            activeFileContent = uiState.activeFileContent,
            onDismiss = { viewModel.toggleRepl() }
        )
    }

    // File Version Snapshots Dialog
    if (uiState.isVersionHistoryOpen) {
        VersionHistoryDialog(
            fileVersions = uiState.fileVersions,
            onRestoreVersion = { viewModel.restoreVersion(it) },
            onDismiss = { viewModel.toggleVersionHistory() }
        )
    }

    // Git VCS Dialog
    if (uiState.isGitPanelOpen) {
        GitPanelDialog(
            activeProjectName = uiState.activeProject?.name,
            modifiedFiles = if (uiState.isDirty) uiState.projectFiles.filter { it.id == uiState.activeTabId } else emptyList(),
            commitHistory = uiState.commitHistory,
            onCommit = { viewModel.commitGitChanges(it) },
            onDismiss = { viewModel.toggleGitPanel() }
        )
    }

    // JSON Formatter Dialog
    if (uiState.isJsonFormatterOpen) {
        JsonFormatterDialog(
            onFormatJson = { viewModel.formatJson() },
            onDismiss = { viewModel.toggleJsonFormatter() }
        )
    }

    // CSV Viewer Dialog
    if (uiState.isCsvViewerOpen) {
        CsvViewerDialog(
            csvContent = uiState.activeFileContent,
            onDismiss = { viewModel.toggleCsvViewer() }
        )
    }
}
