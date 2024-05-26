package com.blackmidori.expenses.stores

import com.blackmidori.expenses.core.Entity
import com.blackmidori.expenses.core.EntityList
import com.blackmidori.expenses.repositories.Storage
import kotbase.DatabaseConfiguration
import kotbase.Database
import kotbase.Collection
import kotbase.DataSource
import kotbase.Dictionary
import kotbase.Meta
import kotbase.MutableDocument
import kotbase.QueryBuilder
import kotbase.SelectResult
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

class MultiPlatformStorage<T : Entity, TList : EntityList<T>> : Storage<TList> {
    val database: Database
    val collection: Collection
    val fromMap: (Map<String,Any?>)->T
    val transform: (PersistentList<T>) -> TList

    constructor(collectionName: String,transform: (PersistentList<T>,) -> TList,fromMap: (Map<String,Any?>)->T,) {
        this.fromMap = fromMap
        this.transform = transform
        // Get the database (and create it if it doesn't exist).
        val config = DatabaseConfiguration()
        database = Database("core_db", config)
        collection =
            database.getCollection(collectionName) ?: database.createCollection(collectionName)
    }

    override suspend fun update(data: TList) {
        val queryAll = QueryBuilder
            .select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(collection))
        for (result in queryAll.execute().allResults()) {
            collection.delete(collection.getDocument(result.getString("id")!!)!!)
        }
        for (entity in data.list) {
            collection.save(MutableDocument(entity.id).setValue("data", entity.toMap()))
        }
    }

    override suspend fun load(): TList {
        val queryAll = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.collection(collection))
        val list = mutableListOf<T>()
        val resultSet = queryAll.execute()
        for (collectionsResult in resultSet) {
            val dictionary = collectionsResult.getValue(collection.name)
            if( dictionary is Dictionary){
                for (obj in dictionary.toMap().values) {
                    if(obj is Map<*, *>){
                        @Suppress("UNCHECKED_CAST")
                        list.add(fromMap(obj as Map<String, Any?>))
                    }
                }
            }
        }
        return transform(list.toPersistentList())
    }
}