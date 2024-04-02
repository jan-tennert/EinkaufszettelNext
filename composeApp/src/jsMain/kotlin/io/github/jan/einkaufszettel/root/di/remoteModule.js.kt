package io.github.jan.einkaufszettel.root.di

import io.github.jan.einkaufszettel.update.data.remote.UpdateManager
import org.koin.core.scope.Scope

actual fun Scope.updateManager(): UpdateManager {
    return UpdateManager()
}