package io.github.jan.einkaufszettel.ui.screen.app.tabs.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io.github.jan.einkaufszettel.Res

data object HomeTab: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val painter = rememberVectorPainter(Icons.Filled.Home)
            return remember {
                TabOptions(0u, Res.string.home, painter)
            }
        }

    @Composable
    override fun Content() {
        Text("Home")
    }

}