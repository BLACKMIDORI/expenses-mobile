package com.blackmidori.expenses.repositories

import com.blackmidori.expenses.core.EntityList
import com.blackmidori.expenses.core.PagedList
import com.blackmidori.expenses.models.Payer
import com.blackmidori.expenses.models.PayerPaymentWeight

class PayerPaymentWeightRepository(
    private val storage: Storage<out EntityList<PayerPaymentWeight>>,
    private val payerStorage: Storage<out EntityList<Payer>>,
) {
    suspend fun add(
                    chargeAssociationId: String,
                    entity: PayerPaymentWeight
    ): Result<PayerPaymentWeight> {
        val payerResult = payerStorage.getOne(entity.payer.id)
        if (payerResult.isFailure) {
            return payerResult.map { throw Exception() };
        }
        return storage.add() { id, creationDateTime ->
            PayerPaymentWeight(
                id,
                creationDateTime,
                chargeAssociationId,
                entity.weight,
                payerResult.getOrThrow(),
            )
        }
    }

    suspend fun getPagedList(chargeAssociationId: String): Result<PagedList<PayerPaymentWeight>> {
        val result = storage.getList();
        return result.map { it ->
            PagedList(
                999,
                0,
                it.filter { it.chargeAssociationId == chargeAssociationId }.toTypedArray()
            )
        }
    }

    suspend fun getOne(id: String): Result<PayerPaymentWeight> {
        return storage.getOne(id)
    }

    suspend fun update(entity: PayerPaymentWeight): Result<PayerPaymentWeight> {
        val old = getOne(entity.id).getOrNull()
        return storage.update(
            old?.let {
                PayerPaymentWeight(
                    it.id,
                    it.creationDateTime,
                    it.chargeAssociationId,
                    entity.weight,
                    entity.payer
                )
            } ?: entity
        ).map {
            PayerPaymentWeight(
                it.id,
                it.creationDateTime,
                it.chargeAssociationId,
                it.weight,
                payerStorage.getOne(it.payer.id).getOrNull() ?: it.payer,
            )
        }
    }

    suspend fun delete(id: String): Result<Boolean> {
        return storage.delete(id)
    }

}