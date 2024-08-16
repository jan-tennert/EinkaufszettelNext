package io.github.jan.einkaufszettel

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor2.KtorNetworkFetcherFactory
import io.github.jan.einkaufszettel.root.data.local.db.DatabaseProvider
import io.github.jan.einkaufszettel.root.data.local.image.LocalImageFetcher
import io.github.jan.einkaufszettel.root.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.root.ui.screen.RootScreen
import io.github.jan.einkaufszettel.root.ui.theme.AppTheme
import io.github.jan.einkaufszettel.update.ui.CheckForUpdates
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.coil.CoilIntegration
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun App() = AppTheme {
    val coil = koinInject<CoilIntegration>()
    setSingletonImageLoaderFactory {
        createImageLoader(it, coil)
    }
    Surface(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
        color = MaterialTheme.colorScheme.background
    ) {
        var dbInitialized by rememberSaveable { mutableStateOf(false) }
        val databaseProvider = koinInject<DatabaseProvider>()
        val auth = koinInject<Auth>()

        LaunchedEffect(Unit) {
            launch(Dispatchers.Default) {
                databaseProvider.initDatabase()
                dbInitialized = true
            }
            launch(Dispatchers.Default) {
                auth.checkForCode()
            }
        }

        if(dbInitialized) {
            Navigator(RootScreen)
        } else {
            LoadingCircle()
        }
    }
    CheckForUpdates()
}

@OptIn(SupabaseExperimental::class)
fun createImageLoader(context: PlatformContext, coilIntegration: CoilIntegration) = ImageLoader.Builder(context).apply {
    components {
        add(KtorNetworkFetcherFactory())
        add(coilIntegration)
        add(LocalImageFetcher.Factory())
    }
}.build()