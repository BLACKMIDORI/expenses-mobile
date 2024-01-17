package com.blackmidori.familyexpenses.android.ui.workspace

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.familyexpenses.android.AppScreen
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.android.R
import com.blackmidori.familyexpenses.android.core.HttpClientJavaImpl
import com.blackmidori.familyexpenses.android.shared.ui.SimpleAppBar
import com.blackmidori.familyexpenses.android.shared.ui.SimpleScaffold
import com.blackmidori.familyexpenses.models.ChargesModel
import com.blackmidori.familyexpenses.models.Expense
import com.blackmidori.familyexpenses.models.Payer
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.repositories.ChargesModelRepository
import com.blackmidori.familyexpenses.repositories.ExpenseRepository
import com.blackmidori.familyexpenses.repositories.PayerRepository
import com.blackmidori.familyexpenses.repositories.WorkspaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WorkspaceScreen(
    navController: NavHostController,
    workspaceId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var name by remember {
        mutableStateOf("")
    }
    var page by remember {
        mutableStateOf("chargesModel")
    }
    var payers by remember {
        mutableStateOf(arrayOf<Payer>())
    }
    var expenses by remember {
        mutableStateOf(arrayOf<Expense>())
    }
    var chargesModels by remember {
        mutableStateOf(arrayOf<ChargesModel>())
    }
    LaunchedEffect(key1 = null) {
        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
        fetchWorkspaceAsync(workspaceId, coroutineScope, context) {
            name = it.name
        }
        fetchPayersAsync(workspaceId, coroutineScope, context) {
            payers = it
        }
        fetchExpensesAsync(workspaceId, coroutineScope, context) {
            expenses = it
        }
        fetchChargesModelsAsync(workspaceId, coroutineScope, context) {
            chargesModels = it
        }
    }

    val onAddPayerClick= {
        navController.navigate(
            AppScreen.AddPayer.route.replace("{workspaceId}", workspaceId)
        )
    }
    val onOpenPayerClick: (id: String) -> Unit = {
        Toast.makeText(context, "payer id: $it", Toast.LENGTH_SHORT).show()
    }
    val onUpdatePayerClick: (id: String) -> Unit = {
        navController.navigate(
            AppScreen.UpdatePayer.route.replace(
                "{id}", it
            )
        )
    }

    val onAddExpenseClick= {
        navController.navigate(
            AppScreen.AddExpense.route.replace("{workspaceId}", workspaceId)
        )
    }
    val onOpenExpenseClick: (id: String) -> Unit = {
        Toast.makeText(context, "expense id: $it", Toast.LENGTH_SHORT).show()
    }
    val onUpdateExpenseClick: (id: String) -> Unit = {
        navController.navigate(
            AppScreen.UpdateExpense.route.replace(
                "{id}", it
            )
        )
    }

    val onAddChargesModelClick= {
        navController.navigate(
            AppScreen.AddChargesModel.route.replace("{workspaceId}", workspaceId)
        )
    }
    val onOpenChargesModelClick: (id: String) -> Unit = {
        navController.navigate(
            AppScreen.ChargesModel.route.replace("{id}", it).replace("{workspaceId}", workspaceId)
        )
    }
    val onUpdateChargesModelClick: (id: String) -> Unit = {
        navController.navigate(
            AppScreen.UpdateChargesModel.route.replace(
                "{id}", it
            )
        )
    }

    SimpleScaffold(
        topBar = {
            SimpleAppBar(
                navController = navController,
                title = { Text(stringResource(AppScreen.Workspace.title) + " - $name") })
        }
    ) {
        LazyColumn {
            item {
                Row{
                    Button(onClick = {page = "payer"}) {
                        Text("Payers")
                    }
                    Button(onClick = {page = "expense"}) {
                        Text("Expenses")
                    }
                    Button(onClick = {page = "chargesModel"}) {
                        Text("Charges Models")
                    }
                }
            }
            if(page == "payer")
                item {
                    Button(onClick = onAddPayerClick) {
                        Text("Add Payer")
                    }
                }
            if(page == "payer")
                item { Text("Payer Count: ${payers.size}") }
            if(page == "payer")
                for (item in payers) {
                    item {
                        Row {
                            Button(onClick = {
                                onOpenPayerClick(item.id)
                            }) {
                                Column {
                                    Text(item.name)
                                    Text(item.creationDateTime.toString())
                                }
                            }
                            Button(onClick = {
                                onUpdatePayerClick(item.id)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = stringResource(R.string.back_button)
                                )
                            }
                        }
                    }
                }

            if(page == "expense")
                item {
                    Button(onClick = onAddExpenseClick) {
                        Text("Add Expense")
                    }
                }
            if(page == "expense")
                item { Text("Expense Count: ${expenses.size}") }
            if(page == "expense")
                for (item in expenses) {
                    item {
                        Row {
                            Button(onClick = {
                                onOpenExpenseClick(item.id)
                            }) {
                                Column {
                                    Text(item.name)
                                    Text(item.creationDateTime.toString())
                                }
                            }
                            Button(onClick = {
                                onUpdateExpenseClick(item.id)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = stringResource(R.string.back_button)
                                )
                            }
                        }
                    }
                }

            if(page == "chargesModel")
                item {
                    Button(onClick = onAddChargesModelClick) {
                        Text("Add Charges Model")
                    }
                }
            if(page == "chargesModel")
                item { Text("Charges Model Count: ${chargesModels.size}") }
            if(page == "chargesModel")
                for (item in chargesModels) {
                    item {
                        Row {
                            Button(onClick = {
                                onOpenChargesModelClick(item.id)
                            }) {
                                Column {
                                    Text(item.name)
                                    Text(item.creationDateTime.toString())
                                }
                            }
                            Button(onClick = {
                                onUpdateChargesModelClick(item.id)
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

private fun fetchWorkspaceAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Workspace) -> Unit
) {
    val TAG = "WorkspaceScreen.fetchWorkspacesAsync"
    Thread {
        val workspaceResult =
            WorkspaceRepository(httpClient = HttpClientJavaImpl()).getOne(workspaceId)
        if (workspaceResult.isFailure) {
            Log.w(TAG, "Error: " + workspaceResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${workspaceResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(workspaceResult.getOrNull()!!)
    }.start()
}

private fun fetchPayersAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<Payer>) -> Unit
) {
    val TAG = "WorkspaceScreen.fetchPayersAsync"
    Thread {
        val payersResult =
            PayerRepository(httpClient = HttpClientJavaImpl()).getPagedList(workspaceId)
        if (payersResult.isFailure) {
            Log.w(TAG, "Error: " + payersResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${payersResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(payersResult.getOrNull()!!.results)
    }.start()
}
private fun fetchExpensesAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<Expense>) -> Unit
) {
    val TAG = "WorkspaceScreen.fetchExpensesAsync"
    Thread {
        val expensesResult =
            ExpenseRepository(httpClient = HttpClientJavaImpl()).getPagedList(workspaceId)
        if (expensesResult.isFailure) {
            Log.w(TAG, "Error: " + expensesResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${expensesResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(expensesResult.getOrNull()!!.results)
    }.start()
}
private fun fetchChargesModelsAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<ChargesModel>) -> Unit
) {
    val TAG = "WorkspaceScreen.fetchChargesModelsAsync"
    Thread {
        val chargesModelsResult =
            ChargesModelRepository(httpClient = HttpClientJavaImpl()).getPagedList(workspaceId)
        if (chargesModelsResult.isFailure) {
            Log.w(TAG, "Error: " + chargesModelsResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${chargesModelsResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(chargesModelsResult.getOrNull()!!.results)
    }.start()
}

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        WorkspaceScreen(rememberNavController(), "fake")
    }
}