package com.example.data

import kotlinx.coroutines.flow.Flow

class WorkspaceRepository(
    private val projectDao: ProjectDao,
    private val codeFileDao: CodeFileDao,
    private val snippetDao: SnippetDao,
    private val fileVersionDao: FileVersionDao
) {
    val projects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val snippets: Flow<List<SnippetEntity>> = snippetDao.getAllSnippets()

    fun getFilesForProject(projectId: Long): Flow<List<CodeFileEntity>> {
        return codeFileDao.getFilesForProject(projectId)
    }

    fun getVersionsForFile(fileId: Long): Flow<List<FileVersionEntity>> {
        return fileVersionDao.getVersionsForFile(fileId)
    }

    suspend fun saveFileVersion(fileId: Long, content: String, commitMessage: String = "Auto-save snapshot"): Long {
        return fileVersionDao.insertVersion(
            FileVersionEntity(
                fileId = fileId,
                content = content,
                commitMessage = commitMessage,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun getFileById(fileId: Long): CodeFileEntity? {
        return codeFileDao.getFileById(fileId)
    }

    suspend fun createProject(name: String, description: String = ""): Long {
        val projId = projectDao.insertProject(
            ProjectEntity(name = name, description = description)
        )
        // Add a default main file depending on project type
        val defaultName = if (name.lowercase().contains("web") || name.lowercase().contains("html")) "index.html" else "main.py"
        val defaultExt = if (defaultName.endsWith(".html")) "html" else "py"
        val defaultContent = if (defaultExt == "html") "<!-- New HTML File -->\n<h1>Hello World</h1>" else "# New Python Script\nprint('Hello World')"
        
        codeFileDao.insertFile(
            CodeFileEntity(
                projectId = projId,
                name = defaultName,
                path = defaultName,
                extension = defaultExt,
                content = defaultContent
            )
        )
        return projId
    }

    suspend fun updateProject(project: ProjectEntity) {
        projectDao.updateProject(project)
    }

    suspend fun deleteProject(project: ProjectEntity) {
        codeFileDao.deleteFilesForProject(project.id)
        projectDao.deleteProject(project)
    }

    suspend fun createFile(projectId: Long, name: String, extension: String, content: String = ""): Long {
        return codeFileDao.insertFile(
            CodeFileEntity(
                projectId = projectId,
                name = name,
                path = name,
                extension = extension,
                content = content
            )
        )
    }

    suspend fun updateFile(file: CodeFileEntity) {
        codeFileDao.updateFile(file.copy(lastModified = System.currentTimeMillis()))
    }

    suspend fun deleteFile(file: CodeFileEntity) {
        codeFileDao.deleteFile(file)
    }

    suspend fun addSnippet(title: String, language: String, prefix: String, code: String, description: String = "") {
        snippetDao.insertSnippet(
            SnippetEntity(
                title = title,
                language = language,
                prefix = prefix,
                code = code,
                description = description
            )
        )
    }
}
