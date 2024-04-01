package io.github.jan.einkaufszettel.cards.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import einkaufszettel.GetAllCards
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.components.ContextMenuScope
import io.github.jan.einkaufszettel.cards.data.remote.CardsApi
import io.github.jan.supabase.storage.authenticatedStorageItem

/*expect object RecipeCardDefaults {
    val HEIGHT: Dp
    val WIDTH: Dp
    val PADDING: Dp
}*/

@Composable
fun CardCard(
    card: GetAllCards,
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
            CardImage(card.imagePath, card.description, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun CardImage(imagePath: String?, text: String, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(imagePath != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(authenticatedStorageItem(CardsApi.BUCKET_ID, imagePath))
                    .crossfade(true)
                    .build(),
                contentDescription = text,
                contentScale = ContentScale.Crop,
                modifier = Modifier.weight(1f)
            )
        } else {
            Icon(Icons.Default.QuestionMark, contentDescription = null, tint = Color.Gray, modifier = Modifier.fillMaxSize())
        }
        Text(text = text, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.padding(4.dp))
    }
}

@Composable
fun ImageCard(
    imageModel: Any,
    text: String,
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    ElevatedCard(onClick) {
        Box(
            modifier = modifier
        ) {
            AsyncImage(
                model = imageModel,
                contentDescription = text,
                contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black), 250f)))
            Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp, horizontal = 4.dp), contentAlignment = Alignment.BottomCenter) {
                Text(text = text, style = TextStyle(color = Color.White), maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}