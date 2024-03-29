package io.github.jan.einkaufszettel.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.screen.app.AppScreen
import io.github.jan.einkaufszettel.ui.screen.authenticated.AuthenticatedScreen
import io.github.jan.einkaufszettel.ui.screen.login.LoginScreen
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.filter
import org.koin.compose.koinInject

object RootScreen: Screen {

    @Composable
    override fun Content() {
        Navigator(LoadingScreen) { navigator ->
            val auth = koinInject<Auth>()
            val sessionStatus by auth.sessionStatus.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                auth.sessionStatus
                    .filter {
                        it !is SessionStatus.Authenticated || it.isNew
                    }
                    .collect {
                        when (sessionStatus) {
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