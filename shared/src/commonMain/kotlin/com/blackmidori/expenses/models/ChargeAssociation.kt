package com.blackmidori.expenses.models

import com.blackmidori.expenses.core.Entity
import com.blackmidori.expenses.core.EntityList
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChargeAssociationList(override val list: PersistentList<ChargeAssociation>) :
    EntityList<ChargeAssociation>({ ChargeAssociationList(it) })

@Serializable
data class ChargeAssociation(
    override val id: String,
    override val creationDateTime: Instant,
    val chargesModelId: String,
    val name: String,
    val expense: Expense,
    val actualPayer: Payer
) : Entity {
    override fun toMap(): Map<String, Any?> {
        return mapOf(
            Pair("id", id),
            Pair("creationDateTime", creationDateTime),
            Pair("chargesModelId", chargesModelId),
            Pair("name", name),
            Pair("expense", expense.toMap()),
            Pair("actualPayer", actualPayer.toMap())
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): ChargeAssociation {
            return ChargeAssociation(
                map["id"] as String,
                Instant.parse(map["creationDateTime"] as String),
                map["chargesModelId"] as String,
                map["name"] as String,
                @Suppress("UNCHECKED_CAST")
                Expense.fromMap(map["expense"] as Map<String, Any?>),
                @Suppress("UNCHECKED_CAST")
                Payer.fromMap(map["actualPayer"] as Map<String, Any?>)
            )
        }
    }
}