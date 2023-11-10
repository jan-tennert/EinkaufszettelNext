package io.github.jan.einkaufszettel.ui.screen.app.tabs.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.jan.einkaufszettel.Res

@Composable
fun CreateButton(extended: Boolean, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        expanded = extended,
        text = { Text(Res.string.create) },
        icon = { Icon(Icons.Filled.Add, null) },
        onClick = onClick
    )
}