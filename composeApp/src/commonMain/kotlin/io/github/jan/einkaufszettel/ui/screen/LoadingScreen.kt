package io.github.jan.einkaufszettel.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import io.github.jan.einkaufszettel.ui.component.LoadingCircle

object LoadingScreen: Screen {

    @Composable
    override fun Content() {
        LoadingCircle()
    }

}