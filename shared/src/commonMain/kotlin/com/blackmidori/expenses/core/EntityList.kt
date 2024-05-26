package com.blackmidori.expenses.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
abstract  class EntityList<T: Entity>(val new: (PersistentList<T>)->EntityList<T> ){
    abstract val list: PersistentList<T>
    fun find(predicate: (T) -> Boolean) = list.find(predicate)
    fun add(element: @UnsafeVariance T) = new(list.add(element))
    fun remove(element: @UnsafeVariance T) = new(list.remove(element))
    fun removeAll(predicate: (T) -> Boolean) = new(list.removeAll(predicate))
}
inline fun <T:Entity, R : Comparable<R>>  EntityList<T>.sortedByDescending(crossinline selector: (T) -> R?) = list.sortedByDescending(selector)
interface Entity{
    val id: String
    val creationDateTime: Instant

    fun toMap(): Map<String, Any?>
}