package io.github.jan.einkaufszettel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import io.github.jan.supabase.gotrue.GoTrue
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T> = collectAsState()
actual suspend fun GoTrue.checkForCode() {
    val url = Url(window.location.href)
    val code = url.parameters["code"]
    if (code != null) {
        exchangeCodeForSession(code)
        window.location.href = URLBuilder(url).apply {
            parameters.clear()
        }.buildString()
    }
}