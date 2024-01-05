package com.blackmidori.familyexpenses.android.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.blackmidori.familyexpenses.Session
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.android.core.HttpClientJavaImpl
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.repositories.WorkspaceRepository

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                var list by remember {
                    mutableStateOf(arrayOf<Workspace>())
                }
                OnLifecycleEvent { owner, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()
                            fetchWorkspacesAsync(context){
                                list = it
                            }
                        }

                        else -> {}
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar(onRefreshClick = {
                            fetchWorkspacesAsync(context){
                                list = it
                            }
                        })
                    }
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        Body(list)
                    }
                }
            }
        }
    }

    @Composable
    fun TopBar(onRefreshClick: (() -> Unit)? = null) {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            val context = LocalContext.current
            Text("Home", Modifier.align(Alignment.Center))
            IconButton(
                onClick = onRefreshClick ?: {},
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    Icons.Rounded.Refresh,
                    "Refresh"
                )
            }
            IconButton(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            AddWorkspaceActivity::class.java
                        )
                    )
                },
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Icon(
                    Icons.Rounded.Add,
                    "Add"
                )
            }
        }
    }

    @Composable
    fun Body(list: Array<Workspace> = arrayOf()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val context = LocalContext.current
            LazyColumn {
                item{Text("User: ${Session.appUser.id}")}
                item{Text("Workspace Count: ${list.size}")}
                for (workspace in list) {
                    item {
                        Button(onClick = {
                            Toast.makeText(context, workspace.id,Toast.LENGTH_SHORT).show()
                        }) {
                            Column{
                                Text(workspace.name)
                                Text(workspace.creationDateTime.toString())
                            }
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
    @Composable
    fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
        val eventHandler = rememberUpdatedState(onEvent)
        val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

        DisposableEffect(lifecycleOwner.value) {
            val lifecycle = lifecycleOwner.value.lifecycle
            val observer = LifecycleEventObserver { owner, event ->
                eventHandler.value(owner, event)
            }

            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
    }
    fun fetchWorkspacesAsync(context: Context, onSuccess: (Array<Workspace>)->Unit){

        Thread {
            val workspacesResult =
                WorkspaceRepository(httpClient = HttpClientJavaImpl()).getPagedList()
            if (workspacesResult.isFailure) {
                Log.w(TAG, "Error: " + workspacesResult.exceptionOrNull())
                this@HomeActivity.runOnUiThread{
                    Toast.makeText(context, "Error: ${workspacesResult.exceptionOrNull()}", Toast.LENGTH_SHORT).show()
                }
                return@Thread;
            }
            this@HomeActivity.runOnUiThread {
                Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT)
                    .show()
            }
            onSuccess(workspacesResult.getOrNull()!!.results)
        }.start()
    }

    companion object {
        private val TAG = HomeActivity::class.java.simpleName
    }
}