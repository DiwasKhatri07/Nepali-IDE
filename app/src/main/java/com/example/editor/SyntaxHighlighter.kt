package com.example.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import java.util.regex.Pattern

object SyntaxHighlighter {

    private val PYTHON_KEYWORDS = setOf(
        "and", "as", "assert", "async", "await", "break", "class", "continue",
        "def", "del", "elif", "else", "except", "False", "finally", "for",
        "from", "global", "if", "import", "in", "is", "lambda", "None",
        "nonlocal", "not", "or", "pass", "raise", "return", "True", "try",
        "while", "with", "yield"
    )

    private val HTML_TAGS = setOf(
        "a", "abbr", "address", "area", "article", "aside", "audio", "b", "base",
        "bdi", "bdo", "blockquote", "body", "br", "button", "canvas", "caption",
        "cite", "code", "col", "colgroup", "data", "datalist", "dd", "del",
        "details", "dfn", "dialog", "div", "dl", "dt", "em", "embed", "fieldset",
        "figcaption", "figure", "footer", "form", "h1", "h2", "h3", "h4", "h5", "h6",
        "head", "header", "hr", "html", "i", "iframe", "img", "input", "ins",
        "kbd", "label", "legend", "li", "link", "main", "map", "mark", "meta",
        "meter", "nav", "noscript", "object", "ol", "optgroup", "option", "output",
        "p", "param", "picture", "pre", "progress", "q", "rp", "rt", "ruby", "s",
        "samp", "script", "section", "select", "small", "source", "span", "strong",
        "style", "sub", "summary", "sup", "table", "tbody", "td", "template",
        "textarea", "tfoot", "th", "thead", "time", "title", "tr", "track",
        "u", "ul", "var", "video", "wbr"
    )

    fun highlight(code: String, language: String, theme: CodeThemeColorScheme): AnnotatedString {
        if (code.isEmpty()) return AnnotatedString("")

        val lang = language.lowercase()
        return when {
            lang in listOf("py", "python") -> highlightPython(code, theme)
            lang in listOf("html", "htm", "xml") -> highlightHtml(code, theme)
            lang in listOf("css") -> highlightCss(code, theme)
            lang in listOf("js", "javascript") -> highlightJs(code, theme)
            else -> buildAnnotatedString { append(code) }
        }
    }

    private fun highlightPython(code: String, theme: CodeThemeColorScheme): AnnotatedString {
        return buildAnnotatedString {
            append(code)

            // Comments (# ...)
            highlightPattern(code, Pattern.compile("#.*"), theme.commentColor, isItalic = true)

            // Triple quotes / Docstrings
            highlightPattern(code, Pattern.compile("\"\"\"[\\s\\S]*?\"\"\"|'''[\\s\\S]*?'''"), theme.stringColor)

            // Strings ("..." or '...')
            highlightPattern(code, Pattern.compile("\"[^\"]*\"|'[^']*'"), theme.stringColor)

            // Numbers
            highlightPattern(code, Pattern.compile("\\b\\d+(\\.\\d+)?\\b"), theme.numberColor)

            // Functions (def function_name)
            val funcPattern = Pattern.compile("def\\s+([a-zA-Z_][a-zA-Z0-9_]*)")
            val funcMatcher = funcPattern.matcher(code)
            while (funcMatcher.find()) {
                addStyle(
                    SpanStyle(color = theme.functionColor, fontWeight = FontWeight.Bold),
                    funcMatcher.start(1),
                    funcMatcher.end(1)
                )
            }

            // Keywords
            val wordPattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")
            val wordMatcher = wordPattern.matcher(code)
            while (wordMatcher.find()) {
                val word = wordMatcher.group()
                if (PYTHON_KEYWORDS.contains(word)) {
                    addStyle(
                        SpanStyle(color = theme.keywordColor, fontWeight = FontWeight.Bold),
                        wordMatcher.start(),
                        wordMatcher.end()
                    )
                }
            }
        }
    }

