package io.github.jan.einkaufszettel.ui.screen.app.tabs.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io.github.jan.einkaufszettel.Res

data object SettingsTab: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val painter = rememberVectorPainter(Icons.Filled.Settings)
            return remember {
                TabOptions(3u, Res.string.settings, painter)
            }
        }

    @Composable
    override fun Content() {
        Text("Settings")
    }

}