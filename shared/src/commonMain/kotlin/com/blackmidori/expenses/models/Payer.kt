package com.blackmidori.expenses.models

import com.blackmidori.expenses.core.Entity
import com.blackmidori.expenses.core.EntityList
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