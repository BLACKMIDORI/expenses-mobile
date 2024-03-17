package com.blackmidori.familyexpenses.models

import com.blackmidori.familyexpenses.core.Entity
import com.blackmidori.familyexpenses.core.EntityList
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