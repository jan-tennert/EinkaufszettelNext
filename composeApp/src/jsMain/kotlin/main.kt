import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import app.cash.sqldelight.async.coroutines.awaitCreate
import io.github.jan.einkaufszettel.App
import io.github.jan.einkaufszettel.db.Einkaufszettel
import io.github.jan.einkaufszettel.di.installModules
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        startKoin {
            installModules()
        }
        CanvasBasedWindow("Einkaufszettel") {
            App()
        }
    }
}
