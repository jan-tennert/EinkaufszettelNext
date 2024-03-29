package io.github.jan.einkaufszettel.root.ui.dialog

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog() {
    Dialog(
        onDismissRequest = {},
    ) {
        CircularProgressIndicator()
    }
}
