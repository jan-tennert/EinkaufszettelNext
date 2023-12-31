package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.authenticated.AuthenticatedScreenModel
import io.github.jan.einkaufszettel.ui.screen.login.LoginScreenModel
import org.koin.dsl.module

val authModels = module {
    factory {
        LoginScreenModel(get())
    }
    factory {
        AuthenticatedScreenModel(get(), get())
    }
}