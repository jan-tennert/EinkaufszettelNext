package io.github.jan.einkaufszettel.ui.screen.app.tabs

import io.github.jan.einkaufszettel.ui.screen.app.tabs.home.HomeTab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.RecipesTab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.settings.SettingsTab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.ShopsTab

object Tabs {

    val ALL = listOf(
        HomeTab,
        ShopsTab,
        RecipesTab,
        SettingsTab
    )

}