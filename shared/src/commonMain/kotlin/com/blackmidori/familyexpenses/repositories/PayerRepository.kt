package com.blackmidori.familyexpenses.repositories

import com.blackmidori.familyexpenses.Config
import com.blackmidori.familyexpenses.core.http.HttpClient
import com.blackmidori.familyexpenses.core.PagedList
import com.blackmidori.familyexpenses.models.Payer
import com.blackmidori.familyexpenses.services.TokensService
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class PayerRepository(
    val baseUrl: String = Config.apiBaseUrl,
    val httpClient: HttpClient,
    val tokensService: TokensService = TokensService(authRepository = AuthRepository(httpClient = httpClient)),
) {
    fun add(workspaceId: String, payer: Payer): Result<Payer> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val requestBody =
            "{\"name\":\"${payer.name}\",\"workspace\":{\"id\":\"${workspaceId}\"}}"
        val response = httpClient.post(
            "$baseUrl/v1/payers/",
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
                Payer(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content
                )
            )
        }
    }

    fun getPagedList(workspaceId: String): Result<PagedList<Payer>> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.get(
            "$baseUrl/v1/payers/?filter=workspace.id__$workspaceId&size=999&from=0",
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            val list = ArrayList<Payer>()
            for (jsonElement in responseBody["results"]!!.jsonArray) {
                val obj = jsonElement.jsonObject
                list.add(
                    Payer(
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

    fun getOne(payerId: String): Result<Payer> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.get(
            "$baseUrl/v1/payers/${payerId}",
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
                Payer(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content
                )
            )
        }
    }

    fun update(payer: Payer): Result<Payer> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val requestBody = "{\"name\":\"${payer.name}\"}"
        val response = httpClient.put(
            "$baseUrl/v1/payers/${payer.id}",
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
                Payer(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content
                )
            )
        }
    }

    fun delete(payer: Payer): Result<Boolean> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.delete(
            "$baseUrl/v1/payers/${payer.id}",
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