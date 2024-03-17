package com.blackmidori.familyexpenses

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.blackmidori.familyexpenses.repositories.Store
import kotlinx.coroutines.flow.last

class AndroidDataStore<T>(val context: Context, dataStoreFile: String, serializer: Serializer<T>): Store<T> {
    private val Context.dataStore by dataStore(dataStoreFile, serializer)

    override suspend fun update(data: T){
        context.dataStore.updateData { data };
    }

    override suspend fun load():T{
        return context.dataStore.data.last()
    }
}