package com.blackmidori.expenses.android.ui.workspace

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackmidori.expenses.android.AppScreen
import com.blackmidori.expenses.android.MyApplicationTheme
import com.blackmidori.expenses.android.shared.ui.SimpleAppBar
import com.blackmidori.expenses.android.shared.ui.SimpleScaffold
import com.blackmidori.expenses.models.ChargesModel
import com.blackmidori.expenses.repositories.ChargesModelRepository
import com.blackmidori.expenses.stores.chargesModelStorage
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
fun AddChargesModelScreen(
    navController: NavHostController,
    workspaceId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var name by remember {
        mutableStateOf("")
    }

    SimpleScaffold(topBar = {
        SimpleAppBar(
            navController = navController,
            title = { Text(stringResource(AppScreen.AddChargesModel.title)) },
        )
    }) {
        Column {
            TextField(value = name, onValueChange = {
                name = it
            })
            Button(onClick = {
                coroutineScope.launch {
                    val TAG = "AddChargesModel.submit"
                    val chargesModel = ChargesModel("", Instant.DISTANT_PAST, workspaceId, name)
                    val chargesModelResult =
                        ChargesModelRepository(chargesModelStorage()).add(workspaceId, chargesModel)
                    if (chargesModelResult.isFailure) {
                        Log.w(TAG, "Error: " + chargesModelResult.exceptionOrNull())

                        Toast.makeText(
                            context,
                            "Error: ${chargesModelResult.exceptionOrNull()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Added",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigateUp()
                    }
                }
            }) {
                Text("Submit")
            }
        }
    }
}


@Preview
@Composable
private fun Preview() {
    MyApplicationTheme {
        AddChargesModelScreen(rememberNavController(),"fake")
    }
}