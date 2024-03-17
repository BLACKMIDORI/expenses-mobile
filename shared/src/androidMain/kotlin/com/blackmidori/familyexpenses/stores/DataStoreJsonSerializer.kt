package com.blackmidori.familyexpenses.stores

import androidx.datastore.core.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class DataStoreJsonSerializer<T>(private val serializer: KSerializer<T>) : Serializer<T> {
    override val defaultValue: T get() = throw Exception("There is not default")
    override suspend fun readFrom(input: InputStream): T {
        return try {
            Json.decodeFromString(
                serializer,
                input.readBytes().toString(),
            )
        }catch (e: SerializationException){
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(obj: T, output: OutputStream) {
        output.write(
            Json.encodeToString(serializer,obj).encodeToByteArray()
        )
    }

}