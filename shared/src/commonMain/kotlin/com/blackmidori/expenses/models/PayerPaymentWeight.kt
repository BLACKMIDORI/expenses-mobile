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
    val weight: Float,
    val payer: Payer
): Entity {
    override fun toMap(): Map<String, Any?> {
        return mapOf(
            Pair("id", id),
            Pair("creationDateTime", creationDateTime),
            Pair("chargeAssociationId", chargeAssociationId),
            Pair("weight", weight),
            Pair("payer", payer.toMap())
        )
    }

    companion object{
        fun fromMap(map: Map<String, Any?>):PayerPaymentWeight{
            return PayerPaymentWeight(
                map["id"] as String,
                Instant.parse(map["creationDateTime"] as String),
                map["chargeAssociationId"] as String,
                map["weight"] as Float,
                @Suppress("UNCHECKED_CAST")
                Payer.fromMap(map["payer"] as Map<String, Any?>)
            )
        }
    }
}