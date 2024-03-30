package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.cards.ui.screen.main.CardsScreenModel
import org.koin.dsl.module

val cardsModels = module {
    factory {
        CardsScreenModel()
    }
}