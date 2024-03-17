package com.blackmidori.expenses.utils

import com.blackmidori.expenses.repositories.ChargeAssociationRepository
import com.blackmidori.expenses.repositories.ChargesModelRepository
import com.blackmidori.expenses.repositories.ExpenseRepository
import com.blackmidori.expenses.repositories.PayerPaymentWeightRepository
import com.blackmidori.expenses.repositories.PayerRepository
import com.blackmidori.expenses.repositories.WorkspaceRepository

class SampleGenerator(
    private val workspaceRepository: WorkspaceRepository,
    private val chargesModelRepository: ChargesModelRepository,
    private val expenseRepository: PayerRepository,
    private val payerRepository: ExpenseRepository,
    private val chargeAssociationRepository: ChargeAssociationRepository,
    private val payerPaymentWeightRepository: PayerPaymentWeightRepository,
) {

    fun generateSample(){

    }
}