package io.github.jan.einkaufszettel.ui.screen.app.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.Tabs
import io.github.jan.einkaufszettel.ui.screen.app.tabs.home.HomeTab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.RecipesTab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.settings.SettingsTab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.ShopsTab

@Composable
internal fun AppNavigationBar() {
    NavigationBar {
        Tabs.ALL.forEach {
            TabNavigationItem(it)
        }
    }
}

@Composable
internal fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, tab.options.title) }
    )
}