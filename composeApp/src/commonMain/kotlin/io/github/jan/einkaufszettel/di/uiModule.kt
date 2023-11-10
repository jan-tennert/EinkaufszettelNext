package io.github.jan.einkaufszettel.di

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.component.ComponentRegistryBuilder
import io.github.jan.einkaufszettel.data.local.image.LocalImageFetcher
import io.github.jan.supabase.imageloader.ImageLoaderIntegration
import org.koin.core.scope.Scope
import org.koin.dsl.module

expect fun Scope.createImageLoader(): ImageLoader

val uiModule = module {
    single {
        createImageLoader()
    }
}

fun Scope.commonComponents(componentRegistryBuilder: ComponentRegistryBuilder) {
    with(componentRegistryBuilder) {
        add(fetcherFactory = get<ImageLoaderIntegration>())
        add(keyer = get<ImageLoaderIntegration>())
        add(LocalImageFetcher)
    }
}