package com.blackmidori.expenses.stores

import android.content.Context
import com.blackmidori.expenses.AndroidDataStore
import com.blackmidori.expenses.models.ChargeAssociationList
import com.blackmidori.expenses.models.ChargesModelList
import com.blackmidori.expenses.models.ExpenseList
import com.blackmidori.expenses.models.PayerList
import com.blackmidori.expenses.models.PayerPaymentWeightList
import com.blackmidori.expenses.models.WorkspaceList

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