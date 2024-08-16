package io.github.jan.einkaufszettel.app.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.input.pointer.PointerInputScope
import org.w3c.dom.events.MouseEvent

object SkikoMouseButtons {
    const val LEFT: Short = 0
    const val RIGHT: Short = 2
    const val TYPE_UP = "mouseup"
    const val TYPE_DOWN = "mousedown"
}

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
            println(nativeEvent)
            if(nativeEvent is MouseEvent) {
                println(nativeEvent.button)
                println(nativeEvent.type)
                println(nativeEvent.button == SkikoMouseButtons.LEFT && nativeEvent.type == SkikoMouseButtons.TYPE_UP)
                if (nativeEvent.button == SkikoMouseButtons.RIGHT && nativeEvent.type == SkikoMouseButtons.TYPE_UP) {
                    val position = event.changes.first().position
                    event.changes.forEach { it.consume() } // or similar, would need to check if this works nicely with other registered listeners
                    onRightClick(position.x, position.y)
                } else if (nativeEvent.button == SkikoMouseButtons.LEFT && nativeEvent.type == SkikoMouseButtons.TYPE_DOWN) {
                    val position = event.changes.first().position
                    press = PressInteraction.Press(position)
                    interactionSource.tryEmit(press)
                    event.changes.forEach { it.consume() } // or similar, would need to check if this works nicely with other registered listeners

                } else if(nativeEvent.button == SkikoMouseButtons.LEFT && nativeEvent.type == SkikoMouseButtons.TYPE_UP) {
                    println("hallo????")
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