package com.blackmidori.expenses.repositories

import com.blackmidori.expenses.core.EntityList
import com.blackmidori.expenses.core.PagedList
import com.blackmidori.expenses.models.Payer

class PayerRepository(
    private val store: Store<out EntityList<Payer>>
) {
    suspend fun add(workspaceId: String, entity: Payer): Result<Payer> {
        return store.add { id, creationDateTime ->
            Payer(id, creationDateTime,workspaceId, entity.name)
        }
    }

    suspend fun getPagedList(workspaceId: String): Result<PagedList<Payer>> {
        val result = store.getList();
        return result.map { it ->
            PagedList(
                999,
                0,
                it.filter { it.workspaceId == workspaceId }.toTypedArray()
            )
        }
    }

    suspend fun getOne(id: String): Result<Payer> {
        return store.getOne(id)
    }

    suspend fun update(entity: Payer): Result<Payer> {
        val old = getOne(entity.id).getOrNull()
        return store.update(
            old?.let {
                Payer(it.id, it.creationDateTime,it.workspaceId, entity.workspaceId,)
            } ?: entity
        )
    }

    suspend fun delete(id: String): Result<Boolean> {
        return store.delete(id)
    }
}