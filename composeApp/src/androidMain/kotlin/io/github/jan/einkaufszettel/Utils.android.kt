package io.github.jan.einkaufszettel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jan.supabase.gotrue.GoTrue
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T> = collectAsStateWithLifecycle()
actual suspend fun GoTrue.checkForCode() {
}