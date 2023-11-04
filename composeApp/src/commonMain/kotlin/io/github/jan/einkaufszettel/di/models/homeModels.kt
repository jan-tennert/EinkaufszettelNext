package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.home.HomeScreenModel
import org.koin.dsl.module

val homeModels = module {
    single {
        HomeScreenModel(get(), get())
    }
}