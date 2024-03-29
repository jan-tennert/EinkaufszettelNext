package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.recipes.ui.detail.RecipeDetailScreenModel
import io.github.jan.einkaufszettel.recipes.ui.main.RecipeScreenModel
import org.koin.dsl.module

val recipeModels = module {
    factory {
        RecipeScreenModel(get(), get())
    }
    factory { parameters ->
        RecipeDetailScreenModel(parameters.get(), get())
    }
}