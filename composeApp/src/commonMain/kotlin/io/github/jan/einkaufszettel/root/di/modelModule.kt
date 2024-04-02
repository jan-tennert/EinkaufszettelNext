package io.github.jan.einkaufszettel.root.di

import io.github.jan.einkaufszettel.root.di.models.authModels
import io.github.jan.einkaufszettel.root.di.models.cardsModels
import io.github.jan.einkaufszettel.root.di.models.homeModels
import io.github.jan.einkaufszettel.root.di.models.profileModels
import io.github.jan.einkaufszettel.root.di.models.recipeModels
import io.github.jan.einkaufszettel.root.di.models.settingsModels
import io.github.jan.einkaufszettel.root.di.models.shopModels
import io.github.jan.einkaufszettel.root.di.models.updateModels
import org.koin.dsl.module

val modelModule = module {
    includes(authModels, profileModels, homeModels, shopModels, recipeModels, settingsModels, cardsModels, updateModels)
}