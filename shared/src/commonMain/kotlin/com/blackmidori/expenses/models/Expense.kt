package com.blackmidori.expenses.models

import com.blackmidori.expenses.core.Entity
import com.blackmidori.expenses.core.EntityList
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ExpenseList(override val list: PersistentList<Expense>): EntityList<Expense>({ExpenseList(it)})
@Serializable
data class Expense(
    override val id: String,
    override val creationDateTime: Instant,
    val workspaceId: String,
    val name: String,
): Entity{
    override fun toMap(): Map<String, Any?> {
        return mapOf(
            Pair("id", id),
            Pair("creationDateTime", creationDateTime),
            Pair("workspaceId", workspaceId),
            Pair("name", name),
        )
    }

    companion object{
        fun fromMap(map: Map<String, Any?>):Expense{
            return Expense(
                map["id"] as String,
                Instant.parse(map["creationDateTime"] as String),
                map["workspaceId"] as String,
                map["name"] as String,
            )
        }
    }
}