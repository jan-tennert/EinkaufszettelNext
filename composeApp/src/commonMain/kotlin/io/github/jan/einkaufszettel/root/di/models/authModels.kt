package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.auth.LoginScreenModel
import io.github.jan.einkaufszettel.root.ui.screen.authenticated.AuthenticatedScreenModel
import org.koin.dsl.module

val authModels = module {
    factory {
        LoginScreenModel(get())
    }
    factory {
        AuthenticatedScreenModel(get(), get())
    }
}