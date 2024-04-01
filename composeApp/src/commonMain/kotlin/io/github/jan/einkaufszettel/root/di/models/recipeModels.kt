package io.github.jan.einkaufszettel.root.di.models

import io.github.jan.einkaufszettel.recipes.ui.create.RecipeCreateScreenModel
import io.github.jan.einkaufszettel.recipes.ui.detail.RecipeDetailScreenModel
import io.github.jan.einkaufszettel.recipes.ui.edit.RecipeEditScreenModel
import io.github.jan.einkaufszettel.recipes.ui.main.RecipeScreenModel
import org.koin.dsl.module

val recipeModels = module {
    factory {
        RecipeScreenModel(get(), get(), get())
    }
    factory { parameters ->
        RecipeDetailScreenModel(parameters.get(), get())
    }
    factory { parameters ->
        RecipeEditScreenModel(parameters.get(), get(), get(), get(), get())
    }
    factory {
        RecipeCreateScreenModel(get(), get(), get(), get())
    }
}