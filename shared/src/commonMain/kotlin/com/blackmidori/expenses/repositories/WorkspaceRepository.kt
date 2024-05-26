package com.blackmidori.expenses.repositories

import com.blackmidori.expenses.core.EntityList
import com.blackmidori.expenses.models.Workspace
import com.blackmidori.expenses.core.PagedList

class WorkspaceRepository(
    private val storage: Storage<out EntityList<Workspace>>
) {
    suspend fun add(entity: Workspace): Result<Workspace> {
        return storage.add() { id, creationDateTime ->
            Workspace(id, creationDateTime, entity.name)
        }
    }

    suspend fun getPagedList(): Result<PagedList<Workspace>> {
        val result = storage.getList();
        return result.map {
            PagedList(
                999,
                0,
                it.toTypedArray()
            )
        }
    }

    suspend fun getOne(id: String): Result<Workspace> {
        return storage.getOne(id)
    }

    suspend fun update(entity: Workspace): Result<Workspace> {
        val old = getOne(entity.id).getOrNull()
        return storage.update(
            old?.let {
                Workspace(it.id, it.creationDateTime, entity.name)
            } ?: entity
        )
    }

    suspend fun delete(id: String): Result<Boolean> {
        return storage.delete(id)
    }

}