package com.blackmidori.expenses.android.ui.workspace.chargesmodel.calculation

import com.blackmidori.expenses.models.Payer

class Charge(val source: Payer, val destination:MutableMap<String,MutableList<Bill>> = mutableMapOf()){
    fun sum(payerId: String): Float{
        val bills = destination.getValue(payerId)
        var sum = .0f
        for (bill in bills) {
            sum += bill.amount;
        }
        return sum;
    }
}
