package com.blackmidori.expenses.models

import com.blackmidori.expenses.core.Entity
import com.blackmidori.expenses.core.EntityList
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class WorkspaceList(override val list: PersistentList<Workspace>) : EntityList<Workspace>({WorkspaceList(it)})

@Serializable
data class Workspace(
    override val id: String,
    override val creationDateTime: Instant,
    val name: String
) : Entity{
    override fun toMap(): Map<String, Any?> {
        return mapOf(
            Pair("id", id),
            Pair("creationDateTime", creationDateTime),
            Pair("name", name),
        )
    }

    companion object{
        fun fromMap(map: Map<String, Any?>):Workspace{
            return Workspace(
                map["id"] as String,
                Instant.parse(map["creationDateTime"] as String),
                map["name"] as String
            )
        }
    }
}