package com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.chargeassociation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.familyexpenses.android.AppScreen
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.android.R
import com.blackmidori.familyexpenses.android.core.HttpClientJavaImpl
import com.blackmidori.familyexpenses.android.shared.ui.SimpleAppBar
import com.blackmidori.familyexpenses.android.shared.ui.SimpleScaffold
import com.blackmidori.familyexpenses.models.ChargeAssociation
import com.blackmidori.familyexpenses.models.ChargesModel
import com.blackmidori.familyexpenses.models.Expense
import com.blackmidori.familyexpenses.models.Payer
import com.blackmidori.familyexpenses.models.PayerPaymentWeight
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.repositories.ChargeAssociationRepository
import com.blackmidori.familyexpenses.repositories.ChargesModelRepository
import com.blackmidori.familyexpenses.repositories.PayerPaymentWeightRepository
import com.blackmidori.familyexpenses.repositories.WorkspaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
fun ChargeAssociationScreen(
    navController: NavHostController,
    chargeAssociationId: String,
    workspaceId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var chargeAssociation by remember {
        mutableStateOf(
            ChargeAssociation(
                "",
                Instant.DISTANT_PAST,
                "",
                Expense("", Instant.DISTANT_PAST, ""),
                Payer("", Instant.DISTANT_PAST, "")
            )
        )
    }
    var list by remember {
        mutableStateOf(arrayOf<PayerPaymentWeight>())
    }
    LaunchedEffect(key1 = null) {
        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
        fetchChargeAssociationAsync(chargeAssociationId, coroutineScope, context) {
            chargeAssociation = it
        }
        fetchPayerPaymentWeightsAsync(chargeAssociationId, coroutineScope, context) {
            list = it
        }
    }

    val onAddClick = {
        navController.navigate(
            AppScreen.AddPayerPaymentWeight.route.replace(
                "{chargeAssociationId}", chargeAssociationId
            ).replace(
                "{workspaceId}", workspaceId
            )
        )
    }
    val onOpenClick: (id: String) -> Unit = {
        Toast.makeText(context, "payer payment weight id: $it", Toast.LENGTH_SHORT).show()
    }
    val onUpdateClick: (id: String) -> Unit = {
        navController.navigate(
            AppScreen.UpdatePayerPaymentWeight.route.replace(
                "{id}", it
            ).replace(
                "{workspaceId}", workspaceId
            )
        )
    }

    SimpleScaffold(
        topBar = {
            SimpleAppBar(
                navController = navController,
                title = { Text(stringResource(AppScreen.ChargeAssociation.title) + " - ${chargeAssociation.name}") })
        }
    ) {
        LazyColumn {
            item {
                Button(onClick = onAddClick) {
                    Text("Add Payer Payment Weight")
                }
            }
            item { Text("Payer Payment Weight Count: ${list.size}") }
            for (item in list) {
                item {
                    Row {
                        Button(onClick = {
                            onOpenClick(item.id)
                        }) {
                            Column {
                                Text(item.weight.toString())
                                Text(item.creationDateTime.toString())
                            }
                        }
                        Button(onClick = {
                            onUpdateClick(item.id)
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

private fun fetchChargeAssociationAsync(
    chargeAssociationId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (ChargeAssociation) -> Unit
) {
    val TAG = "ChargeAssociationScreen.fetchChargeAssociationAsync"
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

private fun fetchPayerPaymentWeightsAsync(
    chargeAssociationId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<PayerPaymentWeight>) -> Unit
) {
    val TAG = "ChargeAssociationScreen.fetchPayerPaymentWeightsAsync"
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

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        ChargeAssociationScreen(rememberNavController(), "fake", "")
    }
}