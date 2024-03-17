package com.blackmidori.familyexpenses.repositories

import com.blackmidori.familyexpenses.core.EntityList
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.core.PagedList
import kotlinx.collections.immutable.PersistentList
import kotlinx.serialization.serializer

class WorkspaceRepository(
    private val store: Store<out EntityList<Workspace>>
) {
    suspend fun add(entity: Workspace): Result<Workspace> {
        return store.add { id, creationDateTime ->
            Workspace(id, creationDateTime, entity.name)
        }
    }

    suspend fun getPagedList(): Result<PagedList<Workspace>> {
        val result = store.getList();
        return result.map {
            PagedList(
                999,
                0,
                it.toTypedArray()
            )
        }
    }

    suspend fun getOne(id: String): Result<Workspace> {
        return store.getOne(id)
    }

    suspend fun update(entity: Workspace): Result<Workspace> {
        val old = getOne(entity.id).getOrNull()
        return store.update(
            old?.let {
                Workspace(it.id, it.creationDateTime, entity.name)
            } ?: entity
        )
    }

    suspend fun delete(id: String): Result<Boolean> {
        return store.delete(id)
    }

}