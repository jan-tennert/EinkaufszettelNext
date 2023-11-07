package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.RecipeDataSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RecipeDetailScreenModel(
    private val recipeId: Long,
    recipeDataSource: RecipeDataSource
): ScreenModel {

    val recipeFlow = recipeDataSource.getAllRecipes()
        .map {
            it.find { recipe -> recipe.id == recipeId }
        }
        .stateIn(screenModelScope, SharingStarted.Eagerly, null)

}