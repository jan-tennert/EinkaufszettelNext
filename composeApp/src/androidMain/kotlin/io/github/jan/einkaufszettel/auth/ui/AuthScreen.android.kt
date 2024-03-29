package io.github.jan.einkaufszettel.auth.ui

import androidx.compose.runtime.Composable
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle

@Composable
internal actual fun loginWithGoogle(
    composeAuth: ComposeAuth,
    onResult: (NativeSignInResult) -> Unit
) = composeAuth.rememberSignInWithGoogle(onResult)