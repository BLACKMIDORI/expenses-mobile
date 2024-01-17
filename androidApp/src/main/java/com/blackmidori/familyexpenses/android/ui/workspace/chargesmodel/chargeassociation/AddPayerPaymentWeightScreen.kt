package com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.chargeassociation

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
import com.blackmidori.familyexpenses.models.PayerPaymentWeight
import com.blackmidori.familyexpenses.repositories.ChargeAssociationRepository
import com.blackmidori.familyexpenses.repositories.ExpenseRepository
import com.blackmidori.familyexpenses.repositories.PayerPaymentWeightRepository
import com.blackmidori.familyexpenses.repositories.PayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPayerPaymentWeightScreen(
    navController: NavHostController,
    chargeAssociationId: String,
    workspaceId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var payerPaymentWeight by remember {
        mutableStateOf<PayerPaymentWeight?>(null)
    }
    var payers by remember {
        mutableStateOf(arrayOf<Payer>())
    }

    LaunchedEffect(key1 = null) {
        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()

        fetchPayersAsync(workspaceId, coroutineScope, context) {
            payers = it
        }
    }
    SimpleScaffold(topBar = {
        SimpleAppBar(
            navController = navController,
            title = { Text(stringResource(AppScreen.UpdatePayerPaymentWeight.title)) },
        )
    }) {
        Column {
            TextField(value = payerPaymentWeight?.weight?.toString() ?: "", onValueChange = {
                payerPaymentWeight = PayerPaymentWeight(
                    payerPaymentWeight?.id ?: "",
                    payerPaymentWeight?.creationDateTime ?: Instant.DISTANT_PAST,
                    it.toDouble(),
                    payerPaymentWeight?.payer ?: Payer("", Instant.DISTANT_PAST, ""),
                )
            })
            var expanded by remember {
                mutableStateOf(false)
            }
            ExposedDropdownMenuBox(expanded = expanded, { expanded = it }) {

                val payerId = payerPaymentWeight?.payer?.id
                TextField(
                    value = payers.find { it.id == payerId }?.name
                        ?: payerPaymentWeight?.payer?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (payers.isEmpty()) {
                        Text(text = "Empty")
                    }

                    for (item in payers) {
                        DropdownMenuItem(
                            text = { Text(text = item.name) },
                            onClick = {
                                payerPaymentWeight = PayerPaymentWeight(
                                    payerPaymentWeight?.id ?: "",
                                    payerPaymentWeight?.creationDateTime ?: Instant.DISTANT_PAST,
                                    payerPaymentWeight?.weight ?: .0,
                                    item,
                                )
                                expanded = false
                            }
                        )
                    }
                }
            }
            Button(onClick = {
                Thread {
                    val TAG = "AddPayerPaymentWeightScreen.submit"
                    val localPayerPaymentWeight = payerPaymentWeight ?: return@Thread
                    val chargeAssociationResult =
                        PayerPaymentWeightRepository(httpClient = HttpClientJavaImpl()).add(chargeAssociationId, localPayerPaymentWeight)
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
                }.start()
            }) {
                Text("Submit")
            }
        }
    }
}

private fun fetchPayerPaymentWeightAsync(
    chargeAssociationId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (PayerPaymentWeight) -> Unit
) {
    val TAG = "AddPayerPaymentWeightScreen.fetchPayerPaymentWeightAsync"
    Thread {
        val payerPaymentWeightResult =
            PayerPaymentWeightRepository(httpClient = HttpClientJavaImpl()).getOne(
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
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(payerPaymentWeightResult.getOrNull()!!)
    }.start()
}

private fun fetchPayersAsync(
    workspaceId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<Payer>) -> Unit
) {
    val TAG = "AddPayerPaymentWeightScreen.fetchPayersAsync"
    Thread {
        val payerResult =
            PayerRepository(httpClient = HttpClientJavaImpl()).getPagedList(workspaceId)
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
            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(payerResult.getOrNull()!!.results)
    }.start()
}

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        AddPayerPaymentWeightScreen(
            rememberNavController(),
            chargeAssociationId = "fake",
            workspaceId = "fake",
        )
    }
}