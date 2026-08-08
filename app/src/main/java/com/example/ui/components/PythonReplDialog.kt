package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interpreter.PythonInterpreter

data class ReplLogEntry(
    val prompt: String,
    val stdout: String,
    val stderr: String,
    val executionTimeMs: Long,
    val isError: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PythonReplDialog(
    activeFileContent: String,
    onDismiss: () -> Unit
) {
    val session = remember { PythonInterpreter.createSession() }
    val logs = remember { mutableStateListOf<ReplLogEntry>() }
    var inputText by remember { mutableStateOf("") }
    var historyIndex by remember { mutableStateOf(-1) }
    var showVariables by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    // Add initial welcome banner
    LaunchedEffect(Unit) {
        if (logs.isEmpty()) {
            logs.add(
                ReplLogEntry(
                    prompt = "# Python 3.11.0 Interactive REPL Environment",
                    stdout = "Type Python expressions or commands below. Active editor variables can be loaded using 'Load Active Script'.",
                    stderr = "",
                    executionTimeMs = 0
                )
            )
        }
    }

    fun submitCommand(cmd: String) {
        if (cmd.isBlank()) return
        val result = session.evaluate(cmd)
        logs.add(
            ReplLogEntry(
                prompt = cmd,
                stdout = result.stdout,
                stderr = result.stderr,
                executionTimeMs = result.executionTimeMs,
                isError = result.exitCode != 0
            )
        )
        inputText = ""
        historyIndex = -1
    }

    fun loadActiveFile() {
        if (activeFileContent.isBlank()) return
        val result = session.loadScript(activeFileContent)
        logs.add(
            ReplLogEntry(
                prompt = "# Loaded Active Script into REPL Context",
                stdout = result.stdout.ifBlank { "Script executed cleanly into session scope." },
                stderr = result.stderr,
                executionTimeMs = result.executionTimeMs,
                isError = result.exitCode != 0
            )
        )
    }

    val quickCommands = listOf(
        "print('Hello REPL')",
        "x = 42",
        "numbers = [1, 2, 3, 4, 5]",
        "sum(numbers)",
        "len(numbers)",
        "math.sqrt(144)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Terminal, contentDescription = "Python REPL", tint = MaterialTheme.colorScheme.primary) },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Python Console REPL", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.2f)
                ) {
                    Text(
                        "Python 3.11",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                // Top Action Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { loadActiveFile() },
                        label = { Text("Load Active Script", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )

                    Row {
                        IconButton(
                            onClick = {
                                logs.clear()
                                logs.add(ReplLogEntry("# Console Cleared", "", "", 0))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                session.reset()
                                logs.clear()
                                logs.add(ReplLogEntry("# Session Reset", "Scope variables cleared.", "", 0))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Session", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Variables Scope Inspector Header
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth().clickable { showVariables = !showVariables }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DataArray, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Session Scope Variables (${session.variables.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(if (showVariables) "Hide" else "Show", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                if (showVariables && session.variables.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                    ) {
                        session.variables.forEach { (k, v) ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = "$k: ${v.toString().take(20)}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ReplConsole(
                    session = session,
                    logs = logs,
                    onExecuteCommand = { submitCommand(it) },
                    modifier = Modifier.weight(1f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close Console")
            }
        }
    )
}
