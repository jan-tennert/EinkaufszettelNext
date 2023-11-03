package io.github.jan.einkaufszettel.ui.screen

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.screen.auth.AuthScreen
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.SessionStatus
import org.koin.compose.koinInject

object RootScreen: Screen {

    @Composable
    override fun Content() {
        val gotrue = koinInject<GoTrue>()
        val sessionStatus by gotrue.sessionStatus.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow
        LaunchedEffect(sessionStatus) {
            when(sessionStatus) {
                is SessionStatus.Authenticated -> navigator.push(HomeScreen)
                SessionStatus.NetworkError -> navigator.push(HomeScreen)
                SessionStatus.NotAuthenticated -> navigator.push(AuthScreen)
                else -> {}
            }
        }
        CircularProgressIndicator()
    }

}