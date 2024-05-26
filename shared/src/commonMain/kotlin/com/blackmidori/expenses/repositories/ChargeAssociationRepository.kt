package com.blackmidori.expenses.repositories

import com.blackmidori.expenses.core.EntityList
import com.blackmidori.expenses.core.PagedList
import com.blackmidori.expenses.models.ChargeAssociation
import com.blackmidori.expenses.models.Expense
import com.blackmidori.expenses.models.Payer

class ChargeAssociationRepository(
    private val storage: Storage<out EntityList<ChargeAssociation>>,
    private val expenseStorage: Storage<out EntityList<Expense>>,
    private val payerStorage: Storage<out EntityList<Payer>>,
) {
    suspend fun add(
        chargesModelId: String,
        entity: ChargeAssociation
    ): Result<ChargeAssociation> {
        val expenseResult = expenseStorage.getOne(entity.expense.id)
        if (expenseResult.isFailure) {
            return expenseResult.map { throw Exception() };
        }
        val payerResult = payerStorage.getOne(entity.actualPayer.id)
        if (payerResult.isFailure) {
            return payerResult.map { throw Exception() };
        }

        return storage.add() { id, creationDateTime ->
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

    suspend fun getPagedList(
        chargesModelId: String): Result<PagedList<ChargeAssociation>> {
        val result = storage.getList();
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
                        expenseStorage.getOne(it.expense.id).getOrNull() ?: it.expense,
                        payerStorage.getOne(it.actualPayer.id).getOrNull() ?: it.actualPayer,
                    )
                }.toTypedArray()
            )
        }
    }

    suspend fun getOne(
        id: String): Result<ChargeAssociation> {
        return storage.getOne(id).map {
            ChargeAssociation(
                it.id,
                it.creationDateTime,
                it.chargesModelId,
                it.name,
                expenseStorage.getOne(it.expense.id).getOrNull() ?: it.expense,
                payerStorage.getOne(it.actualPayer.id).getOrNull() ?: it.actualPayer,
            )
        }
    }

    suspend fun update(
        entity: ChargeAssociation): Result<ChargeAssociation> {
        val old = getOne(entity.id).getOrNull()
        return storage.update(
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
                expenseStorage.getOne(it.expense.id).getOrNull() ?: it.expense,
                payerStorage.getOne(it.actualPayer.id).getOrNull() ?: it.actualPayer,
            )
        }
    }

    suspend fun delete(
        id: String): Result<Boolean> {
        return storage.delete(id)
    }

}