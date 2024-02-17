package com.blackmidori.familyexpenses.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.familyexpenses.android.AppScreen
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.android.R
import com.blackmidori.familyexpenses.android.shared.ui.SimpleAppBar
import com.blackmidori.familyexpenses.android.shared.ui.SimpleScaffold
import com.blackmidori.familyexpenses.models.Workspace

@Composable
fun HomeScreen(
    navController: NavHostController,
    list: Array<Workspace> = arrayOf(),
    onClickUpdateWorkspace: (workspaceId: String) -> Unit = {},
    onClickOpenWorkspace: (workspaceId: String) -> Unit = {},
    onClickAddWorkspace: () -> Unit = {},
) {
    SimpleScaffold(
        topBar = {
            SimpleAppBar(
                navController = navController,
                title = { Text(stringResource(AppScreen.Home.title)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onClickAddWorkspace) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_workspace)
                )
            }
        }
    ) {

        LazyColumn {
            item { Text("Workspace Count: ${list.size}") }
            for (item in list) {
                item {

                    ListItem(
                        modifier = Modifier.clickable {
                            onClickOpenWorkspace(item.id)
                        },
                        headlineContent = { Text(item.name) },
                        supportingContent = { Text(item.creationDateTime.toString()) },
                        trailingContent = {
                            IconButton({
                                onClickUpdateWorkspace(item.id)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = stringResource(R.string.update_workspace)
                                )
                            }

                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        HomeScreen(rememberNavController())
    }
}