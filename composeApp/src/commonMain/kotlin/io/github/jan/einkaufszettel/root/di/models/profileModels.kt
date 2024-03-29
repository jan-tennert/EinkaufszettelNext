package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.profile.ui.ProfileCreateScreenModel
import org.koin.dsl.module

val profileModels = module {
    factory {
        ProfileCreateScreenModel(get(), get())
    }
}