package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CodeAnalyzerDialog(
    fileContent: String,
    language: String,
    onDismiss: () -> Unit
) {
    val lines = fileContent.lines()
    val lineCount = lines.size
    val functionCount = lines.count { it.contains("def ") || it.contains("function ") || it.contains("fun ") }
    val ifCount = lines.count { it.contains("if ") || it.contains("elif ") || it.contains("else:") }
    val loopCount = lines.count { it.contains("for ") || it.contains("while ") }
    val cyclomaticComplexity = 1 + ifCount + loopCount

    val linterIssues = mutableListOf<String>()
    if (language == "py" || language == "python") {
        lines.forEachIndexed { idx, line ->
            if (line.length > 79) linterIssues.add("Line ${idx + 1}: PEP 8 Line length exceeds 79 characters (${line.length} chars)")
            if (line.contains("import *")) linterIssues.add("Line ${idx + 1}: Wildcard import detected 'import *'")
            if (line.contains("print(") && !line.contains("#")) linterIssues.add("Line ${idx + 1}: Leftover debug print statement")
        }
    }
    if (linterIssues.isEmpty()) {
        linterIssues.add("No critical style or linter errors found!")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Analytics, contentDescription = "Code Quality", tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Code Analyzer & Linter", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Text(
                    "Real-Time Quality & Complexity Metrics",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Complexity", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$cyclomaticComplexity", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (cyclomaticComplexity > 10) Color(0xFFEF4444) else Color(0xFF10B981))
                            Text(if (cyclomaticComplexity <= 5) "Low Risk" else "Moderate Risk", fontSize = 9.sp)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Functions", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$functionCount", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Definitions", fontSize = 9.sp)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Loops/Branches", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${ifCount + loopCount}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Control Flow", fontSize = 9.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Static Linter & PEP 8 Scanner", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(linterIssues) { issue ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (issue.startsWith("No")) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (issue.startsWith("No")) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (issue.startsWith("No")) Color(0xFF10B981) else Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    issue,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
