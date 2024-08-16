package io.github.jan.einkaufszettel.shops.ui.screen.create

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
import io.github.jan.einkaufszettel.app.ui.BlankScreen
import io.github.jan.einkaufszettel.app.ui.components.ChildTopBar
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.root.ui.component.LocalImage
import io.github.jan.einkaufszettel.root.ui.dialog.LoadingDialog
import io.github.jan.einkaufszettel.shops.ui.components.UserProfileList
import io.github.jan.einkaufszettel.shops.ui.screen.main.ShopScreen
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType

object ShopCreateStateScreen: AppStateScreen<ShopCreateScreenModel> {

    @Composable
    override fun createScreenModel(): ShopCreateScreenModel {
        return getScreenModel<ShopCreateScreenModel>()
    }

    @Composable
    override fun Content(screenModel: ShopCreateScreenModel, state: AppState) {
        val userProfiles by screenModel.userProfiles.collectAsStateWithLifecycle()
        val imageData by screenModel.imageData.collectAsStateWithLifecycle()
        val name by screenModel.name.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow
        val launcher = rememberFilePickerLauncher(mode = PickerMode.Single, type = PickerType.Image) {
            it?.let { file -> screenModel.importNativeFile(file) }
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ChildTopBar(Res.string.create_list, navigator)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        imageData?.let {
                            screenModel.createShop() }
                            //verify params
                        },
                ) {
                    Icon(Icons.Filled.Done, null)
                }
            }
        ) {
            Column(
                modifier = Modifier.padding(it).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = screenModel::setName,
                    label = { Text(Res.string.name) },
                    singleLine = true
                )
                Box(Modifier.padding(8.dp)) {
                    LocalImage(
                        imageData,
                        modifier = Modifier
                            .size(128.dp)
                            .border(2.dp, MaterialTheme.colorScheme.onSurface)
                            .clickable { launcher.launch() }
                    )
                }
                UserProfileList(
                    profiles = userProfiles,
                    selectedUsers = screenModel.authorizedUsers,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    addUser = { id ->
                        screenModel.importUser(id)
                    }
                )
            }
        }

        when(state) {
            is AppState.Loading -> {
                LoadingDialog()
            }
            is ShopCreateScreenModel.State.Success -> {
                ShopCreateDialog {
                    screenModel.resetState()
                    screenModel.resetContent()
                    if(CurrentPlatformTarget == PlatformTarget.ANDROID) {
                        navigator.push(ShopScreen)
                    } else {
                        navigator.replace(BlankScreen)
                    }
                }
            }
        }
    }

    @Composable
    private fun ShopCreateDialog(
        onClose: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(Res.string.list_created) },
            text = { Text(Res.string.list_created_message) },
            confirmButton = {
                TextButton(
                    onClick = onClose
                ) {
                    Text("Ok")
                }
            }
        )
    }

}