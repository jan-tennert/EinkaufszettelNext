package io.github.jan.einkaufszettel.app.ui.pullrefresh

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.Navigator
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppScreenModel
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.root.ui.dialog.ErrorDialog
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

@OptIn(ExperimentalVoyagerApi::class, ExperimentalMaterial3Api::class)
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
    val pullRefreshState = rememberPullToRefreshState()

    if(pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            appScreenModel.refresh(false, type) {
                pullRefreshState.endRefresh()
            }
        }
    }

    Box(modifier = Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
        Box(Modifier.fillMaxSize(), content = content)
        PullToRefreshContainer(pullRefreshState, modifier = Modifier.align(Alignment.TopCenter))
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