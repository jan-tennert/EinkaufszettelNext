package io.github.jan.einkaufszettel.data.local.image

import com.seiko.imageloader.component.fetcher.FetchResult
import com.seiko.imageloader.component.fetcher.Fetcher
import com.seiko.imageloader.option.Options
import okio.Buffer

class LocalImageFetcher(
    private val localImageData: LocalImageData
): Fetcher {

    override suspend fun fetch(): FetchResult {
        return FetchResult.OfSource(Buffer().apply {
            write(localImageData.data)
        })
    }

    companion object : Fetcher.Factory {

        override fun create(data: Any, options: Options): Fetcher? {
            if(data !is LocalImageData) return null
            return LocalImageFetcher(data)
        }

    }

}