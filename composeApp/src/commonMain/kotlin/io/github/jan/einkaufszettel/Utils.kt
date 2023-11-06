package io.github.jan.einkaufszettel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import io.github.jan.supabase.gotrue.GoTrue
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.getKoin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.coroutines.CoroutineContext

@Composable
expect fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T>

expect val PlatformNetworkContext: CoroutineContext

expect suspend fun GoTrue.checkForCode()

@Composable
public inline fun <reified T : ScreenModel> Screen.getScreenModel(
    tag: String? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    val koin = getKoin()
    return rememberScreenModel(tag = tag) { koin.get(null, parameters) }
}