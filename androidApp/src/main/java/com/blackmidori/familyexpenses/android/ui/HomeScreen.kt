package com.blackmidori.familyexpenses.android.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.blackmidori.familyexpenses.Session
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.models.Workspace

@Composable
fun HomeScreen(
    list: Array<Workspace> = arrayOf(),
    onClickUpdateWorkspace: (workspaceId: String) -> Unit = {},
    onClickAddWorkspace: () -> Unit = {},
) {
    val context = LocalContext.current

    LazyColumn {
        item {
            Button(onClick = onClickAddWorkspace) {
                Text("Add Workspace")
            }
        }
        item { Text("User: ${Session.appUser.id}") }
        item { Text("Workspace Count: ${list.size}") }
        for (workspace in list) {
            item {
                Button(onClick = {
                    onClickUpdateWorkspace(workspace.id)
                }) {
                    Column {
                        Text(workspace.name)
                        Text(workspace.creationDateTime.toString())
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    MyApplicationTheme {
        HomeScreen()
    }
}