package io.github.jan.einkaufszettel.root.di

import io.github.jan.einkaufszettel.root.data.local.image.LocalImageReader
import org.koin.core.scope.Scope

actual fun Scope.createLocalImageReader(): LocalImageReader = LocalImageReader()