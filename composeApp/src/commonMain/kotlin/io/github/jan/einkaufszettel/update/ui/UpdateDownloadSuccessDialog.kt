package io.github.jan.einkaufszettel.update.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.jan.einkaufszettel.Res

@Composable
fun UpdateDownloadSuccessDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(Res.string.app_update) },
        text = { Text(Res.string.update_downloaded) },
        confirmButton = { },
        dismissButton = { }
    )
}