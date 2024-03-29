package io.github.jan.einkaufszettel.root.data.local.image

import coil3.Image
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options

class LocalImageFetcher(
    private val localImageData: LocalImageData
): Fetcher {

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun fetch(): FetchResult {
        return ImageFetchResult(createImage(localImageData), false, DataSource.MEMORY)
    }

    class Factory : Fetcher.Factory<LocalImageData> {

        override fun create(
            data: LocalImageData,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return LocalImageFetcher(data)
        }

    }

}

@OptIn(ExperimentalCoilApi::class)
expect fun createImage(localImageData: LocalImageData): Image