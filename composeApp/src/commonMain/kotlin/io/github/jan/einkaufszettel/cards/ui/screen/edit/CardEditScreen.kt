package io.github.jan.einkaufszettel.cards.ui.screen.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import io.github.jan.einkaufszettel.root.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.root.ui.dialog.LoadingDialog
import io.github.jan.einkaufszettel.shops.ui.components.UserProfileList
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget
import org.koin.core.parameter.parametersOf

class CardEditScreen(
    private val cardId: Long,
): AppStateScreen<CardEditScreenModel> {

    @Composable
    override fun createScreenModel(): CardEditScreenModel {
        return getScreenModel<CardEditScreenModel>(tag = cardId.toString(), parameters = { parametersOf(cardId) })
    }

    @Composable
    override fun Content(screenModel: CardEditScreenModel, state: AppState) {
        val card by screenModel.card.collectAsStateWithLifecycle()
        if(card == null) {
            LoadingCircle()
            return
        }
        val navigator = LocalNavigator.currentOrThrow
        val userProfiles by screenModel.userProfiles.collectAsStateWithLifecycle()
        var description by remember { mutableStateOf(card!!.description) }
        val authorizedUsers = remember { mutableStateListOf(*card!!.authorizedUsers.toTypedArray()) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ChildTopBar(Res.string.edit_card, navigator)
            },
            floatingActionButton = {
                if(state is AppState.Loading) {
                    CircularProgressIndicator()
                } else {
                    FloatingActionButton(
                        onClick = { screenModel.updateCard(description, authorizedUsers) },
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(Res.string.description) },
                    singleLine = true
                )
                UserProfileList(
                    profiles = userProfiles,
                    selectedUsers = authorizedUsers,
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
            is CardEditScreenModel.State.Success -> {
                CardEditDialog {
                    screenModel.resetState()
                    if (CurrentPlatformTarget == PlatformTarget.ANDROID) {
                        navigator.push(CardsScreen)
                    } else {
                        navigator.replace(BlankScreen)
                    }
                }
            }
        }
    }

    @Composable
    private fun CardEditDialog(
        onClose: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(Res.string.card_edited) },
            text = { Text(Res.string.card_edited_message) },
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