package io.github.jan.einkaufszettel.auth.ui

import androidx.compose.runtime.Composable
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.defaultLoginBehavior
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import kotlinx.browser.document

@OptIn(SupabaseInternal::class)
@Composable
internal actual fun loginWithGoogle(
    composeAuth: ComposeAuth,
    onResult: (NativeSignInResult) -> Unit
) = composeAuth.defaultLoginBehavior {
    composeAuth.supabaseClient.auth.signInWith(Google, redirectUrl = document.location?.href)
}