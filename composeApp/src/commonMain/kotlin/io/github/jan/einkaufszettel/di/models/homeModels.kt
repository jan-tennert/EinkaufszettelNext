package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.app.AppScreenModel
import org.koin.dsl.module

val homeModels = module {
    single {
        AppScreenModel(get(), get(), get(), get(), get(), get())
    }
}