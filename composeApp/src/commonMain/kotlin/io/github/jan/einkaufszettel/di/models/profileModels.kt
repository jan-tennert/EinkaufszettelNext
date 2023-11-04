package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.profile.ProfileCreateScreenModel
import org.koin.dsl.module

val profileModels = module {
    single<ProfileCreateScreenModel> {
        ProfileCreateScreenModel(get(), get())
    }
}