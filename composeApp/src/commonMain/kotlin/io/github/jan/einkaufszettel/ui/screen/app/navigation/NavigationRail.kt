package io.github.jan.einkaufszettel.ui.screen.app.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.Tabs
import io.github.jan.einkaufszettel.ui.screen.app.tabs.home.HomeTab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.RecipesTab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.settings.SettingsTab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.ShopsTab

@Composable
internal fun AppNavigationRail() {
    NavigationRail {
        Tabs.ALL.forEach {
            TabNavigationRailItem(it)
        }
    }
}

@Composable
internal fun TabNavigationRailItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationRailItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, tab.options.title) }
    )
}