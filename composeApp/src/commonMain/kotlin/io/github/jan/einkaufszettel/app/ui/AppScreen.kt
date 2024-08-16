package io.github.jan.einkaufszettel.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import io.github.jan.einkaufszettel.app.ui.navigation.AppNavigationBar
import io.github.jan.einkaufszettel.app.ui.navigation.AppNavigationRail
import io.github.jan.einkaufszettel.app.ui.navigation.AppNavigationTopBar
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.home.ui.HomeTab
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

object AppScreen: Screen {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<AppScreenModel>()
        val windowSizeClass = calculateWindowSizeClass()
       // val screenState by screenModel.state.collectAsStateWithLifecycle()

        LifecycleEffect(
            onStarted = {
                screenModel.refresh(silent = true)
            }
        )

        TabNavigator(HomeTab) { navigator ->
            Scaffold(
                topBar = {
                    AppNavigationTopBar()
                },
                bottomBar = {
                    if(windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                        AppNavigationBar()
                    }
                }
            ) {
                Row(Modifier.fillMaxSize().padding(it)) {
                    if(windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact && CurrentPlatformTarget == PlatformTarget.ANDROID) {
                        AppNavigationRail()
                    }
                    Box {
                        CurrentTab()
                    }
                }
            }
        }
    }

}