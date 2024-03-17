package com.blackmidori.familyexpenses.models

import com.blackmidori.familyexpenses.core.Entity
import com.blackmidori.familyexpenses.core.EntityList
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChargeAssociationList(override val list: PersistentList<ChargeAssociation>): EntityList<ChargeAssociation>({ChargeAssociationList(it)})
@Serializable
data class ChargeAssociation(
    override val id: String,
    override val creationDateTime: Instant,
    val chargesModelId: String,
    val name: String,
    val expense: Expense,
    val actualPayer: Payer
): Entity