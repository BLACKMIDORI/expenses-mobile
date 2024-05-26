package com.blackmidori.expenses.repositories

import com.blackmidori.expenses.core.Entity
import com.blackmidori.expenses.core.EntityList
import com.blackmidori.expenses.core.sortedByDescending
import com.blackmidori.expenses.utils.randomUUID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface Storage<T> {

    suspend fun update(data: T);
    suspend fun load(): T


}

suspend fun <TList : EntityList<T>, T : Entity> Storage<TList>.add(constructor: (id: String, creationDateTime: Instant) -> T): Result<T> {
    val obj = constructor(
        randomUUID(),
        Clock.System.now(),
    )
    val list = load()
    if (list.find { it.id == obj.id } != null) {
        return Result.failure(Exception("Id Conflic"))
    }
    @Suppress("UNCHECKED_CAST")
    update(list.add(obj) as TList)
    return Result.success(obj)
}

suspend inline fun <TList : EntityList<T>, T : Entity> Storage<TList>.getList(): Result<List<T>>  {
    return Result.success(load().sortedByDescending { it.creationDateTime })
}

suspend fun <TList : EntityList<T>, T : Entity> Storage<TList>.getOne(id: String): Result<T> {
    val list = load()
    val foundObj = list.find { it.id == id }
        ?: return Result.failure(Exception("Not Found"))
    return Result.success(foundObj)
}

suspend fun <TList : EntityList<T>, T : Entity> Storage<TList>.update(newObj: T): Result<T>  {
    val list = load()
    val foundObj = list.find { it.id == newObj.id }
        ?: return Result.failure(Exception("Not Found"))

    @Suppress("UNCHECKED_CAST")
    update(list.remove(foundObj).add(newObj) as TList)
    return Result.success(newObj)
}


suspend fun <TList : EntityList<T>, T : Entity> Storage<TList>.delete(id: String): Result<Boolean> {
    val list = load()
    val foundObj = list.find { it.id == id }
        ?: return Result.failure(Exception("Not Found"))
    @Suppress("UNCHECKED_CAST")
    update(list.removeAll { it.id == foundObj.id } as TList)
    return Result.success(true)
}