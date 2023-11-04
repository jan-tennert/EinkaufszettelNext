package io.github.jan.einkaufszettel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import io.github.jan.supabase.gotrue.GoTrue
import kotlinx.coroutines.flow.StateFlow

@Composable
expect fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T>

expect suspend fun GoTrue.checkForCode()