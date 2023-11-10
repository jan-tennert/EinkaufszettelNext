package io.github.jan.einkaufszettel.di

import io.github.jan.einkaufszettel.data.local.image.LocalImageReader
import org.koin.core.scope.Scope

actual fun Scope.createLocalImageReader(): LocalImageReader = LocalImageReader()