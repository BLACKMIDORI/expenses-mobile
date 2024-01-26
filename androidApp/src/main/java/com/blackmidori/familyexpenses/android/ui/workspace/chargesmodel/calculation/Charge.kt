package com.blackmidori.familyexpenses.android.ui.workspace.chargesmodel.calculation

import com.blackmidori.familyexpenses.models.Payer

class Charge(val source: Payer, val destination:MutableMap<String,MutableList<Bill>> = mutableMapOf<String,MutableList<Bill>>())
