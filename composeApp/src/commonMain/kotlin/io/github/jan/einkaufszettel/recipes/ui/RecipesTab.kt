package io.github.jan.einkaufszettel.recipes.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.recipes.ui.main.RecipeScreen

data object RecipesTab: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val painter = rememberVectorPainter(Icons.Filled.RestaurantMenu)
            return remember {
                TabOptions(2u, Res.string.recipes, painter)
            }
        }

    @Composable
    override fun Content() {
        Navigator(RecipeScreen) {
            FadeTransition(it)
        }
    }

}