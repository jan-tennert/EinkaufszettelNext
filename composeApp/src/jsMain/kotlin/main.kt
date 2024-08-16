import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.github.jan.einkaufszettel.App
import io.github.jan.einkaufszettel.root.di.installModules
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        installModules()
    }
    onWasmReady {
        CanvasBasedWindow("Einkaufszettel") {
            App()
        }
    }
}
