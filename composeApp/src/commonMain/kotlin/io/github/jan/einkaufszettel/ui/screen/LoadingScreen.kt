package io.github.jan.einkaufszettel.ui.screen

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object LoadingScreen: Screen {

    @Composable
    override fun Content() {
        CircularProgressIndicator()
    }

}