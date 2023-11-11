package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.main

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.RecipeDataSource
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RecipeScreenModel(
    recipeDataSource: RecipeDataSource,
    auth: Auth
): ScreenModel {

    val recipeFlow = recipeDataSource.getAllRecipes().stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())
    val userIdFlow = auth.sessionStatus
        .filter { it is SessionStatus.Authenticated }
        .map { (it as SessionStatus.Authenticated).session.user?.id }
        .stateIn(screenModelScope, SharingStarted.Eagerly, null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

}