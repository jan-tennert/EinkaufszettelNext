package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.main.ShopScreen

data object ShopsTab: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val painter = rememberVectorPainter(Icons.Filled.ShoppingCart)
            return remember {
                TabOptions(1u, Res.string.shops, painter)
            }
        }

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        Navigator(ShopScreen) {
            FadeTransition(it)
        }
    }

}