package io.github.jan.einkaufszettel.ui.screen.app.tabs.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.input.pointer.PointerInputScope

internal actual suspend fun PointerInputScope.handleClicks(
    interactionSource: MutableInteractionSource,
    onLeftClick: () -> Unit,
    onRightClick: (x: Float, y: Float) -> Unit
) {
}