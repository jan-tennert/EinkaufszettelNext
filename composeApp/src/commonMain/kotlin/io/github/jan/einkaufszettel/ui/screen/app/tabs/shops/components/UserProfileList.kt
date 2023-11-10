package io.github.jan.einkaufszettel.ui.screen.app.tabs.shops.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jan.einkaufszettel.Res
import io.github.jan.einkaufszettel.data.remote.ProfileDto

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserProfileList(
    profiles: List<ProfileDto>,
    selectedUsers: SnapshotStateList<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader {
            UserProfileListHeader()
        }
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
    Text(Res.string.authorized_users, style = MaterialTheme.typography.headlineSmall)
}