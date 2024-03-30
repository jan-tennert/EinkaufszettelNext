package io.github.jan.einkaufszettel.recipes.ui.create

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.screenModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateModel
import io.github.jan.einkaufszettel.recipes.data.local.RecipeDataSource
import io.github.jan.einkaufszettel.recipes.data.remote.RecipeApi
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageData
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageReader
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
    private val _name = MutableStateFlow("")
    private val _showIngredientsDialog = MutableStateFlow(false)
    val ingredients = mutableStateListOf<String>()
    val name = _name.asStateFlow()
    val imageData = _imageData.asStateFlow()
    val instructionState = RichTextState()
    val showIngredientsDialog = _showIngredientsDialog.asStateFlow()

    fun createRecipe(
        name: String,
        imageData: LocalImageData?,
        steps: String,
        ingredients: List<String>,
        private: Boolean,
    ) {
        mutableState.value = AppState.Loading
        screenModelScope.launch {
            runCatching {
                val imagePath = imageData?.let {
                    val imagePath = "${Clock.System.now().toEpochMilliseconds()}.${imageData.extension}"
                    recipeApi.uploadImage(imagePath, imageData.data)
                    imagePath
                }
                recipeApi.createRecipe(name, auth.currentUserOrNull()?.id ?: error("No user found"), imagePath, ingredients, steps, private)
            }.onSuccess {
                recipeDataSource.insertRecipe(it)
                resetContent()
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

    fun setName(name: String) {
        _name.value = name
    }

    fun setShowIngredientsDialog(show: Boolean) {
        _showIngredientsDialog.value = show
    }

    fun resetContent() {
        _name.value = ""
        ingredients.clear()
        instructionState.clear()
        _showIngredientsDialog.value = false
    }

}