package com.blackmidori.expenses.android.ui.workspace.chargesmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.expenses.android.AppScreen
import com.blackmidori.expenses.android.MyApplicationTheme
import com.blackmidori.expenses.android.shared.ui.SimpleAppBar
import com.blackmidori.expenses.android.shared.ui.SimpleScaffold
import com.blackmidori.expenses.models.ChargeAssociation
import com.blackmidori.expenses.models.Expense
import com.blackmidori.expenses.models.Payer
import com.blackmidori.expenses.repositories.ChargeAssociationRepository
import com.blackmidori.expenses.repositories.ExpenseRepository
import com.blackmidori.expenses.repositories.PayerRepository
import com.blackmidori.expenses.stores.chargeAssociationStorage
import com.blackmidori.expenses.stores.expenseStorage
import com.blackmidori.expenses.stores.payerStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChargeAssociationScreen(
    navController: NavHostController,
    chargesModelId: String,
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
//        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()

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
            title = { Text(stringResource(AppScreen.AddChargeAssociation.title)) },
        )
    }) {
        Column {
            TextField(value = chargeAssociation?.name ?: "", onValueChange = {
                chargeAssociation = ChargeAssociation(
                    chargeAssociation?.id ?: "",
                    chargeAssociation?.creationDateTime ?: Instant.DISTANT_PAST,
                    chargeAssociation?.chargesModelId ?: "",
                    it,
                    chargeAssociation?.expense ?: Expense(
                        "",
                        Instant.DISTANT_PAST,
                        "",
                        ""
                    ),
                    chargeAssociation?.actualPayer ?: Payer(
                        "",
                        Instant.DISTANT_PAST,
                        "",
                        ""
                    ),
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
                                    chargeAssociation?.chargesModelId ?: "",
                                    chargeAssociation?.name ?: "",
                                    item,
                                    chargeAssociation?.actualPayer ?: Payer(
                                        "",
                                        Instant.DISTANT_PAST,
                                        "",
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
                                    chargeAssociation?.chargesModelId ?: "",
                                    chargeAssociation?.name ?: "",
                                    chargeAssociation?.expense ?: Expense(
                                        "",
                                        Instant.DISTANT_PAST,
                                        "",
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
                coroutineScope.launch {
                    val TAG = "AddChargeAssociationScreen.submit"
                    val localChargeAssociation = chargeAssociation ?: return@launch
                    val chargeAssociationResult =
                        ChargeAssociationRepository(
                            chargeAssociationStorage(),
                            expenseStorage(),
                            payerStorage(),
                        ).add(chargesModelId, localChargeAssociation)
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
                                "Added",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigateUp()
                        }
                    }
                }
            }) {
                Text("Submit")
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
    coroutineScope.launch {
        val chargeAssociationResult =
            ChargeAssociationRepository(
                chargeAssociationStorage(),
                expenseStorage(),
                payerStorage(),
            ).getOne(
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
            return@launch;
        }
        coroutineScope.launch {
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(chargeAssociationResult.getOrNull()!!)
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

private fun fetchPayersAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<Payer>) -> Unit
) {
    val TAG = "UpdatePayerPaymentWeightScreen.fetchPayersAsync"
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