package io.github.jan.einkaufszettel.auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.auth.LoginScreenModel
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.root.ui.dialog.ErrorDialog
import io.github.jan.einkaufszettel.root.ui.dialog.LoadingDialog
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.NativeSignInState
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.LocalAuthState
import io.github.jan.supabase.compose.auth.ui.ProviderIcon
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import io.github.jan.supabase.gotrue.providers.Google
import org.koin.compose.koinInject

object LoginScreen : Screen {

    @OptIn(SupabaseExperimental::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val composeAuth = koinInject<ComposeAuth>()
        var nativeSignInResult by remember { mutableStateOf<NativeSignInResult?>(null) }
        val screenModel = getScreenModel<LoginScreenModel>()
        val googleLogin = loginWithGoogle(
            composeAuth = composeAuth,
            onResult = {
                nativeSignInResult = it
                screenModel.resetState()
            }
        )
        val screenState by screenModel.state.collectAsStateWithLifecycle()
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var signUp by rememberSaveable { mutableStateOf(false) }

        AuthForm {
            val state = LocalAuthState.current
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
                        onClick = { if(signUp) screenModel.signUp(email, password) else screenModel.login(email, password) },
                        enabled = state.validForm
                    ) {
                        Text(if (signUp) Res.string.sign_up else Res.string.sign_in)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            googleLogin.startFlow()
                            screenModel.setLoading()
                        }
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


        when(screenState) {
            is LoginScreenModel.State.Error -> {
                ErrorDialog(
                    error = (screenState as LoginScreenModel.State.Error).message,
                    onDismiss = { screenModel.resetState() }
                )
            }
            LoginScreenModel.State.Loading -> {
                LoadingDialog()
            }
            LoginScreenModel.State.SignUpSuccess -> {
                SignUpSuccessDialog {
                    screenModel.resetState()
                }
            }
            else -> {}
        }

        if(nativeSignInResult != null && nativeSignInResult !in listOf(NativeSignInResult.Success, NativeSignInResult.ClosedByUser)) {
            val message = when(nativeSignInResult) {
                is NativeSignInResult.Error -> Res.string.unknown_error.format((nativeSignInResult as NativeSignInResult.Error).message)
                is NativeSignInResult.NetworkError -> Res.string.network_error
                else -> ""
            }
            ErrorDialog(
                error = message,
                onDismiss = { nativeSignInResult = null }
            )
        }
    }

    @Composable
    private fun SignUpSuccessDialog(onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(Res.string.sign_up) },
            text = { Text(Res.string.sign_up_success) },
            confirmButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text("Ok")
                }
            }
        )
    }



}

@Composable
internal expect fun loginWithGoogle(
    composeAuth: ComposeAuth,
    onResult: (NativeSignInResult) -> Unit = {}
): NativeSignInState