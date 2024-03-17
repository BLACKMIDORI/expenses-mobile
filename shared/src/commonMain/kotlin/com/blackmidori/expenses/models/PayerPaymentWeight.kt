package com.blackmidori.expenses.models

import com.blackmidori.expenses.core.Entity
import com.blackmidori.expenses.core.EntityList
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PayerPaymentWeightList(override val list: PersistentList<PayerPaymentWeight>): EntityList<PayerPaymentWeight>({PayerPaymentWeightList(it)})
@Serializable
data class PayerPaymentWeight(
    override val id: String,
    override val creationDateTime: Instant,
    val chargeAssociationId: String,
    val weight: Double,
    val payer: Payer
): Entity