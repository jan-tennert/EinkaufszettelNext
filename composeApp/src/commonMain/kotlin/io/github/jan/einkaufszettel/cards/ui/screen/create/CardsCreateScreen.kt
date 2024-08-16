package io.github.jan.einkaufszettel.cards.ui.screen.create

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
import io.github.jan.einkaufszettel.cards.ui.screen.main.CardsScreen
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.root.ui.component.LocalImage
import io.github.jan.einkaufszettel.root.ui.dialog.LoadingDialog
import io.github.jan.einkaufszettel.shops.ui.components.UserProfileList
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType

object CardsCreateScreen: AppStateScreen<CardsCreateScreenModel> {

    @Composable
    override fun createScreenModel(): CardsCreateScreenModel {
        return getScreenModel<CardsCreateScreenModel>()
    }

    @Composable
    override fun Content(screenModel: CardsCreateScreenModel, state: AppState) {
        val userProfiles by screenModel.userProfiles.collectAsStateWithLifecycle()
        val imageData by screenModel.imageData.collectAsStateWithLifecycle()
        val description by screenModel.description.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow
        val launcher = rememberFilePickerLauncher(mode = PickerMode.Single, type = PickerType.Image) {
            it?.let { file -> screenModel.importNativeFile(file) }
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ChildTopBar(Res.string.create_card, navigator)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        imageData?.let { screenModel.createCard() }
                        //check for missing data
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
                    value = description,
                    onValueChange = screenModel::setDescription,
                    label = { Text(Res.string.description) },
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

       /* if(state is CardsCreateScreenModel.State.CreateSuccess) {
            SideEffect {
                navigator.replace(BlankScreen)
            }
        }*/

        when(state) {
            is AppState.Loading -> {
                LoadingDialog()
            }
            is CardsCreateScreenModel.State.CreateSuccess -> {
                CardCreatedDialog {
                    screenModel.resetContent()
                    screenModel.resetState()
                    if(CurrentPlatformTarget == PlatformTarget.ANDROID) {
                        navigator.push(CardsScreen)
                    } else {
                        navigator.replace(BlankScreen)
                    }
                }
            }
        }
    }

    @Composable
    private fun CardCreatedDialog(
        onClose: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(Res.string.card_created) },
            text = { Text(Res.string.card_created_message) },
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