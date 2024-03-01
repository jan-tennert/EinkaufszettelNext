package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.create

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.data.local.image.LocalImageData
import io.github.jan.einkaufszettel.data.local.image.LocalImageReader
import io.github.jan.einkaufszettel.data.remote.RecipeApi
import io.github.jan.einkaufszettel.ui.screen.app.AppState
import io.github.jan.einkaufszettel.ui.screen.app.AppStateModel
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class RecipeCreateScreenModel(
    private val recipeApi: RecipeApi,
    private val recipeDataSource: RecipeDataSource,
    private val localImageReader: LocalImageReader,
    private val auth: Auth,
): AppStateModel() {

    sealed interface State: AppState {
        data object Success : State
    }

    private val _imageData = MutableStateFlow<LocalImageData?>(null)
    val imageData = _imageData.asStateFlow()

    fun createRecipe(
        name: String,
        imageData: LocalImageData,
        steps: String,
        ingredients: List<String>,
        private: Boolean,
    ) {
        screenModelScope.launch {
            runCatching {
                val imagePath = "${Clock.System.now().toEpochMilliseconds()}.${imageData.extension}"
                recipeApi.uploadImage(imagePath, imageData.data)
                recipeApi.createRecipe(name, auth.currentUserOrNull()?.id ?: error("No user found"), imagePath, ingredients, steps, private)
            }.onSuccess {
                recipeDataSource.insertRecipe(it)
                mutableState.value = State.Success
            }.onFailure {
                it.printStackTrace()
                when(it) {
                    is RestException -> mutableState.value = AppState.Error(it.message ?: "")
                    else -> mutableState.value = AppState.NetworkError
                }
            }
        }
    }

    fun importNativeFile(file: Any) {
        screenModelScope.launch {
            runCatching {
                localImageReader.platformFileToLocalImage(file)
            }.onSuccess {
                _imageData.value = it
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

}