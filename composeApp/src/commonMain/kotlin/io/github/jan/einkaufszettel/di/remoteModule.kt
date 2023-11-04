package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.data.remote.AuthenticationApi
import io.github.jan.einkaufszettel.data.remote.AuthenticationApiImpl
import org.koin.dsl.module

val remoteModule = module {
    single<AuthenticationApi> {
        AuthenticationApiImpl(get())
    }
}