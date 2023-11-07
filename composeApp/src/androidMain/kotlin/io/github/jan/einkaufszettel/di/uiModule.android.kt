package io.github.jan.einkaufszettel.di

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.defaultImageResultMemoryCache
import com.seiko.imageloader.option.androidContext
import com.seiko.imageloader.util.LogPriority
import com.seiko.imageloader.util.Logger
import io.github.jan.supabase.imageloader.ImageLoaderIntegration
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

actual fun Scope.createImageLoader(): ImageLoader {
    return ImageLoader {
        options {
            androidContext(androidContext())
        }
        components {
            setupDefaultComponents()
            add(fetcherFactory = get<ImageLoaderIntegration>())
            add(keyer = get<ImageLoaderIntegration>())
        }
        interceptor {
            // cache 100 success image result, without bitmap
            defaultImageResultMemoryCache()
            memoryCacheConfig {
                // Set the max size to 25% of the app's available memory.
                maxSizePercent(androidContext(), 0.25)
            }
            diskCacheConfig {
                directory(androidContext().cacheDir.resolve("image_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}