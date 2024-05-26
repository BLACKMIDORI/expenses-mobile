package com.blackmidori.expenses.android

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.blackmidori.expenses.android.ui.AddWorkspaceScreen
import com.blackmidori.expenses.android.ui.HomeScreen
import com.blackmidori.expenses.android.ui.UpdateWorkspaceScreen
import com.blackmidori.expenses.android.ui.workspace.AddChargesModelScreen
import com.blackmidori.expenses.android.ui.workspace.AddExpenseScreen
import com.blackmidori.expenses.android.ui.workspace.AddPayerScreen
import com.blackmidori.expenses.android.ui.workspace.UpdateChargesModelScreen
import com.blackmidori.expenses.android.ui.workspace.UpdateExpenseScreen
import com.blackmidori.expenses.android.ui.workspace.UpdatePayerScreen
import com.blackmidori.expenses.android.ui.workspace.WorkspaceScreen
import com.blackmidori.expenses.android.ui.workspace.chargesmodel.AddChargeAssociationScreen
import com.blackmidori.expenses.android.ui.workspace.chargesmodel.ChargesModelScreen
import com.blackmidori.expenses.android.ui.workspace.chargesmodel.UpdateChargeAssociationScreen
import com.blackmidori.expenses.android.ui.workspace.chargesmodel.calculation.CalculationScreen
import com.blackmidori.expenses.android.ui.workspace.chargesmodel.chargeassociation.AddPayerPaymentWeightScreen
import com.blackmidori.expenses.android.ui.workspace.chargesmodel.chargeassociation.ChargeAssociationScreen
import com.blackmidori.expenses.android.ui.workspace.chargesmodel.chargeassociation.UpdatePayerPaymentWeightScreen
import com.blackmidori.expenses.models.ChargeAssociation
import com.blackmidori.expenses.models.ChargesModel
import com.blackmidori.expenses.models.Expense
import com.blackmidori.expenses.models.Payer
import com.blackmidori.expenses.models.PayerPaymentWeight
import com.blackmidori.expenses.models.Workspace
import com.blackmidori.expenses.repositories.ChargeAssociationRepository
import com.blackmidori.expenses.repositories.ChargesModelRepository
import com.blackmidori.expenses.repositories.ExpenseRepository
import com.blackmidori.expenses.repositories.PayerPaymentWeightRepository
import com.blackmidori.expenses.repositories.PayerRepository
import com.blackmidori.expenses.repositories.WorkspaceRepository
import com.blackmidori.expenses.stores.chargeAssociationStorage
import com.blackmidori.expenses.stores.chargesModelStorage
import com.blackmidori.expenses.stores.expenseStorage
import com.blackmidori.expenses.stores.payerPaymentWeightStorage
import com.blackmidori.expenses.stores.payerStorage
import com.blackmidori.expenses.stores.workspaceStorage
import com.blackmidori.expenses.utils.SampleGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

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
    Calculation(
        title = R.string.calculation,
        route = Calculation.name + "/{chargesModelId}/{workspaceId}"
    ),
}

@Composable
@ExperimentalFoundationApi
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
//        Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()
        fetchWorkspacesAsync(coroutineScope, context) {
            list = it
            if (it.isEmpty()) {
                seedTemplateData()
                fetchWorkspacesAsync(coroutineScope, context) {
                    list = it
                }
            }
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
                val chargeAssociationId =
                    navBackStackEntry.arguments?.getString("chargeAssociationId")
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
            composable(route = AppScreen.Calculation.route) { navBackStackEntry ->
                val chargesModelId = navBackStackEntry.arguments?.getString("chargesModelId")
//                val workspaceId = navBackStackEntry.arguments?.getString("workspaceId")
                CalculationScreen(
                    navController = navController,
                    chargesModelId = chargesModelId!!,
//                    workspaceId = workspaceId!!,
                )
            }
        }
    }
}

private fun fetchWorkspacesAsync(
    coroutineScope: CoroutineScope, context: Context, onSuccess: suspend (Array<Workspace>) -> Unit
) {
    val TAG = "fetchWorkspacesAsync"
    coroutineScope.launch {
        val workspaceRepository = WorkspaceRepository(workspaceStorage())
        val workspacesResult = workspaceRepository.getPagedList().let {
            if (it.isSuccess && it.getOrThrow().results.isEmpty()) {
                val chargesModelRepository = ChargesModelRepository(chargesModelStorage())
                val payerStore = payerStorage();
                val expenseRepository = PayerRepository(payerStore)
                val expenseStore = expenseStorage()
                val payerRepository = ExpenseRepository(expenseStore)
                val chargeAssociationRepository =
                    ChargeAssociationRepository(chargeAssociationStorage(), expenseStore, payerStore)
                val payerPaymentWeightRepository =
                    PayerPaymentWeightRepository(payerPaymentWeightStorage(), payerStore)
                SampleGenerator(
                    workspaceRepository,
                    chargesModelRepository,
                    expenseRepository,
                    payerRepository,
                    chargeAssociationRepository,
                    payerPaymentWeightRepository,
                ).generateSample()
                return@let workspaceRepository.getPagedList();
            }
            return@let it
        }

        if (workspacesResult.isFailure) {
            Log.w(TAG, "Error: " + workspacesResult.exceptionOrNull())
            Toast.makeText(
                context, "Error: ${workspacesResult.exceptionOrNull()}", Toast.LENGTH_SHORT
            ).show()
            return@launch;
        }
//        Toast.makeText(context, "List Updated", Toast.LENGTH_SHORT).show()
        onSuccess(workspacesResult.getOrNull()!!.results)
    }
}


