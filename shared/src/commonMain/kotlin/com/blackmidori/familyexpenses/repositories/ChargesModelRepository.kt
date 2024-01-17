package com.blackmidori.familyexpenses.repositories

import com.blackmidori.familyexpenses.Config
import com.blackmidori.familyexpenses.core.http.HttpClient
import com.blackmidori.familyexpenses.core.PagedList
import com.blackmidori.familyexpenses.models.ChargesModel
import com.blackmidori.familyexpenses.services.TokensService
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ChargesModelRepository(
    val baseUrl: String = Config.apiBaseUrl,
    val httpClient: HttpClient,
    val tokensService: TokensService = TokensService(authRepository = AuthRepository(httpClient = httpClient)),
) {
    fun add(workspaceId: String, chargesModel: ChargesModel): Result<ChargesModel> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val requestBody =
            "{\"name\":\"${chargesModel.name}\",\"workspace\":{\"id\":\"${workspaceId}\"}}"
        val response = httpClient.post(
            "$baseUrl/v1/charges-models/",
            requestBody,
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            return Result.success(
                ChargesModel(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content
                )
            )
        }
    }

    fun getPagedList(workspaceId: String): Result<PagedList<ChargesModel>> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.get(
            "$baseUrl/v1/charges-models/?filter=workspace.id__$workspaceId&size=999&from=0",
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            val list = ArrayList<ChargesModel>()
            for (jsonElement in responseBody["results"]!!.jsonArray) {
                val obj = jsonElement.jsonObject
                list.add(
                    ChargesModel(
                        id = obj["id"]!!.jsonPrimitive.content,
                        creationDateTime = Instant.parse(obj["creationDateTime"]!!.jsonPrimitive.content),
                        name = obj["name"]!!.jsonPrimitive.content
                    )
                )
            }
            return Result.success(
                PagedList(
                    responseBody["size"]!!.jsonPrimitive.int,
                    responseBody["from"]!!.jsonPrimitive.int,
                    list.toTypedArray()
                )
            )
        }
    }

    fun getOne(chargesModelId: String): Result<ChargesModel> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.get(
            "$baseUrl/v1/charges-models/${chargesModelId}",
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            return Result.success(
                ChargesModel(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content
                )
            )
        }
    }

    fun update(chargesModel: ChargesModel): Result<ChargesModel> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val requestBody = "{\"name\":\"${chargesModel.name}\"}"
        val response = httpClient.put(
            "$baseUrl/v1/charges-models/${chargesModel.id}",
            requestBody,
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            return Result.success(
                ChargesModel(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content
                )
            )
        }
    }

    fun delete(chargesModel: ChargesModel): Result<Boolean> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.delete(
            "$baseUrl/v1/charges-models/${chargesModel.id}",
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            return Result.success(
                responseBody["ok"]!!.jsonPrimitive.boolean
            )
        }
    }

}