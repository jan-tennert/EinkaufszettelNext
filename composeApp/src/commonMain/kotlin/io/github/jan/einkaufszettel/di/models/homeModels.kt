package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.app.AppScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.home.HomeScreenModel
import org.koin.dsl.module

val homeModels = module {
    single {
        AppScreenModel(get(), get(), get(), get(), get(), get())
    }
    single {
        HomeScreenModel(get(), get(), get(), get())
    }
}