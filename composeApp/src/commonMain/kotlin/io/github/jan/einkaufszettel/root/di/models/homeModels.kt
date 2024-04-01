package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.app.ui.AppScreenModel
import io.github.jan.einkaufszettel.home.ui.HomeScreenModel
import org.koin.dsl.module

val homeModels = module {
    single {
        AppScreenModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
    single {
        HomeScreenModel(get(), get(), get(), get())
    }
}