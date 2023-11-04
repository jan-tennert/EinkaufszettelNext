package io.github.jan.einkaufszettel.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.koin.core.scope.Scope
import org.w3c.dom.Worker

actual fun Scope.createDriver(): SqlDriver {
    return WebWorkerDriver(
        Worker(
            js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
        )
    )
}