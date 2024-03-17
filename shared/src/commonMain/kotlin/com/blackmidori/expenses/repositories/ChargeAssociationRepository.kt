package com.blackmidori.expenses.repositories

import com.blackmidori.expenses.core.EntityList
import com.blackmidori.expenses.core.PagedList
import com.blackmidori.expenses.models.ChargeAssociation
import com.blackmidori.expenses.models.Expense
import com.blackmidori.expenses.models.Payer

class ChargeAssociationRepository(
    private val store: Store<out EntityList<ChargeAssociation>>,
    private val expenseStore: Store<out EntityList<Expense>>,
    private val payerStore: Store<out EntityList<Payer>>,
) {
    suspend fun add(
        chargesModelId: String,
        entity: ChargeAssociation
    ): Result<ChargeAssociation> {
        val expenseResult = expenseStore.getOne(entity.expense.id)
        if (expenseResult.isFailure) {
            return expenseResult.map { throw Exception() };
        }
        val payerResult = payerStore.getOne(entity.actualPayer.id)
        if (payerResult.isFailure) {
            return payerResult.map { throw Exception() };
        }

        return store.add { id, creationDateTime ->
            ChargeAssociation(
                id,
                creationDateTime,
                chargesModelId,
                entity.name,
                expenseResult.getOrThrow(),
                payerResult.getOrThrow(),
            )
        }
    }

    suspend fun getPagedList(chargesModelId: String): Result<PagedList<ChargeAssociation>> {
        val result = store.getList();
        return result.map { it ->
            PagedList(
                999,
                0,
                it.filter { it.chargesModelId == chargesModelId }.map {
                    ChargeAssociation(
                        it.id,
                        it.creationDateTime,
                        it.chargesModelId,
                        it.name,
                        expenseStore.getOne(it.expense.id).getOrNull() ?: it.expense,
                        payerStore.getOne(it.actualPayer.id).getOrNull() ?: it.actualPayer,
                    )
                }.toTypedArray()
            )
        }
    }

    suspend fun getOne(id: String): Result<ChargeAssociation> {
        return store.getOne(id).map {
            ChargeAssociation(
                it.id,
                it.creationDateTime,
                it.chargesModelId,
                it.name,
                expenseStore.getOne(it.expense.id).getOrNull() ?: it.expense,
                payerStore.getOne(it.actualPayer.id).getOrNull() ?: it.actualPayer,
            )
        }
    }

    suspend fun update(entity: ChargeAssociation): Result<ChargeAssociation> {
        val old = getOne(entity.id).getOrNull()
        return store.update(
            old?.let {
                ChargeAssociation(
                    it.id,
                    it.creationDateTime,
                    it.chargesModelId,
                    entity.name,
                    entity.expense,
                    entity.actualPayer
                )
            } ?: entity
        ).map {
            ChargeAssociation(
                it.id,
                it.creationDateTime,
                it.chargesModelId,
                it.name,
                expenseStore.getOne(it.expense.id).getOrNull() ?: it.expense,
                payerStore.getOne(it.actualPayer.id).getOrNull() ?: it.actualPayer,
            )
        }
    }

    suspend fun delete(id: String): Result<Boolean> {
        return store.delete(id)
    }

}