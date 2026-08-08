package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CodeFileEntity
import com.example.editor.CodeThemeColorScheme

@Composable
fun EditorTabRow(
    openTabs: List<CodeFileEntity>,
    activeTabId: Long?,
    isDirty: Boolean,
    theme: CodeThemeColorScheme,
    onSelectTab: (CodeFileEntity) -> Unit,
    onCloseTab: (Long) -> Unit,
    onNewFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = theme.lineNumberBg,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(openTabs, key = { it.id }) { tab ->
                    val isActive = tab.id == activeTabId
                    TabItem(
                        file = tab,
                        isActive = isActive,
                        isDirty = isActive && isDirty,
                        theme = theme,
                        onClick = { onSelectTab(tab) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }
            }

            IconButton(
                onClick = onNewFileClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("add_file_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New File",
                    tint = theme.textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    file: CodeFileEntity,
    isActive: Boolean,
    isDirty: Boolean,
    theme: CodeThemeColorScheme,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    val bg = if (isActive) theme.background else theme.lineNumberBg
    val textTint = if (isActive) theme.textColor else theme.textColor.copy(alpha = 0.6f)
    val ext = file.extension.lowercase()

    val extColor = when (ext) {
        "py" -> Color(0xFF38BDF8)   // Sky Blue
        "html" -> Color(0xFFFB923C) // Orange
        "css" -> Color(0xFF818CF8)  // Indigo
        "js" -> Color(0xFFFACC15)   // Yellow
        else -> theme.attributeColor
    }

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("tab_${file.name}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(extColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = ext.uppercase(),
                    color = extColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = file.name,
                color = textTint,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (isDirty) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(theme.keywordColor, RoundedCornerShape(4.dp))
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Tab",
                    tint = textTint,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
