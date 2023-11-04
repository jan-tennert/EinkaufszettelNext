package io.github.jan.einkaufszettel.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.collectAsStateWithLifecycle
import io.github.jan.einkaufszettel.ui.dialog.ErrorDialog
import io.github.jan.einkaufszettel.ui.dialog.LoadingDialog
import io.github.jan.einkaufszettel.ui.screen.app.AppScreen

object ProfileCreateScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var name by remember { mutableStateOf("") }
        val screenModel = getScreenModel<ProfileCreateScreenModel>()
        val screenState by screenModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(screenState) {
            if(screenState is ProfileCreateScreenModel.State.Success) {
                navigator.push(AppScreen)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(Res.string.create_user_profile, style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(Res.string.name) },
                singleLine = true,
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { screenModel.createProfile(name) },
                enabled = name.isNotBlank()
            ) {
                Text(Res.string.create)
            }
        }

        when(screenState) {
            is ProfileCreateScreenModel.State.Error -> {
                ErrorDialog(
                    error = (screenState as ProfileCreateScreenModel.State.Error).message,
                    onDismiss = { screenModel.reset() }
                )
            }
            ProfileCreateScreenModel.State.Loading -> {
                LoadingDialog()
            }
            else -> {}
        }
    }

}