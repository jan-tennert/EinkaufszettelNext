package io.github.jan.einkaufszettel.app.ui

import io.github.jan.einkaufszettel.cards.ui.CardsTab
import io.github.jan.einkaufszettel.home.ui.HomeTab
import io.github.jan.einkaufszettel.recipes.ui.RecipesTab
import io.github.jan.einkaufszettel.settings.ui.SettingsTab
import io.github.jan.einkaufszettel.shops.ui.ShopsTab

object Tabs {

    val ALL = listOf(
        HomeTab,
        ShopsTab,
        RecipesTab,
        CardsTab,
        SettingsTab
    )

}