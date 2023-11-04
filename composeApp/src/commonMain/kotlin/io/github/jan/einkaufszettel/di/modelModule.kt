package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.di.models.authModels
import io.github.jan.einkaufszettel.di.models.homeModels
import io.github.jan.einkaufszettel.di.models.profileModels
import org.koin.dsl.module

val modelModule = module {
    includes(authModels, profileModels, homeModels)
}