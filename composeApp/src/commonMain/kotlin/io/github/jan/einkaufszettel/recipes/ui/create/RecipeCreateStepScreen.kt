package io.github.jan.einkaufszettel.recipes.ui.create

import cafe.adriel.voyager.core.screen.Screen

interface RecipeCreateStepScreen : Screen {

    val index: Int

    val nextStep: RecipeCreateStepScreen?

    companion object {
        const val MAX_INDEX = 1
    }

}