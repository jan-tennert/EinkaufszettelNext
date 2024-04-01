package io.github.jan.einkaufszettel.cards.ui.screen.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.jan.einkaufszettel.cards.data.local.CardsDataSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CardDetailScreenModel(
    private val cardId: Long,
    cardsDataSource: CardsDataSource
): ScreenModel {

    val cardFlow = cardsDataSource.getAllCards()
        .map {
            it.find { card -> card.id == cardId }
        }
        .stateIn(screenModelScope, SharingStarted.Eagerly, null)

}