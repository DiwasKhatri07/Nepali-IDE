package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.CodeThemeColorScheme

@Composable
fun StatusBarView(
    projectName: String?,
    language: String,
    cursorLine: Int,
    cursorCol: Int,
    autoSaveStatus: String,
    theme: CodeThemeColorScheme,
    onGitClick: () -> Unit,
    onVersionClick: () -> Unit,
    onZenToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barBg = if (theme.isDark) Color(0xFF4F46E5) else Color(0xFF007ACC)

    val statusDotColor = when (autoSaveStatus) {
        "Saved" -> Color(0xFF10B981) // Green
        "Saving..." -> Color(0xFF38BDF8) // Blue
        else -> Color(0xFFF59E0B) // Amber
    }

    Surface(
        color = barBg,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Git Branch / Panel Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onGitClick() }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Commit, contentDescription = "Git VCS", tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "main",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // File Version History Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onVersionClick() }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.History, contentDescription = "History", tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "History",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "Ln $cursorLine, Col $cursorCol",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Auto-save status indicator badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(statusDotColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = autoSaveStatus,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Zen Mode Toggle
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Zen Mode",
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onZenToggle() }
                )

                Text(
                    text = language.uppercase(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

