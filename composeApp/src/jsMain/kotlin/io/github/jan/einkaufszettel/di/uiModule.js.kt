package io.github.jan.einkaufszettel.di

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import io.github.jan.supabase.imageloader.ImageLoaderIntegration
import org.koin.core.scope.Scope

actual fun Scope.createImageLoader(): ImageLoader {
    return ImageLoader {
        components {
            setupDefaultComponents()
            commonComponents(this)
        }
        interceptor {
            memoryCacheConfig {
                // Set the max size to 25% of the app's available memory.
                maxSizePercent(1.0)
            }
        }
    }
}