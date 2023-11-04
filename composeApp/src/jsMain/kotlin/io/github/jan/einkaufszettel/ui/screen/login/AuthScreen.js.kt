package io.github.jan.einkaufszettel.ui.screen.login

import androidx.compose.runtime.Composable
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.defaultLoginBehavior
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Google

@Composable
internal actual fun loginWithGoogle(
    composeAuth: ComposeAuth,
    onResult: (NativeSignInResult) -> Unit
) = defaultLoginBehavior {
    composeAuth.supabaseClient.gotrue.loginWith(Google)
}