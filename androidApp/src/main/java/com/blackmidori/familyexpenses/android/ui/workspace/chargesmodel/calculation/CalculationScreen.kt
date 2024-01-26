package com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.calculation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.familyexpenses.android.AppScreen
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.android.core.HttpClientJavaImpl
import com.blackmidori.familyexpenses.android.shared.ui.SimpleAppBar
import com.blackmidori.familyexpenses.android.shared.ui.SimpleScaffold
import com.blackmidori.familyexpenses.models.ChargeAssociation
import com.blackmidori.familyexpenses.models.ChargesModel
import com.blackmidori.familyexpenses.models.Expense
import com.blackmidori.familyexpenses.models.Payer
import com.blackmidori.familyexpenses.models.PayerPaymentWeight
import com.blackmidori.familyexpenses.repositories.ChargeAssociationRepository
import com.blackmidori.familyexpenses.repositories.ChargesModelRepository
import com.blackmidori.familyexpenses.repositories.ExpenseRepository
import com.blackmidori.familyexpenses.repositories.PayerPaymentWeightRepository
import com.blackmidori.familyexpenses.repositories.PayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
fun CalculationScreen(
    navController: NavHostController,
    chargesModelId: String,
    workspaceId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var name by remember {
        mutableStateOf("")
    }
    var chargeAssociations by remember {
        mutableStateOf(arrayOf<ChargeAssociation>())
    }
    var payerPaymentWeightsByChargeAssociation = remember {
        mutableStateMapOf<String, Array<PayerPaymentWeight>>()
    }
    var payers = remember {
        mutableStateListOf<Payer>()
    }
    var expenses = remember {
        mutableStateListOf<Expense>()
    }
    LaunchedEffect(key1 = null) {
        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
        fetchChargesModelAsync(chargesModelId, coroutineScope, context) {
            name = it.name
        }
        fetchChargeAssociationsAsync(chargesModelId, coroutineScope, context) { it ->
            chargeAssociations = it


            for (chargeAssociation in chargeAssociations) {
                if (expenses.firstOrNull { it.id == chargeAssociation.expense.id } == null) {
                    fetchExpenseAsync(chargeAssociation.expense.id, coroutineScope, context) {
                        coroutineScope.launch {
                            expenses.add(it)
                        }
                    }
                }
                if (payers.firstOrNull { it.id == chargeAssociation.actualPayer.id } == null) {
                    fetchPayerAsync(
                        chargeAssociation.actualPayer.id,
                        coroutineScope,
                        context
                    ) {
                        coroutineScope.launch {
                            payers.add(it)
                        }
                    }
                }
                fetchPayerPaymentWeightsAsync(chargeAssociation.id, coroutineScope, context) {
                    coroutineScope.launch {
                        // No race condition due to coroutine
                        payerPaymentWeightsByChargeAssociation[chargeAssociation.id] =
                            (payerPaymentWeightsByChargeAssociation[chargeAssociation.id]
                                ?: arrayOf()) + it
                        for (payerPaymentWeight in it) {
                            if (payers.firstOrNull { it.id == payerPaymentWeight.payer.id } == null) {
                                fetchPayerAsync(
                                    payerPaymentWeight.payer.id,
                                    coroutineScope,
                                    context
                                ) {
                                    coroutineScope.launch {
                                        payers.add(it)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val inputs = remember {
        mutableStateMapOf<String, String>()
    }

    val getExpense = { expenseId: String ->
        expenses.firstOrNull { it.id == expenseId } ?: Expense(expenseId,Instant.DISTANT_PAST, expenseId)
    }
    val getPayer = { payerId: String ->
        payers.firstOrNull { it.id == payerId }?: Payer(payerId,Instant.DISTANT_PAST, payerId)
    }
    var result by remember {
        mutableStateOf("")
    }

    val onCalculateClick = {
        try {
            result = "";
            var localResult =""
            val charges = mutableListOf<Charge>()
            for (input in inputs) {
                val value = input.value.toDouble()
                val chargeAssociation = chargeAssociations.first { it.id == input.key }
                val expense = getExpense(chargeAssociation.expense.id)
                val actualPayer = getPayer(chargeAssociation.actualPayer.id)

                val chargeAssociations = payerPaymentWeightsByChargeAssociation.getValue(
                    chargeAssociation.id
                )
                for (payerPaymentWeight in chargeAssociations) {
                    val payer = getPayer(payerPaymentWeight.payer.id)
                    if(charges.firstOrNull { it.source.id == payer.id }==null) {
                        charges += Charge(payer)
                    }
                    val charge = charges.first { it.source.id == payer.id  }
                    if(charge.destination.keys.firstOrNull { it == actualPayer.id }==null) {
                        charge.destination[actualPayer.id] = mutableListOf()
                    }
                    val bills = charge.destination.getValue(actualPayer.id)
                    bills.add(Bill(expense, value*payerPaymentWeight.weight))
                }
            }
            localResult+="\n"
            localResult+="Detailed:\n"
            for (charge in charges) {
                for (payerId in charge.destination.keys) {
                    val bills = charge.destination.getValue(payerId)
                    for (bill in bills) {
                        localResult+="${charge.source.name} 👉 ${getPayer(payerId).name}(${bill.expense.name}) = ${bill.amount}\n"
                    }
                }
            }
            localResult+="\n"
            localResult+="Simplified:\n"
            for (charge in charges) {
                for (payerId in charge.destination.keys) {
                    val bills = charge.destination.getValue(payerId)
                    var sum = .0
                    for (bill in bills) {
                        sum+=bill.amount;
                    }
                    localResult+="${charge.source.name} 👉 ${getPayer(payerId).name} = $sum\n"
                }
            }
            result = localResult
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Invalid Input", Toast.LENGTH_SHORT).show()
        }
    }

    SimpleScaffold(
        topBar = {
            SimpleAppBar(
                navController = navController,
                title = { Text(stringResource(AppScreen.Calculation.title) + " - $name") })
        }
    ) {
        LazyColumn() {
            item {
                Button(onCalculateClick) {
                    Text("Calculate")
                }
            }
            for (chargeAssociation in chargeAssociations) {
                item {
                    Column {
                        Text("${getExpense(chargeAssociation.expense.id).name} (${chargeAssociation.name}):")
                        TextField(
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            value = inputs[chargeAssociation.id] ?: "",
                            onValueChange = { input ->
                                if (input.count { it == '.' } <= 1 && !input.contains(",") && input.matches(
                                        Regex("\\d*(:?\\.\\d*)?")
                                    )) {
                                    inputs[chargeAssociation.id] = input
                                }
                            }
                        )
                    }
                }
            }
            item { Text(result) }
        }
    }
}

private fun fetchChargesModelAsync(
    chargesModelId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (ChargesModel) -> Unit
) {
    val TAG = "CalculationScreen.fetchChargesModelAsync"
    Thread {
        val chargesModelResult =
            ChargesModelRepository(httpClient = HttpClientJavaImpl()).getOne(chargesModelId)
        if (chargesModelResult.isFailure) {
            Log.w(TAG, "Error: " + chargesModelResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${chargesModelResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(chargesModelResult.getOrNull()!!)
    }.start()
}

private fun fetchChargeAssociationsAsync(
    chargesModelId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<ChargeAssociation>) -> Unit
) {
    val TAG = "CalculationScreen.fetchChargesModelsAsync"
    Thread {
        val chargeAssociationResult =
            ChargeAssociationRepository(httpClient = HttpClientJavaImpl()).getPagedList(
                chargesModelId
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
            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(chargeAssociationResult.getOrNull()!!.results)
    }.start()
}

private fun fetchPayerPaymentWeightsAsync(
    chargeAssociationId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<PayerPaymentWeight>) -> Unit
) {
    val TAG = "CalculationScreen.fetchPayerPaymentWeightsAsync"
    Thread {
        val payerPaymentWeightResult =
            PayerPaymentWeightRepository(httpClient = HttpClientJavaImpl()).getPagedList(
                chargeAssociationId
            )
        if (payerPaymentWeightResult.isFailure) {
            Log.w(TAG, "Error: " + payerPaymentWeightResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${payerPaymentWeightResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(payerPaymentWeightResult.getOrNull()!!.results)
    }.start()
}


private fun fetchExpenseAsync(
    expenseId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Expense) -> Unit
) {
    val TAG = "UpdateExpenseScreen.fetchExpenseAsync"
    Thread {
        val expenseResult =
            ExpenseRepository(httpClient = HttpClientJavaImpl()).getOne(expenseId)
        if (expenseResult.isFailure) {
            Log.w(TAG, "Error: " + expenseResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${expenseResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(expenseResult.getOrNull()!!)
    }.start()
}

private fun fetchPayerAsync(
    payerId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Payer) -> Unit
) {
    val TAG = "UpdatePayerScreen.fetchPayerAsync"
    Thread {
        val payerResult =
            PayerRepository(httpClient = HttpClientJavaImpl()).getOne(payerId)
        if (payerResult.isFailure) {
            Log.w(TAG, "Error: " + payerResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${payerResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(payerResult.getOrNull()!!)
    }.start()
}

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        CalculationScreen(rememberNavController(), "fake", "fake")
    }
}