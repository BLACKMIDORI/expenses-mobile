package com.blackmidori.familyexpenses.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    SimpleScaffold(topBar = {
        SimpleAppBar(
            navController = navController,
            title = { Text(stringResource(AppScreen.Home.title)) },
        )
    }) {
        LazyColumn {
            item {
                Button(onClick = onClickAddWorkspace) {
                    Text("Add Workspace")
                }
            }
            item { Text("Workspace Count: ${list.size}") }
            for (workspace in list) {
                item {
                    Row {
                        Button(onClick = {
                            onClickOpenWorkspace(workspace.id)
                        }) {
                            Column {
                                Text(workspace.name)
                                Text(workspace.creationDateTime.toString())
                            }
                        }
                        Button(onClick = {
                            onClickUpdateWorkspace(workspace.id)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.back_button)
                            )
                        }
                    }
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