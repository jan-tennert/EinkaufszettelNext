package io.github.jan.einkaufszettel.ui.screen.app.tabs.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

@Composable
fun ContextMenuScope(
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit = {},
    items: @Composable ColumnScope.() -> Unit,
    content: @Composable (indication: InteractionSource) -> Unit
) {
    var isContextMenuVisible by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var itemHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val interactionSource = remember {
        MutableInteractionSource()
    }
    Box(
        modifier = Modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
            .let {
                if(CurrentPlatformTarget == PlatformTarget.ANDROID || true) {
                    it.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { offset ->
                                isContextMenuVisible = true
                                pressOffset = DpOffset(offset.x.toDp(), offset.y.toDp())
                            },
                            onPress = { offset ->
                                val press = PressInteraction.Press(offset)
                                interactionSource.emit(press)
                                tryAwaitRelease()
                                interactionSource.emit(PressInteraction.Release(press))
                            },
                            onTap = {
                                onItemClicked()
                            }
                        )
                    }
                } else {
                    it.pointerInput(Unit) {
                        awaitPointerEventScope {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Press &&
                                event.buttons.isSecondaryPressed
                            ) {
                                event.changes.forEach { e -> e.consume() }
                                println("right click")
                            } else if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                                event.changes.forEach { e -> e.consume() }
                                onItemClicked()
                            }
                        }
                    }
                }
            }
            .then(modifier)
    ) {
        content(interactionSource)
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false },
            offset = pressOffset.copy(y = pressOffset.y - itemHeight),
            content = items
        )
    }
}