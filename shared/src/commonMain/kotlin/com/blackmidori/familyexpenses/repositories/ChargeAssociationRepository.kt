package com.blackmidori.familyexpenses.repositories

import com.blackmidori.familyexpenses.Config
import com.blackmidori.familyexpenses.core.http.HttpClient
import com.blackmidori.familyexpenses.core.PagedList
import com.blackmidori.familyexpenses.models.ChargeAssociation
import com.blackmidori.familyexpenses.models.Expense
import com.blackmidori.familyexpenses.services.TokensService
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ChargeAssociationRepository(
    val baseUrl: String = Config.apiBaseUrl,
    val httpClient: HttpClient,
    val tokensService: TokensService = TokensService(authRepository = AuthRepository(httpClient = httpClient)),
) {
    fun add(
        chargesModelId: String,
        chargeAssociation: ChargeAssociation
    ): Result<ChargeAssociation> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val requestBody =
            "{\"name\":\"${chargeAssociation.name}\",\"chargesModel\":{\"id\":\"${chargesModelId}\"},\"expense\":{\"id\":\"${chargeAssociation.expense.id}\"}}"
        val response = httpClient.post(
            "$baseUrl/v1/charge-associations/",
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
            val expenseId = responseBody["expense"]!!.jsonObject["id"]!!.jsonPrimitive.content
            return Result.success(
                ChargeAssociation(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content,
                    Expense(
                        expenseId,
                        Instant.DISTANT_PAST,
                        expenseId
                    )
                )
            )
        }
    }

    fun getPagedList(chargesModelId: String): Result<PagedList<ChargeAssociation>> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.get(
            "$baseUrl/v1/charge-associations/?filter=chargesModel.id__$chargesModelId&size=999&from=0",
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            val list = ArrayList<ChargeAssociation>()
            for (jsonElement in responseBody["results"]!!.jsonArray) {
                val obj = jsonElement.jsonObject
                val expenseId = obj["expense"]!!.jsonObject["id"]!!.jsonPrimitive.content
                list.add(
                    ChargeAssociation(
                        id = obj["id"]!!.jsonPrimitive.content,
                        creationDateTime = Instant.parse(obj["creationDateTime"]!!.jsonPrimitive.content),
                        name = obj["name"]!!.jsonPrimitive.content,
                        Expense(
                            expenseId,
                            Instant.DISTANT_PAST,
                            expenseId
                        )
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

    fun getOne(chargeAssociationId: String): Result<ChargeAssociation> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.get(
            "$baseUrl/v1/charge-associations/${chargeAssociationId}",
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            val expenseId = responseBody["expense"]!!.jsonObject["id"]!!.jsonPrimitive.content
            return Result.success(
                ChargeAssociation(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content,
                    Expense(
                        expenseId,
                        Instant.DISTANT_PAST,
                        expenseId
                    )
                )
            )
        }
    }

    fun update(chargeAssociation: ChargeAssociation): Result<ChargeAssociation> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val requestBody = "{\"name\":\"${chargeAssociation.name}\",\"expense\":{\"id\":\"${chargeAssociation.expense.id}\"}}"
        val response = httpClient.put(
            "$baseUrl/v1/charge-associations/${chargeAssociation.id}",
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
            val expenseId = responseBody["expense"]!!.jsonObject["id"]!!.jsonPrimitive.content
            return Result.success(
                ChargeAssociation(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content,
                    Expense(
                        expenseId,
                        Instant.DISTANT_PAST,
                        expenseId
                    )
                )
            )
        }
    }

    fun delete(chargeAssociation: ChargeAssociation): Result<Boolean> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.delete(
            "$baseUrl/v1/charge-associations/${chargeAssociation.id}",
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