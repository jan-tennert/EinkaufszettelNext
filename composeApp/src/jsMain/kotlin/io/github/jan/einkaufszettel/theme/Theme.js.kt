package io.github.jan.einkaufszettel.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun SystemColorScheme(isDark: Boolean): ColorScheme {
    return if (isDark) darkColorScheme() else lightColorScheme()
}