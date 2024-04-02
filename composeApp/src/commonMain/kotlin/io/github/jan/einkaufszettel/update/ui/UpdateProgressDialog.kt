package io.github.jan.einkaufszettel.update.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import io.github.jan.einkaufszettel.Res

@Composable
fun UpdateProgressDialog(progress: Float) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(Res.string.app_update) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(Res.string.downloading)
                LinearProgressIndicator(
                    progress = { progress },
                )
            }
        },
        confirmButton = { },
        dismissButton = { }
    )
}