package com.blackmidori.familyexpenses.models

import com.blackmidori.familyexpenses.core.Entity
import com.blackmidori.familyexpenses.core.EntityList
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