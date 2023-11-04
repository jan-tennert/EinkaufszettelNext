package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.data.remote.AuthenticationApi
import io.github.jan.einkaufszettel.data.remote.AuthenticationApiImpl
import io.github.jan.einkaufszettel.data.remote.ProfileApi
import io.github.jan.einkaufszettel.data.remote.ProfileApiImpl
import org.koin.dsl.module

val remoteModule = module {
    single<AuthenticationApi> {
        AuthenticationApiImpl(get())
    }
    single<ProfileApi> {
        ProfileApiImpl(get(), get())
    }
}