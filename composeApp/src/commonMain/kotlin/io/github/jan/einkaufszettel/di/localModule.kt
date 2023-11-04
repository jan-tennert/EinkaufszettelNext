package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.data.local.ProfileDataSource
import io.github.jan.einkaufszettel.data.local.ProfileDataSourceImpl
import org.koin.dsl.module

val localModule = module {
    single<ProfileDataSource> {
        ProfileDataSourceImpl(get(), get())
    }
}