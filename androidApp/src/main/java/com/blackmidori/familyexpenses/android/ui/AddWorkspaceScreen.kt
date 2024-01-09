package com.blackmidori.familyexpenses.android.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.blackmidori.familyexpenses.android.core.HttpClientJavaImpl
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.repositories.WorkspaceRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
fun AddWorkspaceScreen(onFinish: ()->Unit){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var name by remember {
        mutableStateOf("")
    }
    Column {
        TextField(value = name, onValueChange = {
            name = it
        })
        Button(onClick = {
            Thread {
                val workspace = Workspace("", Instant.DISTANT_PAST, name)
                val workspacesResult =
                    WorkspaceRepository(httpClient = HttpClientJavaImpl()).add(workspace)
                if (workspacesResult.isFailure) {
                    Log.w("AddWorkspaceScreen", "Error: " + workspacesResult.exceptionOrNull())

                    coroutineScope.launch {
                        Toast.makeText(
                            context,
                            "Error: ${workspacesResult.exceptionOrNull()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{
                    coroutineScope.launch {
                        Toast.makeText(
                            context,
                            "Added",
                            Toast.LENGTH_SHORT
                        ).show()
                        onFinish()
                    }
                }
            }.start()
        }) {
            Text("Submit")
        }
    }
}