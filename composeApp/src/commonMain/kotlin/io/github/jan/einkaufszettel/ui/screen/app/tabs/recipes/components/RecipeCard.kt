package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.option.Scale
import com.seiko.imageloader.option.SizeResolver
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import einkaufszettel.GetAllRecipes
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.ui.screen.app.tabs.components.ContextMenuScope
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget
import io.github.jan.supabase.storage.authenticatedStorageItem

expect object RecipeCardDefaults {
    val HEIGHT: Dp
    val WIDTH: Dp
    val PADDING: Dp
}

@Composable
fun RecipeCard(
    recipe: GetAllRecipes,
    modifier: Modifier = Modifier,
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
                onClick = onEdit
            )
            DropdownMenuItem(
                text = { Text(Res.string.delete) },
                onClick = onDelete
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
                if(CurrentPlatformTarget != PlatformTarget.JS) {
                    RecipeCardImage(recipe.imagePath, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun RecipeCardImage(imagePath: String?, modifier: Modifier) {
    Box(modifier) {
        if(imagePath != null) {
            AutoSizeBox(
                request = remember { ImageRequest(authenticatedStorageItem("recipes", imagePath)) {
                    scale(Scale.FIT)
                    size(SizeResolver {
                        Size(200f, 200f)
                    })
                } },
                modifier = modifier
            ) { action ->
                when (action) {
                    is ImageAction.Loading -> {
                        LoadingCircle()
                    }
                    is ImageAction.Success -> {
                        Image(
                            painter = rememberImageSuccessPainter(action),
                            contentDescription = null
                        )
                    }
                    is ImageAction.Failure -> {
                        // TODO
                    }
                }
            }
        } else {
            // TODO question mark as icon
        }
    }
}