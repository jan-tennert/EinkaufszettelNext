package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.app.tabs.settings.SettingsScreenModel
import org.koin.dsl.module

val settingsModels = module {
    factory {
        SettingsScreenModel(
            get(),
            get(),
            get(),
            get()
        )
    }
}