package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.CodeThemeColorScheme

@Composable
fun KeyboardToolbar(
    theme: CodeThemeColorScheme,
    onInsertSymbol: (String) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onRunCode: () -> Unit,
    onAiComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickSymbols = listOf(
        "    ", "def ", "class ", "=", "(", ")", "{", "}", "[", "]",
        ":", ";", "\"", "'", "<", ">", "+", "-", "*", "/", "_", ",", "."
    )

    Surface(
        color = theme.lineNumberBg,
        tonalElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            // Undo & Redo
            IconButton(
                onClick = onUndo,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo",
                    tint = theme.textColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = onRedo,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Redo,
                    contentDescription = "Redo",
                    tint = theme.textColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Quick Symbols Row
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(quickSymbols) { symbol ->
                    val label = if (symbol == "    ") "TAB" else symbol
                    Box(
                        modifier = Modifier
                            .background(theme.currentLineBg, RoundedCornerShape(6.dp))
                            .clickable { onInsertSymbol(symbol) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("key_$label")
                    ) {
                        Text(
                            text = label,
                            color = theme.textColor,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // AI Quick Complete
            IconButton(
                onClick = onAiComplete,
                modifier = Modifier
                    .size(34.dp)
                    .background(theme.keywordColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .testTag("ai_complete_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Completion",
                    tint = theme.keywordColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Run Button
            IconButton(
                onClick = onRunCode,
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(0xFF22C55E), RoundedCornerShape(8.dp))
                    .testTag("run_code_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Run Code",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
