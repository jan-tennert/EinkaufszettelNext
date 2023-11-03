package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.auth.AuthScreenModel
import org.koin.dsl.module

val authModels = module {
    factory {
        AuthScreenModel()
    }
}