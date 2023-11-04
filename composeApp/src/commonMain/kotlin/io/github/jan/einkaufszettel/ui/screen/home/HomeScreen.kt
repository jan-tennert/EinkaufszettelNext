package io.github.jan.einkaufszettel.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

object HomeScreen: Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<HomeScreenModel>()

        Box {
            Button(onClick = screenModel::logout) {
                Text("Logout")
            }
        }
    }

}