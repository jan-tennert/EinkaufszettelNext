package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.di.models.authModels
import org.koin.dsl.module

val modelModule = module {
    includes(authModels)
}