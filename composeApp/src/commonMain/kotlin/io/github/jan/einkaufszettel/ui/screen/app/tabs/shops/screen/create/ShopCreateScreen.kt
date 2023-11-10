package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.create

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.component.LocalImage
import io.github.jan.einkaufszettel.ui.dialog.ErrorDialog
import io.github.jan.einkaufszettel.ui.screen.app.tabs.components.CreateButton
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components.UserProfileList
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.ShopProductScreenModel
import io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.screen.main.BlankScreen
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget

object ShopCreateScreen: Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ShopCreateScreenModel>()
        val userProfiles by screenModel.userProfiles.collectAsStateWithLifecycle()
        val imageData by screenModel.imageData.collectAsStateWithLifecycle()
        var showImageDialog by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        val authorizedUsers = remember { mutableStateListOf<String>() }
        val navigator = LocalNavigator.currentOrThrow
        val screenModelState by screenModel.state.collectAsStateWithLifecycle()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ShopCreateTopBar(navigator)
            },
            floatingActionButton = {
                if(screenModelState is ShopCreateScreenModel.State.Loading) {
                    CircularProgressIndicator()
                } else {
                    FloatingActionButton(
                        onClick = { imageData?.let { screenModel.createShop(name, it, authorizedUsers) } },
                    ) {
                        Icon(Icons.Filled.Done, null)
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier.padding(it).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Res.string.name) },
                    singleLine = true
                )
                Box(Modifier.padding(8.dp)) {
                    LocalImage(
                        imageData,
                        modifier = Modifier
                            .size(128.dp)
                            .border(2.dp, MaterialTheme.colorScheme.onSurface)
                            .clickable { showImageDialog = true }
                    )
                }
                UserProfileList(
                    profiles = userProfiles,
                    selectedUsers = authorizedUsers,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
        }
        FilePicker(
            show = showImageDialog,
            fileExtensions = listOf("png", "jpg", "jpeg"),
            onFileSelected = {
                showImageDialog = false
                it?.platformFile?.let { file -> screenModel.importNativeFile(file) }
            }
        )

        when(screenModelState) {
            is ShopCreateScreenModel.State.Error -> {
                ErrorDialog((screenModelState as ShopCreateScreenModel.State.Error).message, screenModel::resetState)
            }
            ShopCreateScreenModel.State.NetworkError -> {
                ErrorDialog(Res.string.network_error, screenModel::resetState)
            }
            ShopCreateScreenModel.State.Success -> {
                SideEffect {
                    navigator.pop()
                }
            }
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ShopCreateTopBar(navigator: Navigator) {
        CenterAlignedTopAppBar(
            title = {
                Text(Res.string.create_list, style = MaterialTheme.typography.headlineMedium)
            },
            actions = {
                if(CurrentPlatformTarget == PlatformTarget.JS) {
                    IconButton(
                        onClick = { navigator.replace(BlankScreen) }
                    ) {
                        Icon(Icons.Filled.Close, null)
                    }
                }
            }
        )
    }

}