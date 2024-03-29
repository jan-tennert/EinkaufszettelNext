package io.github.jan.einkaufszettel.recipes.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import einkaufszettel.GetAllRecipes
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.components.ContextMenuScope
import io.github.jan.supabase.storage.publicStorageItem

expect object RecipeCardDefaults {
    val HEIGHT: Dp
    val WIDTH: Dp
    val PADDING: Dp
}

@Composable
fun RecipeCard(
    recipe: GetAllRecipes,
    modifier: Modifier = Modifier,
    isOwner: Boolean = false,
    onClick: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    ContextMenuScope(
        modifier = modifier,
        onItemClicked = onClick,
        items = {
            DropdownMenuItem(
                text = { Text(Res.string.edit) },
                onClick = {
                    onEdit()
                    it.value = false
                },
                enabled = isOwner
            )
            DropdownMenuItem(
                text = { Text(Res.string.delete) },
                onClick = {
                    onDelete()
                    it.value = false
                },
                enabled = isOwner
            )
        }
    ) {
        ElevatedCard(
            modifier = Modifier.indication(it, LocalIndication.current),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(recipe.name, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                RecipeCardImage(recipe.imagePath, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RecipeCardImage(imagePath: String?, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        if (imagePath != null) {
            AsyncImage(publicStorageItem("recipes", imagePath), modifier = Modifier.matchParentSize(), contentDescription = null)
        } else {
            Icon(Icons.Filled.QuestionMark, modifier = Modifier.matchParentSize(), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}