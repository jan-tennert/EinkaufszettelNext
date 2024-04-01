package io.github.jan.einkaufszettel.recipes.ui.steps

import cafe.adriel.voyager.core.screen.Screen

interface RecipeModifyStepScreen : Screen {

    val index: Int

    val nextStep: RecipeModifyStepScreen?

    companion object {
        const val MAX_INDEX = 1
    }

}