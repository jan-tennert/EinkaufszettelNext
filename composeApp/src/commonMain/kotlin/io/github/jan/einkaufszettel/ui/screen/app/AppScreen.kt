package io.github.jan.einkaufszettel.ui.screen.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import io.github.jan.einkaufszettel.ui.screen.app.tabs.home.HomeTab
import io.github.jan.einkaufszettel.ui.screen.app.navigation.AppNavigationBar
import io.github.jan.einkaufszettel.ui.screen.app.navigation.AppNavigationRail
import io.github.jan.einkaufszettel.ui.screen.app.navigation.AppNavigationTopBar
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget
import org.koin.compose.koinInject

object AppScreen: Screen {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<AppScreenModel>()
        val windowSizeClass = calculateWindowSizeClass()
        val imageLoader = koinInject<ImageLoader>()
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
                        CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                            CurrentTab()
                        }
                    }
                }
            }
        }
    }

}