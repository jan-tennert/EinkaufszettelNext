package io.github.jan.einkaufszettel.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle

interface AppStateScreen<ScreenModel : AppStateModel>: Screen {

    @Composable
    override fun Content() {
        val screenModel = createScreenModel()
        val appState by screenModel.state.collectAsStateWithLifecycle()
        Content(screenModel, appState)

        AppStateErrorHandler(
            state = appState,
            resetState = screenModel::resetState
        )
    }

    @Composable
    fun createScreenModel(): ScreenModel

    @Composable
    fun Content(screenModel: ScreenModel, state: AppState)

}