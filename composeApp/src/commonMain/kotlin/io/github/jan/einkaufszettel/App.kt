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
import io.github.jan.einkaufszettel.data.local.db.DatabaseProvider
import io.github.jan.einkaufszettel.theme.AppTheme
import io.github.jan.einkaufszettel.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.ui.screen.RootScreen
import io.github.jan.supabase.gotrue.GoTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun App() = AppTheme {
    Surface(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
        color = MaterialTheme.colorScheme.background
    ) {
        var dbInitialized by rememberSaveable { mutableStateOf(false) }
        val databaseProvider = koinInject<DatabaseProvider>()
        val gotrue = koinInject<GoTrue>()

        LaunchedEffect(Unit) {
            launch(Dispatchers.Default) {
                databaseProvider.initDatabase()
                dbInitialized = true
            }
            launch(Dispatchers.Default) {
                gotrue.checkForCode()
            }
        }

        if(dbInitialized) {
            Navigator(RootScreen)
        } else {
            LoadingCircle()
        }
    }
}