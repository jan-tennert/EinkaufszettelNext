package io.github.jan.einkaufszettel.ui.screen.authenticated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.ui.screen.app.AppScreen
import io.github.jan.einkaufszettel.ui.screen.profile.ProfileCreateScreen

object AuthenticatedScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<AuthenticatedScreenModel>()
        val screenState by screenModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            screenModel.checkUser()
        }

        println("authenticated $screenState")
        when(screenState) {
            AuthenticatedScreenModel.State.Loading -> {
                LoadingCircle()
            }
            AuthenticatedScreenModel.State.NetworkError -> {
                navigator.push(AppScreen)
            }
            AuthenticatedScreenModel.State.UserFound -> {
                navigator.push(AppScreen)
            }
            AuthenticatedScreenModel.State.UserNotFound -> {
                navigator.push(ProfileCreateScreen)
            }
        }
    }

}