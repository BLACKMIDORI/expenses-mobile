package com.blackmidori.expenses.android.ui.workspace

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.expenses.android.AppScreen
import com.blackmidori.expenses.android.MyApplicationTheme
import com.blackmidori.expenses.android.R
import com.blackmidori.expenses.android.shared.ui.SimpleAppBar
import com.blackmidori.expenses.android.shared.ui.SimpleScaffold
import com.blackmidori.expenses.models.ChargesModel
import com.blackmidori.expenses.models.Expense
import com.blackmidori.expenses.models.Payer
import com.blackmidori.expenses.models.Workspace
import com.blackmidori.expenses.repositories.ChargesModelRepository
import com.blackmidori.expenses.repositories.ExpenseRepository
import com.blackmidori.expenses.repositories.PayerRepository
import com.blackmidori.expenses.repositories.WorkspaceRepository
import com.blackmidori.expenses.stores.chargesModelStorage
import com.blackmidori.expenses.stores.expenseStorage
import com.blackmidori.expenses.stores.payerStorage
import com.blackmidori.expenses.stores.workspaceStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
@ExperimentalFoundationApi
fun WorkspaceScreen(
    navController: NavHostController,
    workspaceId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var name by remember {
        mutableStateOf("")
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
//        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
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

    val onAddPayerClick = {
        navController.navigate(
            AppScreen.AddPayer.route.replace("{workspaceId}", workspaceId)
        )
    }
//    val onOpenPayerClick: (id: String) -> Unit = {
//        Toast.makeText(context, "payer id: $it", Toast.LENGTH_SHORT).show()
//    }
    val onUpdatePayerClick: (id: String) -> Unit = {
        navController.navigate(
            AppScreen.UpdatePayer.route.replace(
                "{id}", it
            )
        )
    }

    val onAddExpenseClick = {
        navController.navigate(
            AppScreen.AddExpense.route.replace("{workspaceId}", workspaceId)
        )
    }
//    val onOpenExpenseClick: (id: String) -> Unit = {
//        Toast.makeText(context, "expense id: $it", Toast.LENGTH_SHORT).show()
//    }
    val onUpdateExpenseClick: (id: String) -> Unit = {
        navController.navigate(
            AppScreen.UpdateExpense.route.replace(
                "{id}", it
            )
        )
    }

    val onAddChargesModelClick = {
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

    val pagerState = rememberPagerState(
        initialPage = 2,
        initialPageOffsetFraction = 0f,
        pageCount = { 3 }
    )
    SimpleScaffold(
        topBar = {
            SimpleAppBar(
                navController = navController,
                title = { Text(stringResource(AppScreen.Workspace.title) + " - $name") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = when (pagerState.currentPage) {
                    0 -> onAddPayerClick
                    1 -> onAddExpenseClick
                    2 -> onAddChargesModelClick
                    else -> {
                        {}
                    }
                },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = when (pagerState.currentPage) {
                        0 -> stringResource(R.string.add_payer)
                        1 -> stringResource(R.string.add_expense)
                        2 -> stringResource(R.string.add_charges_model)
                        else -> null
                    }
                )
            }
        }
    ) {

        Column {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                Tab(
                    selected = 0 == pagerState.currentPage,
                    text = { Text("Payers") },
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                )
                Tab(
                    selected = 0 == pagerState.currentPage,
                    text = { Text("Expenses") },
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                )
                Tab(
                    selected = 0 == pagerState.currentPage,
                    text = { Text("Charges Models") },
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                )
            }

            HorizontalPager(
                state = pagerState
            ) {
                if(it == 0)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item { Text("Payer Count: ${payers.size}") }
                    for (item in payers) {
                        item {
                            ListItem(
                                modifier = Modifier.clickable {
                                    onUpdatePayerClick(item.id)
                                },
                                headlineContent = { Text(item.name) },
                                supportingContent = { Text(item.creationDateTime.toString()) },
                                trailingContent = {
                                    IconButton({
                                        onUpdatePayerClick(item.id)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = stringResource(R.string.update_payer)
                                        )
                                    }

                                },
                            )
                        }
                    }


                }

                if(it == 1)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item { Text("Expense Count: ${expenses.size}") }
                    for (item in expenses) {
                        item {

                            ListItem(
                                modifier = Modifier.clickable {
                                    onUpdateExpenseClick(item.id)
                                },
                                headlineContent = { Text(item.name) },
                                supportingContent = { Text(item.creationDateTime.toString()) },
                                trailingContent = {
                                    IconButton({
                                        onUpdateExpenseClick(item.id)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = stringResource(R.string.update_expense)
                                        )
                                    }

                                },
                            )
                        }
                    }
                }

                if(it == 2)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item { Text("Charges Model Count: ${chargesModels.size}") }
                    for (item in chargesModels) {
                        item {
                            ListItem(
                                modifier = Modifier.clickable {
                                    onOpenChargesModelClick(item.id)
                                },
                                headlineContent = { Text(item.name) },
                                supportingContent = { Text(item.creationDateTime.toString()) },
                                trailingContent = {
                                    IconButton({
                                        onUpdateChargesModelClick(item.id)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = stringResource(R.string.update_charges_model)
                                        )
                                    }

                                },
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
    coroutineScope.launch {
        val workspaceResult =
            WorkspaceRepository(workspaceStorage()).getOne(workspaceId)
        if (workspaceResult.isFailure) {
            Log.w(TAG, "Error: " + workspaceResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${workspaceResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@launch;
        }
        coroutineScope.launch {
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(workspaceResult.getOrNull()!!)
    }
}

private fun fetchPayersAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<Payer>) -> Unit
) {
    val TAG = "WorkspaceScreen.fetchPayersAsync"
    coroutineScope.launch {
        val payersResult =
            PayerRepository(payerStorage()).getPagedList(workspaceId)
        if (payersResult.isFailure) {
            Log.w(TAG, "Error: " + payersResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${payersResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@launch;
        }
        coroutineScope.launch {
//            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(payersResult.getOrNull()!!.results)
    }
}

private fun fetchExpensesAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<Expense>) -> Unit
) {
    val TAG = "WorkspaceScreen.fetchExpensesAsync"
    coroutineScope.launch {
        val expensesResult =
            ExpenseRepository(expenseStorage()).getPagedList(workspaceId)
        if (expensesResult.isFailure) {
            Log.w(TAG, "Error: " + expensesResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${expensesResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@launch;
        }
        coroutineScope.launch {
//            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(expensesResult.getOrNull()!!.results)
    }
}

private fun fetchChargesModelsAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<ChargesModel>) -> Unit
) {
    val TAG = "WorkspaceScreen.fetchChargesModelsAsync"
    coroutineScope.launch {
        val chargesModelsResult =
            ChargesModelRepository(chargesModelStorage()).getPagedList(workspaceId)
        if (chargesModelsResult.isFailure) {
            Log.w(TAG, "Error: " + chargesModelsResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${chargesModelsResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@launch;
        }
        coroutineScope.launch {
//            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(chargesModelsResult.getOrNull()!!.results)
    }
}

@Preview
@Composable
@ExperimentalFoundationApi
private fun Preview() {
    MyApplicationTheme {
        WorkspaceScreen(rememberNavController(), "fake")
    }
}