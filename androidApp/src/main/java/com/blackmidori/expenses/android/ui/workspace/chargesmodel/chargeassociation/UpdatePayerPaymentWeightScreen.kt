package com.blackmidori.expenses.android.ui.workspace.chargesmodel.chargeassociation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.expenses.android.AppScreen
import com.blackmidori.expenses.android.MyApplicationTheme
import com.blackmidori.expenses.android.shared.ui.SimpleAppBar
import com.blackmidori.expenses.android.shared.ui.SimpleScaffold
import com.blackmidori.expenses.models.Payer
import com.blackmidori.expenses.models.PayerPaymentWeight
import com.blackmidori.expenses.repositories.PayerPaymentWeightRepository
import com.blackmidori.expenses.repositories.PayerRepository
import com.blackmidori.expenses.stores.payerPaymentWeightStore
import com.blackmidori.expenses.stores.payerStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePayerPaymentWeightScreen(
    navController: NavHostController,
    payerPaymentWeightId: String,
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
        fetchPayerPaymentWeightAsync(payerPaymentWeightId, coroutineScope, context) {
            payerPaymentWeight = it
        }
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
            TextField(
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = payerPaymentWeight?.weight?.toString() ?: "", onValueChange = {
                    var weight = payerPaymentWeight?.weight
                    try {
                        weight = it.toDouble();
                    } catch (e: NumberFormatException) {
                        //Silence
                    }
                    payerPaymentWeight = PayerPaymentWeight(
                        payerPaymentWeight?.id ?: "",
                        payerPaymentWeight?.creationDateTime ?: Instant.DISTANT_PAST,
                        payerPaymentWeight?.chargeAssociationId ?: "",
                        weight ?: .0,
                        payerPaymentWeight?.payer ?: Payer("", Instant.DISTANT_PAST, "",""),
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
                                    payerPaymentWeight?.chargeAssociationId ?: "",
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
                coroutineScope.launch {
                    val TAG = "UpdatePayerPaymentWeightScreen.update"
                    val localPayerPaymentWeight = payerPaymentWeight ?: return@launch
                    val payerPaymentWeightResult =
                        PayerPaymentWeightRepository(
                            payerPaymentWeightStore(context),
                            payerStore(context),
                        ).update(
                            localPayerPaymentWeight
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
                        val TAG = "UpdatePayerPaymentWeightScreen.delete"
                        val localPayerPaymentWeight = payerPaymentWeight ?: return@launch
                        val deleteResult =
                            PayerPaymentWeightRepository(
                                payerPaymentWeightStore(context),
                                payerStore(context),
                            ).delete(
                                localPayerPaymentWeight.id
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
                    }
                }) {
                Text("Delete")
            }
        }
    }
}

private fun fetchPayerPaymentWeightAsync(
    payerPaymentWeightId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (PayerPaymentWeight) -> Unit
) {
    val TAG = "UpdatePayerPaymentWeightScreen.fetchPayerPaymentWeightAsync"
    coroutineScope.launch {
        val payerPaymentWeightResult =
            PayerPaymentWeightRepository(
                payerPaymentWeightStore(context),
                payerStore(context),
            ).getOne(
                payerPaymentWeightId
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
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(payerPaymentWeightResult.getOrNull()!!)
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
            PayerRepository(payerStore(context)).getPagedList(workspaceId)
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
            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(payersResult.getOrNull()!!.results)
    }
}

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        UpdatePayerPaymentWeightScreen(
            rememberNavController(),
            workspaceId = "fake",
            payerPaymentWeightId = "fake"
        )
    }
}