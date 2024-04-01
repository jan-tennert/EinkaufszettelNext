package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.cards.ui.screen.create.CardsCreateScreenModel
import io.github.jan.einkaufszettel.cards.ui.screen.detail.CardDetailScreenModel
import io.github.jan.einkaufszettel.cards.ui.screen.edit.CardEditScreenModel
import io.github.jan.einkaufszettel.cards.ui.screen.main.CardsScreenModel
import org.koin.dsl.module

val cardsModels = module {
    factory {
        CardsScreenModel(get(), get(), get())
    }
    factory {
        CardsCreateScreenModel(get(), get(), get(), get(), get(), get())
    }
    factory { parameters ->
        CardDetailScreenModel(parameters.get(), get())
    }
    factory { parameters ->
        CardEditScreenModel(parameters.get(), get(), get(), get(), get())
    }
}