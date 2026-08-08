package com.example.editor

import androidx.compose.ui.graphics.Color

data class CodeThemeColorScheme(
    val name: String,
    val isDark: Boolean,
    val background: Color,
    val lineNumberBg: Color,
    val lineNumberText: Color,
    val currentLineBg: Color,
    val textColor: Color,
    val keywordColor: Color,
    val stringColor: Color,
    val commentColor: Color,
    val numberColor: Color,
    val functionColor: Color,
    val tagColor: Color,
    val attributeColor: Color,
    val selectionBg: Color
)

object EditorThemes {
    val SophisticatedDark = CodeThemeColorScheme(
        name = "Sophisticated Dark",
        isDark = true,
        background = Color(0xFF0F1115),
        lineNumberBg = Color(0xFF0D0F12),
        lineNumberText = Color(0xFF475569),
        currentLineBg = Color(0xFF1E2229),
        textColor = Color(0xFFE2E8F0),
        keywordColor = Color(0xFFC084FC),
        stringColor = Color(0xFFFBBF24),
        commentColor = Color(0xFF64748B),
        numberColor = Color(0xFF38BDF8),
        functionColor = Color(0xFF4ADE80),
        tagColor = Color(0xFF60A5FA),
        attributeColor = Color(0xFF818CF8),
        selectionBg = Color(0xFF312E81)
    )

    val VsCodeDarkPlus = CodeThemeColorScheme(
        name = "VS Code Dark+",
        isDark = true,
        background = Color(0xFF1E1E1E),
        lineNumberBg = Color(0xFF181818),
        lineNumberText = Color(0xFF858585),
        currentLineBg = Color(0xFF282828),
        textColor = Color(0xFFD4D4D4),
        keywordColor = Color(0xFF569CD6), // Blue
        stringColor = Color(0xFFCE9178),  // Light Orange/Brown
        commentColor = Color(0xFF6A9955), // Green
        numberColor = Color(0xFFB5CEA8),  // Light Green
        functionColor = Color(0xDCDCAA),// Yellow
        tagColor = Color(0xFF569CD6),     // Blue
        attributeColor = Color(0xFF9CDCFE),// Light Blue
        selectionBg = Color(0xFF264F78)
    )

    val Monokai = CodeThemeColorScheme(
        name = "Monokai",
        isDark = true,
        background = Color(0xFF272822),
        lineNumberBg = Color(0xFF1E1F1C),
        lineNumberText = Color(0xFF90908A),
        currentLineBg = Color(0xFF3E3D32),
        textColor = Color(0xFFF8F8F2),
        keywordColor = Color(0xFFF92672), // Pink/Red
        stringColor = Color(0xE6DB74),  // Yellow
        commentColor = Color(0xFF75715E), // Grey
        numberColor = Color(0xFFAE81FF),  // Purple
        functionColor = Color(0xFFA6E22E),// Green
        tagColor = Color(0xFFF92672),     // Pink
        attributeColor = Color(0xFF66D9EF),// Cyan
        selectionBg = Color(0xFF49483E)
    )

    val OneDarkPro = CodeThemeColorScheme(
        name = "One Dark Pro",
        isDark = true,
        background = Color(0xFF21252B),
        lineNumberBg = Color(0xFF1B1D23),
        lineNumberText = Color(0xFF636D83),
        currentLineBg = Color(0xFF2C313C),
        textColor = Color(0xFFABB2BF),
        keywordColor = Color(0xFFC678DD), // Purple
        stringColor = Color(0xFF98C379),  // Green
        commentColor = Color(0xFF5C6370), // Grey
        numberColor = Color(0xFFD19A66),  // Dark Yellow
        functionColor = Color(0xFF61AFEF),// Blue
        tagColor = Color(0xFFE06C75),     // Red
        attributeColor = Color(0xFFD19A66),// Orange
        selectionBg = Color(0xFF3E4451)
    )

    val SolarizedLight = CodeThemeColorScheme(
        name = "Solarized Light",
        isDark = false,
        background = Color(0xFFFDF6E3),
        lineNumberBg = Color(0xFFEEE8D5),
        lineNumberText = Color(0xFF93A1A1),
        currentLineBg = Color(0xFFEEE8D5),
        textColor = Color(0xFF657B83),
        keywordColor = Color(0xFF859900), // Green
        stringColor = Color(0xFF2AA198),  // Cyan
        commentColor = Color(0xFF93A1A1), // Grey
        numberColor = Color(0xFFD33682),  // Magenta
        functionColor = Color(0xFF268BD2),// Blue
        tagColor = Color(0xFFCB4B16),     // Orange
        attributeColor = Color(0xFFB58900),// Yellow
        selectionBg = Color(0xFFEEE8D5)
    )

