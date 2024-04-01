package io.github.jan.einkaufszettel.cards.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import einkaufszettel.GetAllCards
import io.github.jan.einkaufszettel.app.ui.components.zoomable
import io.github.jan.einkaufszettel.cards.data.remote.CardsApi
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.root.ui.component.LoadingCircle
import io.github.jan.supabase.storage.authenticatedStorageItem
import org.koin.core.parameter.parametersOf

class CardDetailScreen(
    private val id: Long
): Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<CardDetailScreenModel>(tag = id.toString(), parameters = { parametersOf(id) })
        val card by screenModel.cardFlow.collectAsStateWithLifecycle()
        if(card == null) {
            LoadingCircle()
            return
        }
        CardDetailScreenContent(card!!)
    }

    @Composable
    private fun CardDetailScreenContent(card: GetAllCards) {
        val request = ImageRequest.Builder(LocalPlatformContext.current)
            .data(authenticatedStorageItem(CardsApi.BUCKET_ID, card.imagePath))
            .crossfade(true)
            .build()
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = request,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().zoomable()
            )
        }
    }

}