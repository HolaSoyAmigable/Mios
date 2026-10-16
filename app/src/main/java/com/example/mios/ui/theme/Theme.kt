package com.example.mios.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

private val OnDark = Color(0xFFEDEDF2)
private val OnDarkMuted = Color(0xFFB4B4BF)

@Composable
fun MiosTheme(
    themeSettings: ThemeSettings = ThemeSettings.Default,
    content: @Composable () -> Unit
) {
    val colorScheme = remember(themeSettings) {
        val background = ThemeRules.backgroundColor(themeSettings.appHue)
        val window = ThemeRules.windowColor(themeSettings.appHue)
        val accent = ThemeRules.accentColor(themeSettings.appHue)
        val border = ThemeRules.borderColor(themeSettings.borderHue)

        darkColorScheme(
            primary = accent,
            onPrimary = Color.Black,
            secondary = accent,
            onSecondary = Color.Black,
            tertiary = accent,
            onTertiary = Color.Black,
            background = background,
            onBackground = OnDark,
            surface = background,
            onSurface = OnDark,
            surfaceVariant = window,
            onSurfaceVariant = OnDarkMuted,
            surfaceContainerLowest = background,
            surfaceContainerLow = window,
            surfaceContainer = window,
            surfaceContainerHigh = window,
            surfaceContainerHighest = window,
            outline = border,
            outlineVariant = border.copy(alpha = 0.35f),
            error = ThemeRules.CancelRed,
            onError = Color.White
        )
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}