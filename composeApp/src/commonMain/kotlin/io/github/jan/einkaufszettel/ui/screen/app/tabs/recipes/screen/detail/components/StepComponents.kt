package io.github.jan.einkaufszettel.ui.screen.app.tabs.recipes.screen.detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
        val stepState = rememberSaveable(saver = RichTextState.Saver, inputs = arrayOf(steps)) {
            RichTextState().apply {
                setHtml(steps.ifBlank { Res.string.empty_steps })
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
fun StepCard(
    steps: RichTextState,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier) {
        RichText(steps, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(8.dp), fontStyle = if(steps.annotatedString.text.isBlank()) FontStyle.Italic else FontStyle.Normal)
    }
}