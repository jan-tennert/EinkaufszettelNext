package io.github.jan.einkaufszettel.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import io.github.jan.einkaufszettel.ui.component.LoadingCircle

object LoadingScreen: Screen {

    @Composable
    override fun Content() {
        LoadingCircle()
    }

}