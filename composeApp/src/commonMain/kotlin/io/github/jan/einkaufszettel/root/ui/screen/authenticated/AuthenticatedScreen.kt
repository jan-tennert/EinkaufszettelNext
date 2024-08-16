package io.github.jan.einkaufszettel.root.ui.screen.authenticated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.app.ui.AppScreen
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.profile.ui.ProfileCreateScreen
import io.github.jan.einkaufszettel.root.ui.screen.LoadingScreen
import kotlinx.coroutines.launch

object AuthenticatedScreen: Screen {

    @Composable
    override fun Content() {
        Navigator(LoadingScreen) { navigator ->
            val screenModel = getScreenModel<AuthenticatedScreenModel>()
            LaunchedEffect(Unit) {
                launch {
                    screenModel.checkUser()
                }
                launch {
                    screenModel.state.collect { screenState ->
                        when(screenState) {
                            AuthenticatedScreenModel.State.Loading -> {
                                navigator.replace(LoadingScreen)
                            }
                            AuthenticatedScreenModel.State.NetworkError, AuthenticatedScreenModel.State.UserFound -> {
                                if(navigator.lastItem !is AppScreen) {
                                    println("AuthenticatedScreen.Content ${navigator.lastItem}")
                                    navigator.replace(AppScreen)
                                }
                            }
                            AuthenticatedScreenModel.State.UserNotFound -> {
                                navigator.replace(ProfileCreateScreen)
                            }
                        }
                    }
                }
            }
            CurrentScreen()
        }
    }

}