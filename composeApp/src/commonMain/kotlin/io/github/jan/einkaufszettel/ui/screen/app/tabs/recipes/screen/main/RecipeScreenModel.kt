package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.main

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.RecipeDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class RecipeScreenModel(
    recipeDataSource: RecipeDataSource
): ScreenModel {

    val recipeFlow = recipeDataSource.getAllRecipes().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

}