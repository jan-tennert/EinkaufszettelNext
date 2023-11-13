package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.data.remote.ProfileDto
import io.github.jan.einkaufszettel.ui.screen.app.tabs.components.OutlinedSTextField

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserProfileList(
    profiles: List<ProfileDto>,
    selectedUsers: SnapshotStateList<String>,
    addUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddUserDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserProfileListHeader()
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            items(profiles) {
                UserProfileListItem(
                    profile = it,
                    selected = selectedUsers.contains(it.id),
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    onSelectChange = { selected ->
                        if (selected) {
                            selectedUsers.add(it.id)
                        } else {
                            selectedUsers.remove(it.id)
                        }
                    }
                )
            }
        }

        Button(
            onClick = {
                showAddUserDialog = true
            }
        ) {
            Text(Res.string.add_user)
        }
        Spacer(Modifier.height(8.dp))
    }

    if (showAddUserDialog) {
        AddUserDialog(
            onAdd = { id ->
                showAddUserDialog = false
                addUser(id)
            },
            onDismiss = { showAddUserDialog = false }
        )
    }
}

@Composable
private fun AddUserDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var id by remember { mutableStateOf(TextFieldValue()) }
    AlertDialog(
        title = { Text(Res.string.add_user) },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Res.string.cancel)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onAdd(id.text)
            }) {
                Text(Res.string.add)
            }
        },
        text = {
            OutlinedSTextField(
                value = id,
                onValueChange = {
                    id = it
                },
                label = { Text(Res.string.user_id) },
                singleLine = true
            )
        }
    )
}

@Composable
private fun UserProfileListItem(
    profile: ProfileDto,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onSelectChange: (Boolean) -> Unit = {},
) {
    ElevatedCard(modifier) {
        ListItem(
            modifier = Modifier.fillMaxWidth(),
            headlineContent = { Text(profile.username) },
            leadingContent = {
                Checkbox(
                    checked = selected,
                    onCheckedChange = onSelectChange
                )
            },
            trailingContent = {
                IconButton({}) {
                    Icon(Icons.Filled.Delete, null)
                }
            }
        )
    }
}

@Composable
private fun UserProfileListHeader() {
    Box(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(Res.string.authorized_users, style = MaterialTheme.typography.headlineSmall)
    }
}