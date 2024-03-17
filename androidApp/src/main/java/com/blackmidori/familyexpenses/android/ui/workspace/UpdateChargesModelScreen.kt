package com.blackmidori.familyexpenses.android.ui.workspace

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
import com.blackmidori.familyexpenses.android.AppScreen
import com.blackmidori.familyexpenses.android.MyApplicationTheme
import com.blackmidori.familyexpenses.android.shared.ui.SimpleAppBar
import com.blackmidori.familyexpenses.android.shared.ui.SimpleScaffold
import com.blackmidori.familyexpenses.models.ChargesModel
import com.blackmidori.familyexpenses.repositories.ChargesModelRepository
import com.blackmidori.familyexpenses.stores.chargesModelStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
fun UpdateChargesModelScreen(
    navController: NavHostController,
    chargesModelId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var name by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = null) {
        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
        fetchChargesModelAsync(chargesModelId, coroutineScope, context) {
            name = it.name
        }
    }
    SimpleScaffold(topBar = {
        SimpleAppBar(
            navController = navController,
            title = { Text(stringResource(AppScreen.UpdateChargesModel.title)) },
        )
    }) {
        Column {
            TextField(value = name, onValueChange = {
                name = it
            })
            Button(onClick = {
                coroutineScope.launch {
                    val TAG = "UpdateChargesModel.update"
                    val chargeModel = ChargesModel(chargesModelId, Instant.DISTANT_PAST, chargesModelId, name)
                    val chargeModelResult =
                        ChargesModelRepository(chargesModelStore(context)).update(chargeModel)
                    if (chargeModelResult.isFailure) {
                        Log.w(TAG, "Error: " + chargeModelResult.exceptionOrNull())

                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "Error: ${chargeModelResult.exceptionOrNull()}",
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
                        val TAG = "UpdateChargesModel.delete"
                        val deleteResult =
                            ChargesModelRepository(chargesModelStore(context)).delete(chargesModelId)
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

private fun fetchChargesModelAsync(
    chargesModelId: String,
    coroutineScope: CoroutineScope,
    context: Context,
    onSuccess: (ChargesModel) -> Unit
) {
    val TAG = "UpdateChargesModelScreen.fetchChargesModelAsync"
    coroutineScope.launch {
        val chargesModelResult =
            ChargesModelRepository(chargesModelStore(context)).getOne(chargesModelId)
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
            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT)
                .show()
        }
        onSuccess(chargesModelResult.getOrNull()!!)
    }
}

@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        UpdateChargesModelScreen(rememberNavController(), chargesModelId = "fake")
    }
}