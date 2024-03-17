package com.blackmidori.expenses.android.ui.workspace.chargesmodel.calculation

import com.blackmidori.expenses.models.Payer

class Charge(val source: Payer, val destination:MutableMap<String,MutableList<Bill>> = mutableMapOf<String,MutableList<Bill>>())
