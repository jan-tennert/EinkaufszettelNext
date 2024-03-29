package io.github.jan.einkaufszettel.root.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.app.ui.AppScreen
import io.github.jan.einkaufszettel.auth.ui.LoginScreen
import io.github.jan.einkaufszettel.root.ui.screen.authenticated.AuthenticatedScreen
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import org.koin.compose.koinInject

object RootScreen: Screen {

    @Composable
    override fun Content() {
        Navigator(LoadingScreen) { navigator ->
            val auth = koinInject<Auth>()
            LaunchedEffect(Unit) {
                auth.sessionStatus
                    .collect { status: SessionStatus ->
                        when (status) {
                            is SessionStatus.Authenticated -> {
                                navigator.push(AuthenticatedScreen)
                            }
                            SessionStatus.NetworkError -> {
                                if (navigator.lastItem is LoadingScreen) {
                                    navigator.push(AppScreen)
                                }
                            }
                            is SessionStatus.NotAuthenticated -> navigator.push(LoginScreen)
                            SessionStatus.LoadingFromStorage -> {
                                navigator.push(LoadingScreen)
                            }
                        }
                    }
            }
            CurrentScreen()
        }
    }

}