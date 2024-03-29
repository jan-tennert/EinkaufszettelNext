package io.github.jan.einkaufszettel.app.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.input.pointer.PointerInputScope
import org.jetbrains.skiko.SkikoMouseButtons
import org.jetbrains.skiko.SkikoPointerEvent
import org.jetbrains.skiko.SkikoPointerEventKind

internal actual suspend fun PointerInputScope.handleClicks(
    interactionSource: MutableInteractionSource,
    onLeftClick: () -> Unit,
    onRightClick: (x: Float, y: Float) -> Unit
) {
    awaitPointerEventScope {
        var press: PressInteraction.Press? = null
        while (true) {
            val event = awaitPointerEvent()
            val nativeEvent = event.nativeEvent
            if (nativeEvent is SkikoPointerEvent) {
                if (nativeEvent.button == SkikoMouseButtons.RIGHT && nativeEvent.kind == SkikoPointerEventKind.UP) {
                    val position = event.changes.first().position
                    event.changes.forEach { it.consume() } // or similar, would need to check if this works nicely with other registered listeners
                    onRightClick(position.x, position.y)
                } else if (nativeEvent.button == SkikoMouseButtons.LEFT && nativeEvent.kind == SkikoPointerEventKind.DOWN) {
                    val position = event.changes.first().position
                    press = PressInteraction.Press(position)
                    interactionSource.tryEmit(press)
                    event.changes.forEach { it.consume() } // or similar, would need to check if this works nicely with other registered listeners

                } else if(nativeEvent.button == SkikoMouseButtons.LEFT && nativeEvent.kind == SkikoPointerEventKind.UP) {
                    event.changes.forEach { it.consume() } // or similar, would need to check if this works nicely with other registered listeners
                    press?.let {
                        interactionSource.tryEmit(PressInteraction.Release(press))
                    }
                    onLeftClick()
                }
            }
        }
    }
}