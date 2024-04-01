package io.github.jan.einkaufszettel.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import io.github.jan.einkaufszettel.Res

@Composable
fun DeleteDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDelete()
                    onDismiss()
                }
            ) {
                Text(Res.string.delete)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(Res.string.cancel)
            }
        },
        title = {
            Text(title)
        },
        text = {
            Text(text)
        }
    )

}