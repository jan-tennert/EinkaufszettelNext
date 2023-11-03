package io.github.jan.einkaufszettel.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

enum class AppTheme {
    System, Dark, Light
}

internal val LocalAppTheme = compositionLocalOf { mutableStateOf(AppTheme.System) }

@Composable
internal fun AppTheme(
    content: @Composable() () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDark = when (val theme = LocalAppTheme.current.value) {
        AppTheme.System -> systemIsDark
        AppTheme.Dark -> true
        AppTheme.Light -> false
    }
    val scheme = SystemColorScheme(isDark)
    MaterialTheme(
        colorScheme = scheme,
        content = {
            Surface(content = content)
        }
    )
}

@Composable
internal expect fun SystemColorScheme(isDark: Boolean): ColorScheme
