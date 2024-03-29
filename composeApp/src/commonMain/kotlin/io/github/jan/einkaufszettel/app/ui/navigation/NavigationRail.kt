package io.github.jan.einkaufszettel.app.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import io.github.jan.einkaufszettel.app.ui.Tabs

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