package io.github.jan.einkaufszettel.ui.screen.app.tabs.components

import kotlinx.browser.window
import kotlinx.coroutines.await

actual suspend fun readClipboard(): String = window.navigator.clipboard.readText()
    .catch {
        console.error("Error reading clipboard: $it")
        ""
    }.await()