package com.blackmidori.familyexpenses.utils

import com.blackmidori.familyexpenses.repositories.ChargeAssociationRepository
import com.blackmidori.familyexpenses.repositories.ChargesModelRepository
import com.blackmidori.familyexpenses.repositories.ExpenseRepository
import com.blackmidori.familyexpenses.repositories.PayerPaymentWeightRepository
import com.blackmidori.familyexpenses.repositories.PayerRepository
import com.blackmidori.familyexpenses.repositories.WorkspaceRepository

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