    private fun highlightHtml(code: String, theme: CodeThemeColorScheme): AnnotatedString {
        return buildAnnotatedString {
            append(code)

            // Comments (<!-- ... -->)
            highlightPattern(code, Pattern.compile("<!--[\\s\\S]*?-->"), theme.commentColor, isItalic = true)

            // Tags (<...>)
            val tagPattern = Pattern.compile("</?([a-zA-Z0-9]+)[^>]*>")
            val tagMatcher = tagPattern.matcher(code)
            while (tagMatcher.find()) {
                val tagName = tagMatcher.group(1)?.lowercase()
                if (tagName != null) {
                    val start = tagMatcher.start(1)
                    val end = tagMatcher.end(1)
                    addStyle(
                        SpanStyle(color = theme.tagColor, fontWeight = FontWeight.Bold),
                        start,
                        end
                    )
                }
            }

            // Strings inside tags ("..." or '...')
            highlightPattern(code, Pattern.compile("\"[^\"]*\"|'[^']*'"), theme.stringColor)

            // Attributes (name=)
            val attrPattern = Pattern.compile("\\s+([a-zA-Z0-9_-]+)=")
            val attrMatcher = attrPattern.matcher(code)
            while (attrMatcher.find()) {
                addStyle(
                    SpanStyle(color = theme.attributeColor),
                    attrMatcher.start(1),
                    attrMatcher.end(1)
                )
            }
        }
    }

    private fun highlightCss(code: String, theme: CodeThemeColorScheme): AnnotatedString {
        return buildAnnotatedString {
            append(code)

            // Comments (/* ... */)
            highlightPattern(code, Pattern.compile("/\\*[\\s\\S]*?\\*/"), theme.commentColor, isItalic = true)

            // Selectors (.class or #id or element)
            highlightPattern(code, Pattern.compile("[.#]?[a-zA-Z0-9_-]+(?=\\s*\\{)"), theme.tagColor, isBold = true)

            // Properties (color:, background:, etc)
            highlightPattern(code, Pattern.compile("[a-zA-Z0-9_-]+(?=\\s*:)"), theme.attributeColor)

            // Values / Numbers
            highlightPattern(code, Pattern.compile(":[^;]+"), theme.stringColor)
        }
    }

    private fun highlightJs(code: String, theme: CodeThemeColorScheme): AnnotatedString {
        val jsKeywords = setOf(
            "const", "let", "var", "function", "return", "if", "else", "for", "while",
            "switch", "case", "break", "import", "export", "from", "default", "class", "async", "await"
        )
        return buildAnnotatedString {
            append(code)

            // Comments (// ... or /* ... */)
            highlightPattern(code, Pattern.compile("//.*|/\\*[\\s\\S]*?\\*/"), theme.commentColor, isItalic = true)

            // Strings
            highlightPattern(code, Pattern.compile("\"[^\"]*\"|'[^']*'|`[^`]*`"), theme.stringColor)

            // Numbers
            highlightPattern(code, Pattern.compile("\\b\\d+(\\.\\d+)?\\b"), theme.numberColor)

            // Keywords
            val wordPattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")
            val wordMatcher = wordPattern.matcher(code)
            while (wordMatcher.find()) {
                val word = wordMatcher.group()
                if (jsKeywords.contains(word)) {
                    addStyle(
                        SpanStyle(color = theme.keywordColor, fontWeight = FontWeight.Bold),
                        wordMatcher.start(),
                        wordMatcher.end()
                    )
                }
            }
        }
    }

    private fun AnnotatedString.Builder.highlightPattern(
        text: String,
        pattern: Pattern,
        color: androidx.compose.ui.graphics.Color,
        isBold: Boolean = false,
        isItalic: Boolean = false
    ) {
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            addStyle(
                SpanStyle(
                    color = color,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                ),
                matcher.start(),
                matcher.end()
            )
        }
    }
}
