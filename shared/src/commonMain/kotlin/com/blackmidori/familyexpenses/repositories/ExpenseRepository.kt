package com.blackmidori.familyexpenses.repositories

import com.blackmidori.familyexpenses.core.EntityList
import com.blackmidori.familyexpenses.core.PagedList
import com.blackmidori.familyexpenses.models.Expense
import kotlinx.collections.immutable.PersistentList

class ExpenseRepository(
    private val store: Store<out EntityList<Expense>>
) {
    suspend fun add(workspaceId: String, expense: Expense): Result<Expense> {
        return store.add { id, creationDateTime ->
            Expense(
                id,
                creationDateTime,
                expense.name,
                workspaceId,
            )
        }
    }

    suspend fun getPagedList(workspaceId: String): Result<PagedList<Expense>> {
        val result = store.getList();
        return result.map { it ->
            PagedList(
                999,
                0,
                it.filter { it.workspaceId == workspaceId }.toTypedArray()
            )
        }
    }

    suspend fun getOne(id: String): Result<Expense> {
        return store.getOne(id)
    }

    suspend fun update(entity: Expense): Result<Expense> {
        val old = getOne(entity.id).getOrNull()
        return store.update(
            old?.let {
                Expense(it.id, it.creationDateTime, it.workspaceId, entity.name)
            } ?: entity
        )
    }

    suspend fun delete(id: String): Result<Boolean> {
        return store.delete(id)
    }

}