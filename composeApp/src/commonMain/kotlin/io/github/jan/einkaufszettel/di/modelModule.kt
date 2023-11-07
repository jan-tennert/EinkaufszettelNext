package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.di.models.authModels
import io.github.jan.einkaufszettel.di.models.homeModels
import io.github.jan.einkaufszettel.di.models.profileModels
import io.github.jan.einkaufszettel.di.models.recipeModels
import io.github.jan.einkaufszettel.di.models.shopModels
import org.koin.dsl.module

val modelModule = module {
    includes(authModels, profileModels, homeModels, shopModels, recipeModels)
}