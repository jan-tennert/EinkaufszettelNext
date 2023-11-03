package io.github.jan.einkaufszettel.ui.screen.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import io.github.jan.einkaufszettel.Res
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.NativeSignInState
import io.github.jan.supabase.compose.auth.composable.rememberLoginWithGoogle
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.LocalAuthState
import io.github.jan.supabase.compose.auth.ui.ProviderIcon
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import io.github.jan.supabase.gotrue.providers.Google
import org.koin.compose.koinInject

object AuthScreen: Screen {

    @OptIn(SupabaseExperimental::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val composeAuth = koinInject<ComposeAuth>()
        val googleLogin = loginWithGoogle(composeAuth)
        AuthForm {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            val state = LocalAuthState.current
            var signUp by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    OutlinedEmailField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(Res.string.email) }
                    )
                    OutlinedPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(Res.string.password) }
                    )
                    Button(
                        onClick = { },
                        enabled = state.validForm
                    ) {
                        Text(if (signUp) Res.string.sign_up else Res.string.sign_in)
                    }
                    Button(
                        onClick = { /*googleLogin.startFlow()*/ }
                    ) {
                        ProviderIcon(Google, null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (signUp) Res.string.sign_up_with_google else Res.string.sign_in_with_google)
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                TextButton(
                    onClick = { signUp = !signUp }
                ) {
                    Text(if (signUp) Res.string.already_have_an_account else Res.string.dont_have_an_account)
                }
            }
        }
    }

}

@Composable
internal expect fun loginWithGoogle(composeAuth: ComposeAuth, onResult: (NativeSignInResult) -> Unit = {}): NativeSignInState