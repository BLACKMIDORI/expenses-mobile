package com.blackmidori.familyexpenses.android

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.blackmidori.familyexpenses.android.core.HttpClientJavaImpl
import com.blackmidori.familyexpenses.android.ui.AddWorkspaceScreen
import com.blackmidori.familyexpenses.android.ui.HomeScreen
import com.blackmidori.familyexpenses.android.ui.UpdateWorkspaceScreen
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.repositories.WorkspaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Enum.valueOf

/**
 * enum values that represent the screens in the app
 */
enum class AppScreen(@StringRes val title: Int, val route: String) {
    Home(title = R.string.home_screen, route = Home.name),
    AddWorkspace(title = R.string.add_workspace, route = AddWorkspace.name),
    UpdateWorkspace(
        title = R.string.update_workspace,
        route = UpdateWorkspace.name + "/{workspaceId}"
    ),
}

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen =
        AppScreen.entries.firstOrNull { it.route == backStackEntry?.destination?.route }
            ?: AppScreen.valueOf(AppScreen.Home.name)

    var updateList by remember {
        mutableStateOf(false)
    }
    var list by remember {
        mutableStateOf(arrayOf<Workspace>())
    }
    LaunchedEffect(key1 = updateList) {
        Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()
        fetchWorkspacesAsync(coroutineScope, context) {
            list = it
        }
    }
    MyApplicationTheme {
        Scaffold(
            topBar = {
                AppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = AppScreen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = AppScreen.Home.route) {
                    HomeScreen(list = list, onClickUpdateWorkspace = {
                        navController.navigate(
                            AppScreen.UpdateWorkspace.route.replace(
                                "{workspaceId}",
                                it
                            )
                        )
                    }) {
                        navController.navigate(AppScreen.AddWorkspace.route)
                    }
                }
                composable(route = AppScreen.AddWorkspace.route) {
                    AddWorkspaceScreen(onFinish = {
                        navController.popBackStack()
                        updateList = !updateList
                    })
                }
                composable(route = AppScreen.UpdateWorkspace.route) { navBackStackEntry ->
                    val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                    UpdateWorkspaceScreen(
                        workspaceId!!,
                        onFinish = {
                            navController.popBackStack()
                            updateList = !updateList
                        })
                }
            }
        }
    }
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}


private fun fetchWorkspacesAsync(
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<Workspace>) -> Unit
) {
    val TAG = "fetchWorkspacesAsync"
    Thread {
        val workspacesResult =
            WorkspaceRepository(httpClient = HttpClientJavaImpl()).getPagedList()
        if (workspacesResult.isFailure) {
            Log.w(TAG, "Error: " + workspacesResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${workspacesResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(workspacesResult.getOrNull()!!.results)
    }.start()
}
