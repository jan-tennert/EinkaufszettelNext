package io.github.jan.einkaufszettel.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.app.ui.BlankScreen
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildTopBar(title: String, navigator: Navigator) {
    CenterAlignedTopAppBar(
        title = { Text(title, style = MaterialTheme.typography.headlineMedium) },
        actions = {
            if(CurrentPlatformTarget == PlatformTarget.JS) {
                IconButton(
                    onClick = { navigator.replace(BlankScreen) }
                ) {
                    Icon(Icons.Filled.Close, null)
                }
            }
        }
    )
}