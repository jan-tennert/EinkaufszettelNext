package io.github.jan.einkaufszettel.ui.screen.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.StateScreenModel
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.ui.dialog.ErrorDialog

interface AppState {
    data object Idle : AppState
    data object Loading : AppState
    data object NetworkError : AppState
    data class Error(val message: String) : AppState
}

open class AppStateModel: StateScreenModel<AppState>(AppState.Idle) {

    fun resetState() {
        mutableState.value = AppState.Idle
    }

}

@Composable
fun AppStateErrorHandler(
    state: AppState,
    resetState: () -> Unit
) {
    when(state) {
        is AppState.Error -> {
            ErrorDialog((state as AppState.Error).message, resetState)
        }
        AppState.NetworkError -> {
            ErrorDialog(Res.string.network_error, resetState)
        }
        else -> {}
    }
}