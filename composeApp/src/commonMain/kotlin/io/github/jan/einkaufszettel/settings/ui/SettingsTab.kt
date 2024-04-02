package io.github.jan.einkaufszettel.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.root.ui.component.LoadingCircle
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField

data object SettingsTab: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val painter = rememberVectorPainter(Icons.Filled.Settings)
            return remember {
                TabOptions(4u, Res.string.settings, painter)
            }
        }

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SettingsScreenModel>()
        var showLogoutDialog by remember { mutableStateOf(false) }
        var showPasswordChangeDialog by remember { mutableStateOf(false) }
        var showProfileChangeDialog by remember { mutableStateOf(false) }
        val profile by screenModel.userProfile.collectAsStateWithLifecycle()
        val state by screenModel.state.collectAsStateWithLifecycle()
        if(profile == null) {
            LoadingCircle()
            return
        }
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showLogoutDialog = true },
                    text = { Text(Res.string.logout) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(it).padding(start = 6.dp)
            ) {
                Text(Res.string.account, style = MaterialTheme.typography.headlineMedium)
                Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("${Res.string.name}: ", fontWeight = FontWeight.Bold)
                    Text(profile!!.username)
                    TextButton(onClick = {
                        showProfileChangeDialog = true
                    }) {
                        Text(Res.string.change)
                    }
                }
                Button(onClick = { showPasswordChangeDialog = true }) {
                    Text(Res.string.change_password)
                }
            }
        }

        if(showLogoutDialog) {
            LogoutDialog(
                onDismiss = { showLogoutDialog = false },
                onLogout = screenModel::logout
            )
        }
        if(showPasswordChangeDialog) {
            PasswordChangeDialog(
                onDismiss = { showPasswordChangeDialog = false },
                onChangePassword = screenModel::changePassword
            )
        }

        if(showProfileChangeDialog) {
            ProfileChangeDialog(
                initialName = profile!!.username,
                onDismiss = { showProfileChangeDialog = false },
                onChangeProfile = screenModel::updateProfile
            )
        }

        when(state) {
            is AppState.Loading -> LoadingCircle()
            is SettingsScreenModel.State.PasswordChanged -> {
                AlertDialog(
                    onDismissRequest = screenModel::resetState,
                    title = { Text(Res.string.password_changed) },
                    text = { Text(Res.string.password_changed_message) },
                    confirmButton = {
                        TextButton(
                            onClick = screenModel::resetState
                        ) {
                            Text("Ok")
                        }
                    }
                )
            }
            is SettingsScreenModel.State.ProfileUpdated -> {
                AlertDialog(
                    onDismissRequest = screenModel::resetState,
                    title = { Text(Res.string.profile_changed) },
                    text = { Text(Res.string.profile_changed_message) },
                    confirmButton = {
                        TextButton(
                            onClick = screenModel::resetState
                        ) {
                            Text("Ok")
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun ProfileChangeDialog(
        initialName: String,
        onDismiss: () -> Unit,
        onChangeProfile: (String) -> Unit
    ) {
        var newName by remember { mutableStateOf(initialName) }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(Res.string.change_profile) },
            text = {
                Column {
                    Text(Res.string.change_profile_text)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text(Res.string.new_name) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onChangeProfile(newName)
                        onDismiss()
                    }
                ) {
                    Text(Res.string.change)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(Res.string.cancel)
                }
            }
        )
    }

    @OptIn(SupabaseExperimental::class, ExperimentalMaterial3Api::class)
    @Composable
    fun PasswordChangeDialog(
        onDismiss: () -> Unit,
        onChangePassword: (String) -> Unit
    ) {
        var newPassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(Res.string.change_password) },
            text = {
                   Column {
                       Text(Res.string.change_password_text)
                       Spacer(Modifier.height(8.dp))
                       OutlinedPasswordField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text(Res.string.new_password) }
                       )
                   }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onChangePassword(newPassword)
                        onDismiss()
                    }
                ) {
                    Text(Res.string.change)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(Res.string.cancel)
                }
            }
        )
    }

    @Composable
    fun LogoutDialog(
        onDismiss: () -> Unit,
        onLogout: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(Res.string.logout) },
            text = { Text(Res.string.logout_confirmation) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onLogout()
                        onDismiss()
                    }
                ) {
                    Text(Res.string.yes)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(Res.string.cancel)
                }
            }
        )
    }

}