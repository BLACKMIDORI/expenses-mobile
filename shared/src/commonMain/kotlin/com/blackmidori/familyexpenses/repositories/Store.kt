package com.blackmidori.familyexpenses.repositories

import com.blackmidori.familyexpenses.core.Entity
import com.blackmidori.familyexpenses.core.EntityList
import com.blackmidori.familyexpenses.core.sortedByDescending
import com.blackmidori.familyexpenses.models.WorkspaceList
import com.blackmidori.familyexpenses.utils.randomUUID
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface Store<T> {

    suspend fun update(data: T);
    suspend fun load(): T


}

suspend fun <TList : EntityList<T>, T : Entity> Store<TList>.add(constructor: (id: String, creationDateTime: Instant) -> T): Result<T> {
    val obj = constructor(
        randomUUID(),
        Clock.System.now(),
    )
    val list = load()
    if (list.find { it.id == obj.id } != null) {
        return Result.failure(Exception("Id Conflic"))
    }
    return Result.success(obj)
}

suspend inline fun <TList : EntityList<T>, T : Entity> Store<TList>.getList(): Result<List<T>>  {
    return Result.success(load().sortedByDescending { it.creationDateTime })
}

suspend fun <TList : EntityList<T>, T : Entity> Store<TList>.getOne(id: String): Result<T> {
    val list = load()
    val foundObj = list.find { it.id == id }
        ?: return Result.failure(Exception("Not Found"))
    return Result.success(foundObj)
}

suspend fun <TList : EntityList<T>, T : Entity> Store<TList>.update(newObj: T): Result<T>  {
    val list = load()
    val foundObj = list.find { it.id == newObj.id }
        ?: return Result.failure(Exception("Not Found"))

    update(list.remove(foundObj).add(newObj) as TList)
    return Result.success(newObj)
}


suspend fun <TList : EntityList<T>, T : Entity> Store<TList>.delete(id: String): Result<Boolean> {
    val list = load()
    val foundObj = list.find { it.id == id }
        ?: return Result.failure(Exception("Not Found"))
    update(list.removeAll { it.id == foundObj.id } as TList)
    return Result.success(true)
}