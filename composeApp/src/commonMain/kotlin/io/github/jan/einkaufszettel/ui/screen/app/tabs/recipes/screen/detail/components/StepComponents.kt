package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import io.github.jan.einkaufszettel.Res

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.StepDetailContent(
    steps: String
) {
    stickyHeader {
        Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(8.dp), contentAlignment = Alignment.Center) {
            Text(Res.string.steps, style = MaterialTheme.typography.headlineSmall)
        }
    }

    item {
        val stepState = rememberSaveable(saver = RichTextState.Saver) {
            RichTextState().apply {
                setHtml(steps)
            }
        }
        StepCard(
            steps = stepState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepCard(
    steps: RichTextState,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier) {
        RichText(steps, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxSize().padding(8.dp))
    }
}