package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.jan.einkaufszettel.Res

@Composable
fun ProductDialog(
    oldContent: String? = null,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    Dialog(onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.background(MaterialTheme.colorScheme.background, RoundedCornerShape(10)).padding(10.dp)
        ) {
            var content by remember { mutableStateOf(oldContent ?: "") }
            Text(if(oldContent != null) Res.string.edit_product else Res.string.create_product, style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                singleLine = true,
                label = { Text(Res.string.product) },
                keyboardActions = KeyboardActions(onDone = { onSubmit(content) }),
                modifier = Modifier.onPreviewKeyEvent {
                    //it.handleEnter { onSubmit(content) }
                    false
                }
            )
            Button(
                onClick = {
                    onSubmit(content)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(Res.string.save)
            }
        }
    }
}