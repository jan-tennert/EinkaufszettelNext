package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.settings.ui.SettingsScreenModel
import org.koin.dsl.module

val settingsModels = module {
    factory {
        SettingsScreenModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}