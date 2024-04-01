package io.github.jan.einkaufszettel.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
actual fun Modifier.zoomable(): Modifier = this then zoomable(rememberZoomState())