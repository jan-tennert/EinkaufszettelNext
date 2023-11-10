package io.github.jan.einkaufszettel.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import io.github.jan.einkaufszettel.Res

@Composable
fun ErrorDialog(
    error: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Res.string.error) },
        text = { Text(error) },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Ok")
            }
        }
    )
}