package io.github.jan.einkaufszettel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.getKoin
import org.koin.core.parameter.ParametersDefinition
import kotlin.coroutines.CoroutineContext

@Composable
expect fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T>

expect val PlatformNetworkContext: CoroutineContext

expect suspend fun Auth.checkForCode()

@Composable
public inline fun <reified T : ScreenModel> Screen.getScreenModel(
    tag: String? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    val koin = getKoin()
    return rememberScreenModel(tag = tag) { koin.get(null, parameters) }
}

@Composable
public inline fun <reified T : ScreenModel> Navigator.getNavigatorScreenModelT(
    tag: String? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    val koin = getKoin()
    return rememberNavigatorScreenModel(tag = tag) { koin.get(null, parameters) }
}
