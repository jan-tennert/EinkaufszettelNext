package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.update.ui.UpdateScreenModel
import org.koin.dsl.module

val updateModels = module {
    single {
        UpdateScreenModel(get(), get())
    }
}