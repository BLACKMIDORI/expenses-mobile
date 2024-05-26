package com.blackmidori.expenses.android.ui.workspace.chargesmodel.calculation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.expenses.android.AppScreen
import com.blackmidori.expenses.android.MyApplicationTheme
import com.blackmidori.expenses.android.shared.ui.SimpleAppBar
import com.blackmidori.expenses.android.shared.ui.SimpleScaffold
import com.blackmidori.expenses.models.ChargeAssociation
import com.blackmidori.expenses.models.ChargesModel
import com.blackmidori.expenses.models.Expense
import com.blackmidori.expenses.models.Payer
import com.blackmidori.expenses.models.PayerPaymentWeight
import com.blackmidori.expenses.repositories.ChargeAssociationRepository
import com.blackmidori.expenses.repositories.ChargesModelRepository
import com.blackmidori.expenses.repositories.ExpenseRepository
import com.blackmidori.expenses.repositories.PayerPaymentWeightRepository
import com.blackmidori.expenses.repositories.PayerRepository
import com.blackmidori.expenses.stores.chargeAssociationStorage
import com.blackmidori.expenses.stores.chargesModelStorage
import com.blackmidori.expenses.stores.expenseStorage
import com.blackmidori.expenses.stores.payerPaymentWeightStorage
import com.blackmidori.expenses.stores.payerStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
fun CalculationScreen(
    navController: NavHostController,
    chargesModelId: String,
//    workspaceId: String,
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
//        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
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
        expenses.firstOrNull { it.id == expenseId } ?: Expense(
            expenseId,
            Instant.DISTANT_PAST,
            "None",
            expenseId
        )
    }
    val getPayer = { payerId: String ->
        payers.firstOrNull { it.id == payerId } ?: Payer(
            payerId,
            Instant.DISTANT_PAST,
            "None",
            payerId
        )
    }
    var result by remember {
        mutableStateOf("")
    }

    val onCalculateClick = {
        try {
            result = "";
            var localResult = ""
            val charges = mutableListOf<Charge>()
            for (input in inputs) {
                val value = input.value.toFloat()
                val chargeAssociation = chargeAssociations.first { it.id == input.key }
                val expense = getExpense(chargeAssociation.expense.id)
                val actualPayer = getPayer(chargeAssociation.actualPayer.id)

                val payerPaymentWeights = payerPaymentWeightsByChargeAssociation.getValue(
                    chargeAssociation.id
                )
                for (payerPaymentWeight in payerPaymentWeights) {
                    val payer = getPayer(payerPaymentWeight.payer.id)
                    if (charges.firstOrNull { it.source.id == payer.id } == null) {
                        charges += Charge(payer)
                    }
                    val charge = charges.first { it.source.id == payer.id }
                    if (charge.destination.keys.firstOrNull { it == actualPayer.id } == null) {
                        charge.destination[actualPayer.id] = mutableListOf()
                    }
                    val bills = charge.destination.getValue(actualPayer.id)
                    bills.add(Bill(expense, value * payerPaymentWeight.weight))
                }
            }
            localResult += "\n"
            localResult += "by Expense:\n"
            for (charge in charges) {
                for (payerId in charge.destination.keys) {
                    val bills = charge.destination.getValue(payerId)
                    for (bill in bills) {
                        if (charge.source.id == payerId) {
                            continue;
                        }
                        localResult += "${charge.source.name} pays ${getPayer(payerId).name}(${bill.expense.name}) = ${round3Digits(bill.amount)}\n"
                    }
                }
            }
            localResult += "\n"
            localResult += "by Actual Payer:\n"
            for (charge in charges) {
                for (payerId in charge.destination.keys) {
                    if (charge.source.id == payerId) {
                        continue;
                    }
                    val bills = charge.destination.getValue(payerId)
                    var sum = .0f
                    for (bill in bills) {
                        sum += bill.amount;
                    }
                    localResult += "${charge.source.name} pays ${getPayer(payerId).name} = ${round3Digits(sum)}\n"
                }
            }
            localResult += "\n"
            localResult += "Simplified:\n"
            for (charge in chargesSimplified(charges)) {
                localResult += "${getPayer(charge.source).name} pays ${getPayer(charge.destination).name} = ${round3Digits(charge.amount)}\n"
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
        LazyColumn(Modifier.fillMaxWidth()) {
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
    coroutineScope.launch {
        val chargesModelResult =
            ChargesModelRepository(chargesModelStorage()).getOne(chargesModelId)
        if (chargesModelResult.isFailure) {
            Log.w(TAG, "Error: " + chargesModelResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${chargesModelResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@launch;
        }
        coroutineScope.launch {
//            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
//                .show()
        }
        onSuccess(chargesModelResult.getOrNull()!!)
    }
}

private fun fetchChargeAssociationsAsync(
    chargesModelId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<ChargeAssociation>) -> Unit
) {
    val TAG = "CalculationScreen.fetchChargesModelsAsync"
    coroutineScope.launch {
        val chargeAssociationResult =
            ChargeAssociationRepository(
                chargeAssociationStorage(),
                expenseStorage(),
                payerStorage()
            ).getPagedList(
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
            return@launch;
        }
        coroutineScope.launch {
//            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(chargeAssociationResult.getOrNull()!!.results)
    }
}

private fun fetchPayerPaymentWeightsAsync(
    chargeAssociationId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<PayerPaymentWeight>) -> Unit
) {
    val TAG = "CalculationScreen.fetchPayerPaymentWeightsAsync"
    coroutineScope.launch {
        val payerPaymentWeightResult =
            PayerPaymentWeightRepository(
                payerPaymentWeightStorage(),
                payerStorage()
            ).getPagedList(
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
            return@launch;
        }
        coroutineScope.launch {
//            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(payerPaymentWeightResult.getOrNull()!!.results)
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
            ExpenseRepository(expenseStorage()).getOne(expenseId)
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
//            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
//                .show()
        }
        onSuccess(expenseResult.getOrNull()!!)
    }
}

private fun fetchPayerAsync(
    payerId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Payer) -> Unit
) {
    val TAG = "UpdatePayerScreen.fetchPayerAsync"
    coroutineScope.launch {
        val payerResult =
            PayerRepository(payerStorage()).getOne(payerId)
        if (payerResult.isFailure) {
            Log.w(TAG, "Error: " + payerResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context,
                    "Error: ${payerResult.exceptionOrNull()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@launch;
        }
        coroutineScope.launch {
//            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
//                .show()
        }
        onSuccess(payerResult.getOrNull()!!)
    }
}

private fun round3Digits(number: Float): String{
    return String.format("%.3f", number)
}

private fun chargesSimplified(charges: List<Charge>): List<ChargeSimplified>{
    val pairsAmount = mutableMapOf<Pair<String,String>,Float>()
    for (charge in charges) {
        val source = charge.source.id;
        for (entry in charge.destination) {
            val key = Pair(source,entry.key)
            val key2 = Pair(source,entry.key)
            if (key != key2){
                throw Exception("uai")
            }
            if (!pairsAmount.containsKey(key)){
                pairsAmount.put(key, 0f)
            }
            for (bills in entry.value) {
                pairsAmount[key] = pairsAmount[key]!! + bills.amount;
            }

        }
    }
    val onlyDifference = mutableListOf<ChargeSimplified>()
    for (entry in pairsAmount) {
        val invertedKey = Pair(entry.key.second, entry.key.first);
        if(!pairsAmount.containsKey(invertedKey)){
            onlyDifference.add(ChargeSimplified(entry.key.first, entry.key.second, entry.value))
        }else{
            val difference = entry.value - pairsAmount[invertedKey]!!
            if(difference>0){
                onlyDifference.add(ChargeSimplified(entry.key.first, entry.key.second, difference))
            }
        }
    }
    return onlyDifference
}

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        CalculationScreen(rememberNavController(), "fake")
    }
}