package io.github.jan.einkaufszettel.ui.screen.app.pullrefresh

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.dialog.ErrorDialog
import io.github.jan.einkaufszettel.ui.screen.app.AppScreenModel
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun RefreshScope(
    navigator: Navigator,
    type: AppScreenModel.RefreshType,
    content: @Composable BoxScope.() -> Unit
) {
    if(CurrentPlatformTarget != PlatformTarget.ANDROID) {
        Box(Modifier.fillMaxSize(), content = content)
        return;
    }
    val appScreenModel = navigator.getNavigatorScreenModel<AppScreenModel>()
    val appScreenModelState by appScreenModel.state.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = appScreenModelState == AppScreenModel.State.Loading,
        onRefresh = { appScreenModel.refresh(false, type) }
    )

    Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
        content()
        PullRefreshIndicator(
            refreshing = appScreenModelState == AppScreenModel.State.Loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    when(appScreenModelState) {
        is AppScreenModel.State.Error -> {
            ErrorDialog((appScreenModelState as AppScreenModel.State.Error).message, onDismiss = appScreenModel::resetState)
        }
        AppScreenModel.State.NetworkError -> {
            ErrorDialog(Res.string.network_error, onDismiss = appScreenModel::resetState)
        }
        else -> {}
    }
}