package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.CodeThemeColorScheme
import com.example.editor.SyntaxHighlighter

@Composable
fun CodeEditorView(
    code: String,
    language: String,
    theme: CodeThemeColorScheme,
    isPresentationMode: Boolean = false,
    onCodeChange: (String) -> Unit,
    onCursorPositionChange: (line: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember(code) {
        mutableStateOf(TextFieldValue(text = code, selection = TextRange(code.length)))
    }

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    val editorFontSize = if (isPresentationMode) 18.sp else 13.sp
    val editorLineHeight = if (isPresentationMode) 26.sp else 20.sp

    // Calculate line count and line number string
    val lines = textFieldValue.text.lines()
    val lineCount = maxOf(lines.size, 1)

    // Calculate current cursor line & col
    LaunchedEffect(textFieldValue.selection) {
        val cursorIndex = textFieldValue.selection.start
        var currentLength = 0
        var currentLine = 1
        var currentCol = 1

        for ((index, line) in lines.withIndex()) {
            val lineLengthWithNewline = line.length + 1
            if (cursorIndex < currentLength + lineLengthWithNewline) {
                currentLine = index + 1
                currentCol = maxOf(1, cursorIndex - currentLength + 1)
                break
            }
            currentLength += lineLengthWithNewline
        }
        onCursorPositionChange(currentLine, currentCol)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScroll)
        ) {
            // Line Number Gutter
            Column(
                modifier = Modifier
                    .background(theme.lineNumberBg)
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                for (i in 1..lineCount) {
                    Text(
                        text = i.toString(),
                        color = theme.lineNumberText,
                        fontSize = editorFontSize,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = editorLineHeight
                    )
                }
            }

            // Code Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScroll)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        val processedText = handleAutoBrackets(textFieldValue.text, newValue.text, newValue.selection.start)
                        val finalValue = if (processedText != newValue.text) {
                            newValue.copy(text = processedText)
                        } else newValue

                        textFieldValue = finalValue
                        onCodeChange(finalValue.text)
                    },
                    textStyle = TextStyle(
                        color = theme.textColor,
                        fontSize = editorFontSize,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = editorLineHeight
                    ),
                    cursorBrush = SolidColor(theme.keywordColor),
                    visualTransformation = { text ->
                        val highlighted = SyntaxHighlighter.highlight(text.text, language, theme)
                        TransformedText(highlighted, OffsetMapping.Identity)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 400.dp)
                        .testTag("code_text_input")
                )
            }
        }
    }
}

/**
 * Handles automatic bracket and quote auto-closing for mobile editor productivity
 */
private fun handleAutoBrackets(oldText: String, newText: String, cursorIndex: Int): String {
    if (newText.length <= oldText.length) return newText
    val addedChar = newText.getOrNull(cursorIndex - 1) ?: return newText

    val closingPair = when (addedChar) {
        '(' -> ")"
        '[' -> "]"
        '{' -> "}"
        '"' -> "\""
        '\'' -> "'"
        '<' -> ">"
        else -> null
    }

    if (closingPair != null) {
        val before = newText.substring(0, cursorIndex)
        val after = newText.substring(cursorIndex)
        return before + closingPair + after
    }
    return newText
}
