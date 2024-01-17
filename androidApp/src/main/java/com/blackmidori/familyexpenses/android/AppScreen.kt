package com.blackmidori.familyexpenses.android

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blackmidori.familyexpenses.android.core.HttpClientJavaImpl
import com.blackmidori.familyexpenses.android.ui.AddWorkspaceScreen
import com.blackmidori.familyexpenses.android.ui.HomeScreen
import com.blackmidori.familyexpenses.android.ui.UpdateWorkspaceScreen
import com.blackmidori.familyexpenses.android.ui.workspace.AddChargesModelScreen
import com.blackmidori.familyexpenses.android.ui.workspace.AddExpenseScreen
import com.blackmidori.familyexpenses.android.ui.workspace.AddPayerScreen
import com.blackmidori.familyexpenses.android.ui.workspace.UpdateChargesModelScreen
import com.blackmidori.familyexpenses.android.ui.workspace.UpdateExpenseScreen
import com.blackmidori.familyexpenses.android.ui.workspace.UpdatePayerScreen
import com.blackmidori.familyexpenses.android.ui.workspace.WorkspaceScreen
import com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.AddChargeAssociationScreen
import com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.ChargesModelScreen
import com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.UpdateChargeAssociationScreen
import com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.chargeassociation.AddPayerPaymentWeightScreen
import com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.chargeassociation.ChargeAssociationScreen
import com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.chargeassociation.UpdatePayerPaymentWeightScreen
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.repositories.WorkspaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * enum values that represent the screens in the app
 */
