package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CodeFileEntity
import com.example.data.ProjectEntity
import com.example.editor.CodeThemeColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileExplorerDrawer(
    projects: List<ProjectEntity>,
    activeProject: ProjectEntity?,
    files: List<CodeFileEntity>,
    theme: CodeThemeColorScheme,
    onSelectProject: (ProjectEntity) -> Unit,
    onSelectFile: (CodeFileEntity) -> Unit,
    onCreateFile: (String) -> Unit,
    onDeleteFile: (CodeFileEntity) -> Unit,
    onCreateProject: (name: String, desc: String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateFileDialog by remember { mutableStateOf(false) }
    var showCreateProjectDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }
    var newProjName by remember { mutableStateOf("") }
    var newProjDesc by remember { mutableStateOf("") }

    Surface(
        color = theme.lineNumberBg,
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Workspace Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "EXPLORER",
                    color = theme.textColor.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row {
                    IconButton(
                        onClick = { showCreateProjectDialog = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "New Project",
                            tint = theme.keywordColor
                        )
                    }

                    IconButton(
                        onClick = { showCreateFileDialog = true },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("create_file_drawer_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New File",
                            tint = theme.keywordColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Project Picker Dropdown / Chips
            Text(
                text = activeProject?.name ?: "No Workspace Selected",
                color = theme.textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Project Selector Chips
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    Text(
                        text = "FILES IN WORKSPACE",
                        color = theme.lineNumberText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(files, key = { it.id }) { file ->
                    FileTreeItem(
                        file = file,
                        theme = theme,
                        onClick = { onSelectFile(file) },
                        onDelete = { onDeleteFile(file) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "RECENT WORKSPACES",
                        color = theme.lineNumberText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(projects, key = { it.id }) { proj ->
                    val isSelected = proj.id == activeProject?.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) theme.currentLineBg else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onSelectProject(proj) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = if (isSelected) theme.keywordColor else theme.lineNumberText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = proj.name,
                                color = if (isSelected) theme.textColor else theme.lineNumberText,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog: Create File
    if (showCreateFileDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFileDialog = false },
            title = { Text("Create New File") },
            text = {
                OutlinedTextField(
                    value = newFileName,
                    onValueChange = { newFileName = it },
                    label = { Text("Filename (e.g. script.py, index.html)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            onCreateFile(newFileName)
                            newFileName = ""
                            showCreateFileDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Create Project
    if (showCreateProjectDialog) {
        AlertDialog(
            onDismissRequest = { showCreateProjectDialog = false },
            title = { Text("Create New Workspace") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newProjName,
                        onValueChange = { newProjName = it },
                        label = { Text("Workspace Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newProjDesc,
                        onValueChange = { newProjDesc = it },
                        label = { Text("Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProjName.isNotBlank()) {
                            onCreateProject(newProjName, newProjDesc)
                            newProjName = ""
                            newProjDesc = ""
                            showCreateProjectDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateProjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FileTreeItem(
    file: CodeFileEntity,
    theme: CodeThemeColorScheme,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val ext = file.extension.lowercase()
    val iconColor = when (ext) {
        "py" -> Color(0xFF38BDF8)
        "html" -> Color(0xFFFB923C)
        "css" -> Color(0xFF818CF8)
        "js" -> Color(0xFFFACC15)
        else -> theme.textColor.copy(alpha = 0.7f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("file_item_${file.name}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.InsertDriveFile,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = file.name,
                color = theme.textColor,
                fontSize = 13.sp
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete File",
                tint = theme.lineNumberText,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
