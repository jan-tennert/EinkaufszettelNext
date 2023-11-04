package io.github.jan.einkaufszettel.ui.screen.app.navigation

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import io.github.jan.einkaufszettel.ui.screen.app.tabs.Tabs
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppNavigationTopBar() {
    val tabNavigator = LocalTabNavigator.current
    if(CurrentPlatformTarget == PlatformTarget.JS) {
        AppNavigationTabBar()
    } else {
        TopAppBar(
            title = { Text(tabNavigator.current.options.title) }
        )
    }
}

@Composable
private fun AppNavigationTabBar() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Tabs.ALL.forEach {
                TabBarItem(it)
            }
        }
        Divider(Modifier.fillMaxWidth().padding(vertical = 4.dp))
    }
}

@Composable
private fun TabBarItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(60))
            .selectable(
                tabNavigator.current == tab,
                onClick = { tabNavigator.current = tab },
            )
            .padding(8.dp)
    ) {
        Icon(painter = tab.options.icon!!, tab.options.title, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(4.dp))
        Text(tab.options.title, color = MaterialTheme.colorScheme.primary)
    }
}