    val Cyberpunk = CodeThemeColorScheme(
        name = "Cyberpunk",
        isDark = true,
        background = Color(0xFF0F111A),
        lineNumberBg = Color(0xFF090A10),
        lineNumberText = Color(0xFF4C5270),
        currentLineBg = Color(0xFF1B1E2E),
        textColor = Color(0xFFE2E8F0),
        keywordColor = Color(0xFFFF2A85), // Neon Magenta
        stringColor = Color(0xFF00F5D4),  // Neon Turquoise
        commentColor = Color(0xFF61617D), // Muted Purple
        numberColor = Color(0xFFFFB703),  // Neon Amber
        functionColor = Color(0xFF7209B7),// Deep Purple
        tagColor = Color(0xFFFF2A85),     // Magenta
        attributeColor = Color(0xFF3A86FF),// Neon Blue
        selectionBg = Color(0xFF2D3250)
    )

    val Nord = CodeThemeColorScheme(
        name = "Nord",
        isDark = true,
        background = Color(0xFF2E3440),
        lineNumberBg = Color(0xFF242933),
        lineNumberText = Color(0xFF4C566A),
        currentLineBg = Color(0xFF3B4252),
        textColor = Color(0xD8DEE9),
        keywordColor = Color(0xFF81A1C1),
        stringColor = Color(0xFFA3BE8C),
        commentColor = Color(0xFF616E88),
        numberColor = Color(0xFFB48EAD),
        functionColor = Color(0xFF88C0D0),
        tagColor = Color(0xFF81A1C1),
        attributeColor = Color(0xFFD08770),
        selectionBg = Color(0xFF434C5E)
    )

    val Dracula = CodeThemeColorScheme(
        name = "Dracula",
        isDark = true,
        background = Color(0xFF282A36),
        lineNumberBg = Color(0xFF21222C),
        lineNumberText = Color(0xFF6272A4),
        currentLineBg = Color(0xFF44475A),
        textColor = Color(0xFFF8F8F2),
        keywordColor = Color(0xFFFF79C6),
        stringColor = Color(0xFFF1FA8C),
        commentColor = Color(0xFF6272A4),
        numberColor = Color(0xFFBD93F9),
        functionColor = Color(0xFF50FA7B),
        tagColor = Color(0xFFFF79C6),
        attributeColor = Color(0xFF8BE9FD),
        selectionBg = Color(0xFF44475A)
    )

    val Synthwave84 = CodeThemeColorScheme(
        name = "Synthwave '84",
        isDark = true,
        background = Color(0xFF262335),
        lineNumberBg = Color(0xFF1E1C2A),
        lineNumberText = Color(0xFF605A70),
        currentLineBg = Color(0xFF342E48),
        textColor = Color(0xFF36F9F6),
        keywordColor = Color(0xFFFEDE5D),
        stringColor = Color(0xFFFF7ED8),
        commentColor = Color(0xFF848BB8),
        numberColor = Color(0xFFF92aad),
        functionColor = Color(0xFF72F1B8),
        tagColor = Color(0xFFFE4450),
        attributeColor = Color(0xFF36F9F6),
        selectionBg = Color(0xFF413C58)
    )

    val TokyoNight = CodeThemeColorScheme(
        name = "Tokyo Night",
        isDark = true,
        background = Color(0xFF1A1B26),
        lineNumberBg = Color(0xFF16161E),
        lineNumberText = Color(0xFF565F89),
        currentLineBg = Color(0xFF24283B),
        textColor = Color(0xFFA9B1D6),
        keywordColor = Color(0xFFBB9AF7),
        stringColor = Color(0xFF9ECE6A),
        commentColor = Color(0xFF565F89),
        numberColor = Color(0xFFFF9E64),
        functionColor = Color(0xFF7AA2F7),
        tagColor = Color(0xFFF7768E),
        attributeColor = Color(0xFF2AC3DE),
        selectionBg = Color(0xFF28345A)
    )

    val GitHubLight = CodeThemeColorScheme(
        name = "GitHub Light",
        isDark = false,
        background = Color(0xFFFFFFFF),
        lineNumberBg = Color(0xFFF6F8FA),
        lineNumberText = Color(0xFF8C959F),
        currentLineBg = Color(0xFFF3F4F6),
        textColor = Color(0xFF24292F),
        keywordColor = Color(0xFFCF222E),
        stringColor = Color(0xFF0A3069),
        commentColor = Color(0xFF6E7781),
        numberColor = Color(0xFF0550AE),
        functionColor = Color(0xFF8250DF),
        tagColor = Color(0xFF116329),
        attributeColor = Color(0xFF0550AE),
        selectionBg = Color(0xFFDDF4FF)
    )

    val allThemes = listOf(SophisticatedDark, VsCodeDarkPlus, Monokai, OneDarkPro, Nord, Dracula, Synthwave84, TokyoNight, SolarizedLight, GitHubLight, Cyberpunk)
}
