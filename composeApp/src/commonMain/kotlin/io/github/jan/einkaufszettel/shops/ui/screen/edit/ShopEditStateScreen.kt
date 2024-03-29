package io.github.jan.einkaufszettel.shops.ui.screen.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.app.ui.AppState
import io.github.jan.einkaufszettel.app.ui.AppStateScreen
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.getScreenModel
import io.github.jan.einkaufszettel.root.ui.component.LoadingCircle
import io.github.jan.einkaufszettel.shops.ui.components.UserProfileList
import io.github.jan.einkaufszettel.shops.ui.screen.main.BlankScreen
import io.github.jan.supabase.CurrentPlatformTarget
import io.github.jan.supabase.PlatformTarget
import org.koin.core.parameter.parametersOf

class ShopEditStateScreen(
    private val shopId: Long,
): AppStateScreen<ShopEditScreenModel> {

    @Composable
    override fun createScreenModel(): ShopEditScreenModel {
        return getScreenModel<ShopEditScreenModel>(tag = shopId.toString(), parameters = { parametersOf(shopId) })
    }

    @Composable
    override fun Content(screenModel: ShopEditScreenModel, state: AppState) {
        val shop by screenModel.shop.collectAsStateWithLifecycle()
        if(shop == null) {
            LoadingCircle()
            return
        }
        val navigator = LocalNavigator.currentOrThrow
        val userProfiles by screenModel.userProfiles.collectAsStateWithLifecycle()
        var name by remember { mutableStateOf(shop!!.name) }
        val authorizedUsers = remember { mutableStateListOf(*shop!!.authorizedUsers.toTypedArray()) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ShopEditTopBar(navigator)
            },
            floatingActionButton = {
                if(state is AppState.Loading) {
                    CircularProgressIndicator()
                } else {
                    FloatingActionButton(
                        onClick = { screenModel.updateShop(name, authorizedUsers) },
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

        if(state is ShopEditScreenModel.State.Success) {
            SideEffect {
                navigator.replace(BlankScreen)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ShopEditTopBar(navigator: Navigator) {
        CenterAlignedTopAppBar(
            title = {
                Text(Res.string.edit_list, style = MaterialTheme.typography.headlineMedium)
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