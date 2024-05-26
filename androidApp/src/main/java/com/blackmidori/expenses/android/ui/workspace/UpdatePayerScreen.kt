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
import com.blackmidori.expenses.models.Payer
import com.blackmidori.expenses.repositories.PayerRepository
import com.blackmidori.expenses.stores.payerStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
fun UpdatePayerScreen(
    navController: NavHostController,
    payerId: String,
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
//        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
        fetchPayerAsync(payerId, coroutineScope, context) {
            workspaceId = it.workspaceId
            name = it.name
        }
    }
    SimpleScaffold(topBar = {
        SimpleAppBar(
            navController = navController,
            title = { Text(stringResource(AppScreen.UpdatePayer.title)) },
        )
    }) {
        Column {
            TextField(value = name, onValueChange = {
                name = it
            })
            Button(onClick = {
                coroutineScope.launch {
                    val TAG = "UpdatePayerScreen.update"
                    val payer = Payer(payerId, Instant.DISTANT_PAST,workspaceId, name)
                    val payerResult =
                        PayerRepository(payerStorage()).update(payer)
                    if (payerResult.isFailure) {
                        Log.w(TAG, "Error: " + payerResult.exceptionOrNull())

                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "Error: ${payerResult.exceptionOrNull()}",
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
                        val TAG = "UpdatePayerScreen.delete"
                        val deleteResult =
                            PayerRepository(payerStorage()).delete(payerId)
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
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(payerResult.getOrNull()!!)
    }
}

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        UpdatePayerScreen(rememberNavController(), payerId = "fake")
    }
}