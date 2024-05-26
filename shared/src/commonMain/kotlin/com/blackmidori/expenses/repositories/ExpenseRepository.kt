package com.blackmidori.expenses.repositories

import com.blackmidori.expenses.core.EntityList
import com.blackmidori.expenses.core.PagedList
import com.blackmidori.expenses.models.Expense

class ExpenseRepository(
    private val storage: Storage<out EntityList<Expense>>
) {
    suspend fun add(workspaceId: String, expense: Expense): Result<Expense> {
        return storage.add() { id, creationDateTime ->
            Expense(
                id,
                creationDateTime,
                workspaceId,
                expense.name,
            )
        }
    }

    suspend fun getPagedList(workspaceId: String): Result<PagedList<Expense>> {
        val result = storage.getList();
        return result.map { it ->
            PagedList(
                999,
                0,
                it.filter { it.workspaceId == workspaceId }.toTypedArray()
            )
        }
    }

    suspend fun getOne(id: String): Result<Expense> {
        return storage.getOne(id)
    }

    suspend fun update(entity: Expense): Result<Expense> {
        val old = getOne(entity.id).getOrNull()
        return storage.update(
            old?.let {
                Expense(it.id, it.creationDateTime, it.workspaceId, entity.name)
            } ?: entity
        )
    }

    suspend fun delete(id: String): Result<Boolean> {
        return storage.delete(id)
    }

}