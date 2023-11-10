package io.github.jan.einkaufszettel.di.models

import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.ShopScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.ShopProductScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.create.ShopCreateScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.detail.ShopDetailScreenModel
import org.koin.dsl.module

val shopModels = module {
    factory {
        ShopScreenModel(get(), get(), get())
    }
    factory { parameters ->
        ShopDetailScreenModel(
            shopId = parameters.get(),
            productApi = get(),
            productDataSource = get(),
            auth = get()
        )
    }
    factory {
        ShopProductScreenModel(get(), get(), get())
    }
    factory {
        ShopCreateScreenModel(get(), get(), get(), get(), get())
    }
}