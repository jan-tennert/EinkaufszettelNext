package io.github.jan.einkaufszettel.di

import com.seiko.imageloader.ImageLoader
import org.koin.core.scope.Scope
import org.koin.dsl.module

expect fun Scope.createImageLoader(): ImageLoader

val uiModule = module {
    single {
        createImageLoader()
    }
}