package io.github.jan.einkaufszettel.ui.screen.app.tabs.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

expect suspend fun readClipboard(): String

@Composable
fun OutlinedSTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    singleLine: Boolean = true,
    label: @Composable (() -> Unit)? = null,
) {
    var shortcutEvent: ShortcutEvent? by remember { mutableStateOf(null) }
    var isCtrlPressed by remember { mutableStateOf(false) }

    LaunchedEffect(shortcutEvent) {
        ShortcutEventHandler(
            shortcutEvent = shortcutEvent,
            value = value,
            onValueChange = onValueChange,
        )
        shortcutEvent = null
    }
    Column(
        modifier = Modifier
            .onPreviewKeyEvent {
                isCtrlPressed = it.isCtrlPressed
                shortcutEvent = it.filterKeyDown()?.toShortcutEvent()
                false
            }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (!isCtrlPressed) onValueChange(it)
            },
            modifier = modifier,
            singleLine = singleLine,
            label = label,
        )
    }
}
enum class ShortcutEvent {
    CUT, COPY, PASTE, HIGHLIGHT_ALL
}

suspend fun ShortcutEventHandler(
    shortcutEvent: ShortcutEvent?,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
) {
    shortcutEvent ?: return
    when (shortcutEvent) {
        ShortcutEvent.CUT -> {
            // The cut function seems to work same as copy, so we clear the selected value.
            onValueChange(value.replaceSelected(""))
        }
        ShortcutEvent.COPY -> {
            // Unused - seems to work out of the box
        }
        ShortcutEvent.PASTE -> {
            try {
                val clipboardText = readClipboard()
                onValueChange(value.replaceSelected(clipboardText))
            } catch(e: Throwable) {
                //ignore
            }
        }
        ShortcutEvent.HIGHLIGHT_ALL -> {
            onValueChange(value.copy(selection = TextRange(0, value.text.length)))
        }
    }
}

fun KeyEvent.filterKeyDown() =
    if (type == KeyEventType.KeyDown) this else null

fun KeyEvent.toShortcutEvent() = when {
    isCtrlPressed && key == Key.X -> ShortcutEvent.CUT
    isCtrlPressed && key == Key.V -> ShortcutEvent.PASTE
    isCtrlPressed && key == Key.A -> ShortcutEvent.HIGHLIGHT_ALL
    else -> null
}

private fun TextFieldValue.replaceSelected(replacement: String) =
    copy(text = text.replaceRange(selection.min, selection.max, replacement), TextRange(0, 0))