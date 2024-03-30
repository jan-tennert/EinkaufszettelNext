package io.github.jan.einkaufszettel.cards.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.cards.ui.screen.main.CardsScreen

data object CardsTab: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val painter = rememberVectorPainter(Icons.Filled.CreditCard)
            return remember {
                TabOptions(3u, Res.string.cards, painter)
            }
        }

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        Navigator(CardsScreen) {
            FadeTransition(it)
        }
    }

}