package com.blackmidori.familyexpenses.stores

import android.content.Context
import com.blackmidori.familyexpenses.AndroidDataStore
import com.blackmidori.familyexpenses.core.Entity
import com.blackmidori.familyexpenses.models.ChargeAssociation
import com.blackmidori.familyexpenses.models.ChargeAssociationList
import com.blackmidori.familyexpenses.models.ChargesModel
import com.blackmidori.familyexpenses.models.ChargesModelList
import com.blackmidori.familyexpenses.models.Expense
import com.blackmidori.familyexpenses.models.ExpenseList
import com.blackmidori.familyexpenses.models.Payer
import com.blackmidori.familyexpenses.models.PayerList
import com.blackmidori.familyexpenses.models.PayerPaymentWeight
import com.blackmidori.familyexpenses.models.PayerPaymentWeightList
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.models.WorkspaceList
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

fun workspaceStore(context: Context) =
    AndroidDataStore(context, "workspaces.json", DataStoreJsonSerializer (WorkspaceList.serializer()))
fun chargesModelStore(context: Context) =
    AndroidDataStore(context, "chargesModels.json", DataStoreJsonSerializer (ChargesModelList.serializer()))
fun payerStore(context: Context) =
    AndroidDataStore(context, "payers.json", DataStoreJsonSerializer (PayerList.serializer()))
fun expenseStore(context: Context) =
    AndroidDataStore(context, "expenses.json", DataStoreJsonSerializer (ExpenseList.serializer()))
fun chargeAssociationStore(context: Context) =
    AndroidDataStore(context, "chargeAssociations.json", DataStoreJsonSerializer (
        ChargeAssociationList.serializer()))
fun payerPaymentWeightStore(context: Context) =
    AndroidDataStore(context, "payerPaymentWeights.json", DataStoreJsonSerializer (
        PayerPaymentWeightList.serializer()))