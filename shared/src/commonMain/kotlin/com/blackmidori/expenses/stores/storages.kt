package com.blackmidori.expenses.stores

import com.blackmidori.expenses.models.ChargeAssociation
import com.blackmidori.expenses.models.ChargeAssociationList
import com.blackmidori.expenses.models.ChargesModel
import com.blackmidori.expenses.models.ChargesModelList
import com.blackmidori.expenses.models.Expense
import com.blackmidori.expenses.models.ExpenseList
import com.blackmidori.expenses.models.Payer
import com.blackmidori.expenses.models.PayerList
import com.blackmidori.expenses.models.PayerPaymentWeight
import com.blackmidori.expenses.models.PayerPaymentWeightList
import com.blackmidori.expenses.models.Workspace
import com.blackmidori.expenses.models.WorkspaceList

fun workspaceStorage() =
    MultiPlatformStorage(
        "workspaces",
        { WorkspaceList(it) },
        { Workspace.fromMap(it) }
    )
fun payerStorage() = MultiPlatformStorage(
    "payers",
    { PayerList(it) },
    { Payer.fromMap(it) }
)
fun expenseStorage() = MultiPlatformStorage(
    "expenses",
    { ExpenseList(it) },
    { Expense.fromMap(it) }
)
fun chargesModelStorage() =
    MultiPlatformStorage(
        "chargesModels",
        { ChargesModelList(it) },
        { ChargesModel.fromMap(it) }
    )
fun chargeAssociationStorage() =
    MultiPlatformStorage(
        "chargeAssociations",
        { ChargeAssociationList(it) },
        { ChargeAssociation.fromMap(it) }
    )
fun payerPaymentWeightStorage() =
    MultiPlatformStorage(
        "payerPaymentWeights",
        { PayerPaymentWeightList(it) },
        { PayerPaymentWeight.fromMap(it) }
    )