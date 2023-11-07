package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.RecipeDetailScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.main.RecipeScreenModel
import org.koin.dsl.module

val recipeModels = module {
    factory {
        RecipeScreenModel(get())
    }
    factory { parameters ->
        RecipeDetailScreenModel(parameters.get(), get())
    }
}