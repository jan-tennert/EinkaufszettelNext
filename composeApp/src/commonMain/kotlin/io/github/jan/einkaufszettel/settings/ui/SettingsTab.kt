package io.github.jan.einkaufszettel.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.root.ui.component.LoadingCircle

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
        val profile by screenModel.userProfile.collectAsStateWithLifecycle()
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
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(Res.string.change)
                    }
                }
                Button(onClick = { /*TODO*/ }) {
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