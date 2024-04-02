package io.github.jan.einkaufszettel.update.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.jan.einkaufszettel.BuildConfig
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import net.swiftzer.semver.SemVer
import org.koin.compose.koinInject

@Composable
fun CheckForUpdates() {
    val updateScreenModel = koinInject<UpdateScreenModel>()
    val latestVersion by updateScreenModel.latestVersion.collectAsStateWithLifecycle()
    RegisterLifecycleUpdateCheck(updateScreenModel)
    val currentVersion = remember { SemVer.parse(BuildConfig.APP_VERSION_NAME) }
    val ignoreUpdate by updateScreenModel.ignoreUpdate.collectAsStateWithLifecycle()
    val state by updateScreenModel.state.collectAsStateWithLifecycle()
    if(latestVersion != null && currentVersion < latestVersion!! && !ignoreUpdate && state is AppState.Idle) {
        UpdateAvailableDialog(
            ignoreUpdate = { updateScreenModel.ignoreUpdate() },
            onUpdate = { updateScreenModel.downloadLatestVersion() },
            version = latestVersion!!
        )
    }
    when(state) {
        is UpdateScreenModel.State.Progress -> {
            val progress = (state as UpdateScreenModel.State.Progress).progress
            UpdateProgressDialog(progress)
        }
        is UpdateScreenModel.State.DownloadSuccess -> {
            LaunchedEffect(Unit) {
                updateScreenModel.installLatestVersion()
            }
            UpdateDownloadSuccessDialog()
        }
    }
}

@Composable
fun UpdateAvailableDialog(
    ignoreUpdate: () -> Unit,
    onUpdate: () -> Unit,
    version: SemVer
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(Res.string.app_update) },
        text = { Text(Res.string.app_update_message.format(version.toString())) },
        confirmButton = {
            TextButton(
                onClick = onUpdate
            ) {
                Text(Res.string.yes)
            }
        },
        dismissButton = {
            TextButton(
                onClick = ignoreUpdate
            ) {
                Text(Res.string.cancel)
            }
        }
    )
}

@Composable
expect fun RegisterLifecycleUpdateCheck(updateScreenModel: UpdateScreenModel)