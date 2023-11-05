package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.ShopScreenModel
import org.koin.dsl.module

val shopModels = module {
    single {
        ShopScreenModel(get(), get())
    }
}