package com.blackmidori.familyexpenses.models

import com.blackmidori.familyexpenses.core.Entity
import com.blackmidori.familyexpenses.core.EntityList
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PayerList(override val list: PersistentList<Payer>): EntityList<Payer>({PayerList(it)})
@Serializable
data class Payer(
    override val id: String,
    override val creationDateTime: Instant,
    val workspaceId: String,
    val name: String
): Entity