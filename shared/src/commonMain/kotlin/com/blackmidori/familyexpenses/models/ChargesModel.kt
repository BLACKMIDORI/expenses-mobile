package com.blackmidori.familyexpenses.models

import com.blackmidori.familyexpenses.core.Entity
import com.blackmidori.familyexpenses.core.EntityList
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChargesModelList(override val list: PersistentList<ChargesModel>): EntityList<ChargesModel>({ChargesModelList(it)})
@Serializable
data class ChargesModel(
    override val id: String,
    override val creationDateTime: Instant,
    val workspaceId: String,
    val name: String
): Entity

