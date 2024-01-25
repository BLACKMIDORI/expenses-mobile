package com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.familyexpenses.android.AppScreen
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.android.core.HttpClientJavaImpl
import com.blackmidori.familyexpenses.android.shared.ui.SimpleAppBar
import com.blackmidori.familyexpenses.android.shared.ui.SimpleScaffold
import com.blackmidori.familyexpenses.models.ChargeAssociation
import com.blackmidori.familyexpenses.models.Expense
import com.blackmidori.familyexpenses.models.Payer
import com.blackmidori.familyexpenses.repositories.ChargeAssociationRepository
import com.blackmidori.familyexpenses.repositories.ExpenseRepository
import com.blackmidori.familyexpenses.repositories.PayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateChargeAssociationScreen(
    navController: NavHostController,
    chargeAssociationId: String,
    workspaceId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var chargeAssociation by remember {
        mutableStateOf<ChargeAssociation?>(null)
    }
    var expenses by remember {
        mutableStateOf(arrayOf<Expense>())
    }
    var payers by remember {
        mutableStateOf(arrayOf<Payer>())
    }

    LaunchedEffect(key1 = null) {
        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
        fetchChargeAssociationAsync(chargeAssociationId, coroutineScope, context) {
            chargeAssociation = it
        }
        fetchExpensesAsync(workspaceId, coroutineScope, context) {
            expenses = it
        }
        fetchPayersAsync(workspaceId, coroutineScope, context) {
            payers = it
        }
    }
    SimpleScaffold(topBar = {
        SimpleAppBar(
            navController = navController,
            title = { Text(stringResource(AppScreen.UpdateChargeAssociation.title)) },
        )
    }) {
        Column {
            TextField(value = chargeAssociation?.name ?: "", onValueChange = {
                chargeAssociation = ChargeAssociation(
                    chargeAssociation?.id ?: "",
                    chargeAssociation?.creationDateTime ?: Instant.DISTANT_PAST,
                    it,
                    chargeAssociation?.expense ?: Expense("", Instant.DISTANT_PAST, ""),
                    chargeAssociation?.actualPayer ?: Payer("", Instant.DISTANT_PAST, ""),
                )
            })
            var expensesExpanded by remember {
                mutableStateOf(false)
            }
            ExposedDropdownMenuBox(expanded = expensesExpanded, { expensesExpanded = it }) {

                val expenseId = chargeAssociation?.expense?.id
                TextField(
                    value = expenses.find { it.id == expenseId }?.name
                        ?: chargeAssociation?.expense?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expensesExpanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expensesExpanded,
                    onDismissRequest = { expensesExpanded = false }
                ) {
                    if (expenses.isEmpty()) {
                        Text(text = "Empty")
                    }

                    for (item in expenses) {
                        DropdownMenuItem(
                            text = { Text(text = item.name) },
                            onClick = {
                                chargeAssociation = ChargeAssociation(
                                    chargeAssociation?.id ?: "",
                                    chargeAssociation?.creationDateTime ?: Instant.DISTANT_PAST,
                                    chargeAssociation?.name ?: "",
                                    item,
                                    chargeAssociation?.actualPayer ?: Payer(
                                        "",
                                        Instant.DISTANT_PAST,
                                        ""
                                    )
                                )
                                expensesExpanded = false
                            }
                        )
                    }
                }
            }

            var payersExpanded by remember {
                mutableStateOf(false)
            }
            ExposedDropdownMenuBox(expanded = payersExpanded, { payersExpanded = it }) {

                val payerId = chargeAssociation?.actualPayer?.id
                TextField(
                    value = payers.find { it.id == payerId }?.name
                        ?: chargeAssociation?.actualPayer?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = payersExpanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = payersExpanded,
                    onDismissRequest = { payersExpanded = false }
                ) {
                    if (payers.isEmpty()) {
                        Text(text = "Empty")
                    }

                    for (item in payers) {
                        DropdownMenuItem(
                            text = { Text(text = item.name) },
                            onClick = {
                                chargeAssociation = ChargeAssociation(
                                    chargeAssociation?.id ?: "",
                                    chargeAssociation?.creationDateTime ?: Instant.DISTANT_PAST,
                                    chargeAssociation?.name ?: "",
                                    chargeAssociation?.expense ?: Expense(
                                        "",
                                        Instant.DISTANT_PAST,
                                        ""
                                    ),
                                    item,
                                )
                                payersExpanded = false
                            }
                        )
                    }
                }
            }
            Button(onClick = {
                Thread {
                    val TAG = "UpdateChargeAssociationScreen.update"
                    val localChargeAssociation = chargeAssociation ?: return@Thread
                    val chargeAssociationResult =
                        ChargeAssociationRepository(httpClient = HttpClientJavaImpl()).update(
                            localChargeAssociation
                        )
                    if (chargeAssociationResult.isFailure) {
                        Log.w(TAG, "Error: " + chargeAssociationResult.exceptionOrNull())

                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "Error: ${chargeAssociationResult.exceptionOrNull()}",
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
                }.start()
            }) {
                Text("Submit")
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3737)),
                onClick = {
                    Thread {
                        val TAG = "UpdateExpenseScreen.delete"
                        val localChargeAssociation = chargeAssociation ?: return@Thread
                        val deleteResult =
                            ChargeAssociationRepository(httpClient = HttpClientJavaImpl()).delete(
                                localChargeAssociation
                            )
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
                    }.start()
                }) {
                Text("Delete")
            }
        }
    }
}

private fun fetchChargeAssociationAsync(
    chargeAssociationId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (ChargeAssociation) -> Unit
) {
    val TAG = "UpdateChargeAssociationScreen.fetchChargeAssociationAsync"
    Thread {
        val chargeAssociationResult =
            ChargeAssociationRepository(httpClient = HttpClientJavaImpl()).getOne(
                chargeAssociationId
            )
        if (chargeAssociationResult.isFailure) {
            Log.w(TAG, "Error: " + chargeAssociationResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${chargeAssociationResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(chargeAssociationResult.getOrNull()!!)
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

private fun fetchPayersAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<Payer>) -> Unit
) {
    val TAG = "UpdatePayerPaymentWeightScreen.fetchPayersAsync"
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

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        UpdateChargeAssociationScreen(
            rememberNavController(),
            workspaceId = "fake",
            chargeAssociationId = "fake"
        )
    }
}