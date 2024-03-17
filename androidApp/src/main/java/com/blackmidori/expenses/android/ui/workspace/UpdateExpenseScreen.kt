package com.blackmidori.expenses.android.ui.workspace

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.expenses.android.AppScreen
import com.blackmidori.expenses.android.MyApplicationTheme
import com.blackmidori.expenses.android.shared.ui.SimpleAppBar
import com.blackmidori.expenses.android.shared.ui.SimpleScaffold
import com.blackmidori.expenses.models.Expense
import com.blackmidori.expenses.repositories.ExpenseRepository
import com.blackmidori.expenses.stores.expenseStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
fun UpdateExpenseScreen(
    navController: NavHostController,
    expenseId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var workspaceId by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = null) {
        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
        fetchExpenseAsync(expenseId, coroutineScope, context) {
            workspaceId = it.workspaceId
            name = it.name
        }
    }
    SimpleScaffold(topBar = {
        SimpleAppBar(
            navController = navController,
            title = { Text(stringResource(AppScreen.UpdateExpense.title)) },
        )
    }) {
        Column {
            TextField(value = name, onValueChange = {
                name = it
            })
            Button(onClick = {
                coroutineScope.launch {
                    val TAG = "UpdateExpenseScreen.update"
                    val expense = Expense(expenseId, Instant.DISTANT_PAST,workspaceId, name)
                    val expenseResult =
                        ExpenseRepository(expenseStore(context)).update(expense)
                    if (expenseResult.isFailure) {
                        Log.w(TAG, "Error: " + expenseResult.exceptionOrNull())

                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "Error: ${expenseResult.exceptionOrNull()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigateUp()
                        }
                    }
                }
            }) {
                Text("Submit")
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3737)),
                onClick = {
                    coroutineScope.launch {
                        val TAG = "UpdateExpenseScreen.delete"
                        val deleteResult =
                            ExpenseRepository(expenseStore(context)).delete(expenseId)
                        if (deleteResult.isFailure) {
                            Log.w(TAG, "Error: " + deleteResult.exceptionOrNull())

                            coroutineScope.launch {
                                Toast.makeText(
                                    context,
                                    "Error: ${deleteResult.exceptionOrNull()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            coroutineScope.launch {
                                Toast.makeText(
                                    context,
                                    "Deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigateUp()
                            }
                        }
                    }
                }) {
                Text("Delete")
            }
        }
    }
}

private fun fetchExpenseAsync(
    expenseId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Expense) -> Unit
) {
    val TAG = "UpdateExpenseScreen.fetchExpenseAsync"
    coroutineScope.launch {
        val expenseResult =
            ExpenseRepository(expenseStore(context)).getOne(expenseId)
        if (expenseResult.isFailure) {
            Log.w(TAG, "Error: " + expenseResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${expenseResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@launch;
        }
        coroutineScope.launch {
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(expenseResult.getOrNull()!!)
    }
}

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        UpdateExpenseScreen(rememberNavController(), expenseId = "fake")
    }
}