suspend fun seedTemplateData() {
    val workspaceRepository = WorkspaceRepository(workspaceStorage())
    val payerRepositoryRepository = PayerRepository(payerStorage())
    val expenseRepository = ExpenseRepository(expenseStorage())
    val chargesModelRepository = ChargesModelRepository(chargesModelStorage())
    val chargeAssociationRepository = ChargeAssociationRepository(
        chargeAssociationStorage(), expenseStorage(), payerStorage()
    )
    val payerPaymentWeightRepository = PayerPaymentWeightRepository(
        payerPaymentWeightStorage(), payerStorage()
    )
    val workspace =
        workspaceRepository.add(Workspace("", Instant.DISTANT_PAST, "Template")).getOrNull()
            ?: return
    val motherPayer = payerRepositoryRepository.add(
        workspace.id,
        Payer("", Instant.DISTANT_PAST, workspace.id, "Mother")
    ).getOrNull()
        ?: return
    val fatherPayer = payerRepositoryRepository.add(
        workspace.id,
        Payer("", Instant.DISTANT_PAST, workspace.id, "Father")
    ).getOrNull()
        ?: return
    val daughterPayer = payerRepositoryRepository.add(
        workspace.id,
        Payer("", Instant.DISTANT_PAST, workspace.id, "Daughter")
    ).getOrNull()
        ?: return
    val waterExpense = expenseRepository.add(
        workspace.id,
        Expense("", Instant.DISTANT_PAST, workspace.id, "Water")
    ).getOrNull()
        ?: return
    val energyExpense = expenseRepository.add(
        workspace.id,
        Expense("", Instant.DISTANT_PAST, workspace.id, "Energy")
    ).getOrNull()
        ?: return
    val netflixExpense = expenseRepository.add(
        workspace.id,
        Expense("", Instant.DISTANT_PAST, workspace.id, "Netflix")
    ).getOrNull()
        ?: return
    val internetExpense = expenseRepository.add(
        workspace.id,
        Expense("", Instant.DISTANT_PAST, workspace.id, "Internet")
    ).getOrNull()
        ?: return
    val chargeModel = chargesModelRepository.add(
        workspace.id,
        ChargesModel("", Instant.DISTANT_PAST, workspace.id, "Model Template")
    ).getOrNull()
        ?: return
    val energyByMotherAssociation = chargeAssociationRepository.add(
        chargeModel.id,
        ChargeAssociation(
            "",
            Instant.DISTANT_PAST,
            workspace.id,
            "energy by mother",
            energyExpense,
            motherPayer
        )
    ).getOrNull()
        ?: return
    val waterByFatherAssociation = chargeAssociationRepository.add(
        chargeModel.id,
        ChargeAssociation(
            "",
            Instant.DISTANT_PAST,
            workspace.id,
            "water by father",
            waterExpense,
            fatherPayer
        )
    ).getOrNull()
        ?: return
    val netflixByDaughterAssociation = chargeAssociationRepository.add(
        chargeModel.id,
        ChargeAssociation(
            "",
            Instant.DISTANT_PAST,
            workspace.id,
            "netflix by daughter",
            netflixExpense,
            daughterPayer
        )
    ).getOrNull()
        ?: return

    val internetByDaughterAssociation = chargeAssociationRepository.add(
        chargeModel.id,
        ChargeAssociation(
            "",
            Instant.DISTANT_PAST,
            workspace.id,
            "internet by daughter",
            internetExpense,
            daughterPayer
        )
    ).getOrNull()
        ?: return

    // motherWeightForEnergyByMother
    payerPaymentWeightRepository.add(
        energyByMotherAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 3f, motherPayer)
    ).getOrNull()

    // fatherWeightForEnergyByMother
    payerPaymentWeightRepository.add(
        energyByMotherAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 3f, fatherPayer)
    ).getOrNull()

    // daughterWeightForEnergyByMother
    payerPaymentWeightRepository.add(
        energyByMotherAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 3f, daughterPayer)
    ).getOrNull()

    // motherWeightForWaterByFather
    payerPaymentWeightRepository.add(
        waterByFatherAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 2f, motherPayer)
    ).getOrNull()

    // fatherWeightForWaterByFather
    payerPaymentWeightRepository.add(
        waterByFatherAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 2f, fatherPayer)
    ).getOrNull()

    // motherWeightForNetflixByDaughter
    payerPaymentWeightRepository.add(
        netflixByDaughterAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 2f, motherPayer)
    ).getOrNull()

    // daughterWeightForNetflixByDaughter
    payerPaymentWeightRepository.add(
        netflixByDaughterAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 2f, daughterPayer)
    ).getOrNull()

    // motherWeightForInternetByDaughter
    payerPaymentWeightRepository.add(
        internetByDaughterAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 3f, motherPayer)
    ).getOrNull()

    // fatherWeightForInternetByDaughter
    payerPaymentWeightRepository.add(
        internetByDaughterAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 3f, fatherPayer)
    ).getOrNull()

    // daughterWeightForInternetByDaughter
    payerPaymentWeightRepository.add(
        internetByDaughterAssociation.id,
        PayerPaymentWeight("", Instant.DISTANT_PAST, workspace.id, 1f / 3f, daughterPayer)
    ).getOrNull()
}