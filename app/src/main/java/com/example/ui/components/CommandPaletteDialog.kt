package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.CodeThemeColorScheme
import com.example.editor.EditorThemes

data class CommandItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandPaletteDialog(
    theme: CodeThemeColorScheme,
    onRunCode: () -> Unit,
    onAiExplanation: () -> Unit,
    onAiBugCheck: () -> Unit,
    onAiRefactor: () -> Unit,
    onAiDocstring: () -> Unit,
    onAiUnitTest: () -> Unit,
    onTogglePreview: () -> Unit,
    onOpenGit: () -> Unit,
    onOpenVersionHistory: () -> Unit,
    onToggleZen: () -> Unit,
    onTogglePresentation: () -> Unit,
    onFormatJson: () -> Unit,
    onOpenCsv: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenAnalyzer: () -> Unit,
    onOpenDatabase: () -> Unit,
    onOpenRepl: () -> Unit,
    onSelectTheme: (CodeThemeColorScheme) -> Unit,
    onOpenSnippets: () -> Unit,
    onOpenSearch: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val commands = remember {
        listOf(
            CommandItem("Run Code / Execute Script", "Runs Python script or renders HTML preview", Icons.Default.PlayArrow, onRunCode),
            CommandItem("Python Console REPL (Interactive Shell)", "Stateful interactive Python shell & scope inspector", Icons.Default.Terminal, onOpenRepl),
            CommandItem("Project Dashboard & Tasks", "View code metrics, line stats & task list", Icons.Default.Dashboard, onOpenDashboard),
            CommandItem("Code Quality & Linter Analyzer", "Cyclomatic complexity & PEP 8 scanner", Icons.Default.Analytics, onOpenAnalyzer),
            CommandItem("SQLite Database Browser", "Inspect tables and execute SQL queries", Icons.Default.Storage, onOpenDatabase),
            CommandItem("Git: Control Center & Staging", "Commit changes, view branch log & diffs", Icons.Default.Commit, onOpenGit),
            CommandItem("File Version History & Snapshots", "Restore previous auto-saved file versions", Icons.Default.History, onOpenVersionHistory),
            CommandItem("AI: Generate Unit Tests", "Generates complete unit tests for active file", Icons.Default.ChecklistRtl, onAiUnitTest),
            CommandItem("AI: Explain Selected Code", "Generates detailed code explanation", Icons.Default.AutoAwesome, onAiExplanation),
            CommandItem("AI: Static Bug Analysis", "Scans file for syntax errors and bugs", Icons.Default.BugReport, onAiBugCheck),
            CommandItem("AI: Suggest Code Refactoring", "Modernizes code structure and efficiency", Icons.Default.Psychology, onAiRefactor),
            CommandItem("AI: Generate Docstrings", "Generates documentation comments", Icons.Default.Description, onAiDocstring),
            CommandItem("Zen Mode (Distraction-Free)", "Hide UI bars for full-screen focus", Icons.Default.Fullscreen, onToggleZen),
            CommandItem("Presentation Mode (Large Text)", "Increase font size for live presentation", Icons.Default.PresentToAll, onTogglePresentation),
            CommandItem("JSON: Format & Validate", "Pretty-print JSON document", Icons.Default.DataObject, onFormatJson),
            CommandItem("CSV: Open Table Inspector", "Render CSV file in structured grid view", Icons.Default.TableChart, onOpenCsv),
            CommandItem("Toggle HTML Live Preview", "Switch between code editor & preview", Icons.Default.Visibility, onTogglePreview),
            CommandItem("Insert Code Snippet", "Browse HTML, CSS, Python templates", Icons.Default.Code, onOpenSnippets),
            CommandItem("Find and Replace in File", "Search text with regex support", Icons.Default.Search, onOpenSearch),
            CommandItem("Theme: Sophisticated Dark", "Aesthetic dark IDE scheme", Icons.Default.Palette) { onSelectTheme(EditorThemes.SophisticatedDark) },
            CommandItem("Theme: VS Code Dark+", "Default dark editor scheme", Icons.Default.Palette) { onSelectTheme(EditorThemes.VsCodeDarkPlus) },
            CommandItem("Theme: Monokai", "Vibrant classic theme", Icons.Default.Palette) { onSelectTheme(EditorThemes.Monokai) },
            CommandItem("Theme: One Dark Pro", "Atom / VS Code dark scheme", Icons.Default.Palette) { onSelectTheme(EditorThemes.OneDarkPro) },
            CommandItem("Theme: Nord", "Arctic blue dark scheme", Icons.Default.Palette) { onSelectTheme(EditorThemes.Nord) },
            CommandItem("Theme: Dracula", "Gothic purple dark scheme", Icons.Default.Palette) { onSelectTheme(EditorThemes.Dracula) },
            CommandItem("Theme: Synthwave '84", "Retro neon palette", Icons.Default.Palette) { onSelectTheme(EditorThemes.Synthwave84) },
            CommandItem("Theme: Tokyo Night", "Vibrant neon dark palette", Icons.Default.Palette) { onSelectTheme(EditorThemes.TokyoNight) },
            CommandItem("Theme: Solarized Light", "High legibility light theme", Icons.Default.Palette) { onSelectTheme(EditorThemes.SolarizedLight) },
            CommandItem("Theme: GitHub Light", "Clean GitHub light scheme", Icons.Default.Palette) { onSelectTheme(EditorThemes.GitHubLight) },
            CommandItem("Theme: Cyberpunk", "Neon magenta & teal theme", Icons.Default.Palette) { onSelectTheme(EditorThemes.Cyberpunk) }
        )
    }

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) commands
        else commands.filter { it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("command_palette_dialog")
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.lineNumberBg,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Command Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Type a command or search feature...") },
                    leadingIcon = { Icon(Icons.Default.Terminal, contentDescription = null, tint = theme.keywordColor) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("command_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.heightIn(max = 320.dp)
                ) {
                    items(filtered) { cmd ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(theme.background, RoundedCornerShape(8.dp))
                                .clickable {
                                    cmd.action()
                                    onDismiss()
                                }
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = cmd.icon,
                                contentDescription = null,
                                tint = theme.keywordColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = cmd.title,
                                    color = theme.textColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = cmd.description,
                                    color = theme.lineNumberText,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
