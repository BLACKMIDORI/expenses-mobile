package com.blackmidori.expenses.android.ui.workspace.chargesmodel.chargeassociation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import com.blackmidori.expenses.models.ChargeAssociation
import com.blackmidori.expenses.models.Expense
import com.blackmidori.expenses.models.Payer
import com.blackmidori.expenses.models.PayerPaymentWeight
import com.blackmidori.expenses.repositories.ChargeAssociationRepository
import com.blackmidori.expenses.repositories.PayerPaymentWeightRepository
import com.blackmidori.expenses.stores.chargeAssociationStorage
import com.blackmidori.expenses.stores.expenseStorage
import com.blackmidori.expenses.stores.payerPaymentWeightStorage
import com.blackmidori.expenses.stores.payerStorage
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
                "",
                Expense("", Instant.DISTANT_PAST, workspaceId, ""),
                Payer("", Instant.DISTANT_PAST, workspaceId, "")
            )
        )
    }
    var list by remember {
        mutableStateOf(arrayOf<PayerPaymentWeight>())
    }
    LaunchedEffect(key1 = null) {
//        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
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
//    val onOpenClick: (id: String) -> Unit = {
//        Toast.makeText(context, "payer payment weight id: $it", Toast.LENGTH_SHORT).show()
//    }
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_payer_payment_weight)
                )
            }
        }
    ) {
        LazyColumn {
            item { Text("Payer Payment Weight Count: ${list.size}") }
            for (item in list) {
                item {

                    ListItem(
                        modifier = Modifier.clickable {
                            onUpdateClick(item.id)
                        },
                        headlineContent = { Text("${item.payer.name} (${item.weight})") },
                        supportingContent = { Text(item.creationDateTime.toString()) },
                        trailingContent = {
                            IconButton({
                                onUpdateClick(item.id)
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

private fun fetchChargeAssociationAsync(
    chargeAssociationId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (ChargeAssociation) -> Unit
) {
    val TAG = "ChargeAssociationScreen.fetchChargeAssociationAsync"
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

private fun fetchPayerPaymentWeightsAsync(
    chargeAssociationId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (Array<PayerPaymentWeight>) -> Unit
) {
    val TAG = "ChargeAssociationScreen.fetchPayerPaymentWeightsAsync"
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

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        ChargeAssociationScreen(rememberNavController(), "fake", "")
    }
}