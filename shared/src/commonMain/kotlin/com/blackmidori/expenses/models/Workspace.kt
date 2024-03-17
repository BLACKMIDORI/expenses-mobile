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
) : Entity