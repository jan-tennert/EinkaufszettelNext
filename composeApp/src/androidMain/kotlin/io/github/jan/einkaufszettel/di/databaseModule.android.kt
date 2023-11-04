package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.data.local.db.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

actual fun Scope.createDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory(androidContext())