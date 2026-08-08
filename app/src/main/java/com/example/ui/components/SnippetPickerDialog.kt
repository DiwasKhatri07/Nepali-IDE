package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SnippetEntity
import com.example.editor.CodeThemeColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetPickerDialog(
    snippets: List<SnippetEntity>,
    theme: CodeThemeColorScheme,
    onSelectSnippet: (SnippetEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("snippet_picker_dialog")
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.lineNumberBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Insert Code Snippet",
                    color = theme.textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(snippets) { snippet ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.background,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectSnippet(snippet) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = snippet.title,
                                        color = theme.keywordColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = snippet.prefix,
                                        color = theme.lineNumberText,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                if (snippet.description.isNotBlank()) {
                                    Text(
                                        text = snippet.description,
                                        color = theme.textColor.copy(alpha = 0.7f),
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
}