enum class AppScreen(@StringRes val title: Int, val route: String) {
    Home(
        title = R.string.home_screen, route = Home.name
    ),
    AddWorkspace(title = R.string.add_workspace, route = AddWorkspace.name),
    UpdateWorkspace(
        title = R.string.update_workspace, route = UpdateWorkspace.name + "/{id}"
    ),
    Workspace(
        title = R.string.workspace, route = Workspace.name + "/{id}"
    ),
    AddPayer(
        title = R.string.add_payer, route = AddPayer.name + "/{workspaceId}"
    ),
    UpdatePayer(
        title = R.string.update_payer, route = UpdatePayer.name + "/{id}"
    ),
    AddExpense(
        title = R.string.add_expense, route = AddExpense.name + "/{workspaceId}"
    ),
    UpdateExpense(
        title = R.string.update_expense, route = UpdateExpense.name + "/{id}"
    ),
    AddChargesModel(
        title = R.string.add_charges_model, route = AddChargesModel.name + "/{workspaceId}"
    ),
    UpdateChargesModel(
        title = R.string.update_charges_model, route = UpdateChargesModel.name + "/{id}"
    ),
    ChargesModel(
        title = R.string.charges_model, route = ChargesModel.name + "/{id}/{workspaceId}"
    ),
    AddChargeAssociation(
        title = R.string.add_charge_association,
        route = AddChargeAssociation.name + "/{chargesModelId}/{workspaceId}"
    ),
    UpdateChargeAssociation(
        title = R.string.update_charge_association,
        route = UpdateChargeAssociation.name + "/{id}/{workspaceId}"
    ),
    ChargeAssociation(
        title = R.string.charge_association, route = ChargeAssociation.name + "/{id}/{workspaceId}"
    ),
    AddPayerPaymentWeight(
        title = R.string.add_payer_payment_weight,
        route = AddPayerPaymentWeight.name + "/{chargeAssociationId}/{workspaceId}"
    ),
    UpdatePayerPaymentWeight(
        title = R.string.update_payer_payment_weight,
        route = UpdatePayerPaymentWeight.name + "/{id}/{workspaceId}"
    ),
}

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var updateList by remember {
        mutableStateOf(false)
    }
    var list by remember {
        mutableStateOf(arrayOf<Workspace>())
    }
    LaunchedEffect(key1 = updateList) {
        Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()
        fetchWorkspacesAsync(coroutineScope, context) {
            list = it
        }
    }
    MyApplicationTheme {
        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.route,
        ) {
            composable(route = AppScreen.Home.route) {
                HomeScreen(navController = navController, list = list, onClickUpdateWorkspace = {
                    navController.navigate(
                        AppScreen.UpdateWorkspace.route.replace(
                            "{id}", it
                        )
                    )
                }, onClickOpenWorkspace = {
                    navController.navigate(
                        AppScreen.Workspace.route.replace(
                            "{id}", it
                        )
                    )
                }, onClickAddWorkspace = {
                    navController.navigate(AppScreen.AddWorkspace.route)
                })
            }
            composable(route = AppScreen.AddWorkspace.route) {
                AddWorkspaceScreen(
                    navController = navController,
                    onSuccess = {
                        updateList = !updateList
                    },
                )
            }
            composable(route = AppScreen.UpdateWorkspace.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                UpdateWorkspaceScreen(
                    navController = navController,
                    workspaceId = id!!,
                    onSuccess = {
                        updateList = !updateList
                    },
                )
            }
            composable(route = AppScreen.Workspace.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                WorkspaceScreen(
                    navController = navController,
                    workspaceId = id!!,
                )
            }
            composable(route = AppScreen.AddPayer.route) { navBackStackEntry ->
                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                AddPayerScreen(
                    navController = navController,
                    workspaceId = workspaceId!!,
                )
            }
            composable(route = AppScreen.UpdatePayer.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                UpdatePayerScreen(
                    navController = navController,
                    payerId = id!!,
                )
            }
            composable(route = AppScreen.AddExpense.route) { navBackStackEntry ->
                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                AddExpenseScreen(
                    navController = navController,
                    workspaceId = workspaceId!!,
                )
            }
            composable(route = AppScreen.UpdateExpense.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                UpdateExpenseScreen(
                    navController = navController,
                    expenseId = id!!,
                )
            }
            composable(route = AppScreen.AddChargesModel.route) { navBackStackEntry ->
                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                AddChargesModelScreen(
                    navController = navController,
                    workspaceId = workspaceId!!,
                )
            }
            composable(route = AppScreen.UpdateChargesModel.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                UpdateChargesModelScreen(
                    navController = navController,
                    chargesModelId = id!!,
                )
            }
            composable(route = AppScreen.ChargesModel.route) { navBackStackEntry ->
                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                val id = navBackStackEntry.arguments?.getString("id")
                ChargesModelScreen(
                    navController = navController,
                    chargesModelId = id!!,
                    workspaceId = workspaceId!!
                )
            }

            composable(route = AppScreen.AddChargeAssociation.route) { navBackStackEntry ->
                val chargesModelId = navBackStackEntry.arguments?.getString("chargesModelId")
                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                AddChargeAssociationScreen(
                    navController = navController,
                    chargesModelId = chargesModelId!!,
                    workspaceId = workspaceId!!,
                )
            }
            composable(route = AppScreen.UpdateChargeAssociation.route) { navBackStackEntry ->
                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                val id = navBackStackEntry.arguments?.getString("id")
                UpdateChargeAssociationScreen(
                    navController = navController,
                    chargeAssociationId = id!!,
                    workspaceId = workspaceId!!,
                )
            }
            composable(route = AppScreen.ChargeAssociation.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                ChargeAssociationScreen(
                    navController = navController,
                    chargeAssociationId = id!!,
                    workspaceId = workspaceId!!
                )
            }


            composable(route = AppScreen.AddPayerPaymentWeight.route) { navBackStackEntry ->
                val chargeAssociationId = navBackStackEntry.arguments?.getString("chargeAssociationId")
                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                AddPayerPaymentWeightScreen(
                    navController = navController,
                    chargeAssociationId = chargeAssociationId!!,
                    workspaceId = workspaceId!!,
                )
            }
            composable(route = AppScreen.UpdatePayerPaymentWeight.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                UpdatePayerPaymentWeightScreen(
                    navController = navController,
                    payerPaymentWeightId = id!!,
                    workspaceId = workspaceId!!,
                )
            }
        }
    }
}

private fun fetchWorkspacesAsync(
    coroutineScope: CoroutineScope, context: Context, onSuccess: (Array<Workspace>) -> Unit
) {
    val TAG = "fetchWorkspacesAsync"
    Thread {
        val workspacesResult = WorkspaceRepository(httpClient = HttpClientJavaImpl()).getPagedList()
        if (workspacesResult.isFailure) {
            Log.w(TAG, "Error: " + workspacesResult.exceptionOrNull())
            coroutineScope.launch {
                Toast.makeText(
                    context, "Error: ${workspacesResult.exceptionOrNull()}", Toast.LENGTH_SHORT
                ).show()
            }
            return@Thread;
        }
        coroutineScope.launch {
            Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        }
        onSuccess(workspacesResult.getOrNull()!!.results)
    }.start()
}
