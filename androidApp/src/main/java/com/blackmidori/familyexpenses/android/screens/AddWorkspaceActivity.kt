package com.blackmidori.familyexpenses.android.screens

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.android.core.HttpClientJavaImpl
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.repositories.WorkspaceRepository
import kotlinx.datetime.Instant

class AddWorkspaceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar()
                    }
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        Body()
                    }
                }
            }
        }
    }

    @Composable
    fun TopBar() {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Add Workspace", Modifier.align(Alignment.Center))
        }
    }

    @Composable
    fun Body() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
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
                            Log.w(TAG, "Error: " + workspacesResult.exceptionOrNull())

                            this@AddWorkspaceActivity.runOnUiThread {
                                Toast.makeText(
                                    context,
                                    "Error: ${workspacesResult.exceptionOrNull()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@Thread;
                        }

                        this@AddWorkspaceActivity.runOnUiThread {
                            Toast.makeText(
                                context,
                                "Added",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        finish()
                    }.start()
                }) {
                    Text("Submit")
                }
            }
        }
    }

    @Preview
    @Composable
    fun Preview() {
        MyApplicationTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopBar()
                }
            ) { innerPadding ->
                Box(Modifier.padding(innerPadding)) {
                    Body()
                }
            }
        }
    }

    companion object {
        private val TAG = AddWorkspaceActivity::class.java.simpleName
    }
}