package com.blackmidori.familyexpenses.repositories

import com.blackmidori.familyexpenses.Config
import com.blackmidori.familyexpenses.core.http.HttpClient
import com.blackmidori.familyexpenses.core.PagedList
import com.blackmidori.familyexpenses.models.Payer
import com.blackmidori.familyexpenses.models.PayerPaymentWeight
import com.blackmidori.familyexpenses.services.TokensService
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class PayerPaymentWeightRepository(
    val baseUrl: String = Config.apiBaseUrl,
    val httpClient: HttpClient,
    val tokensService: TokensService = TokensService(authRepository = AuthRepository(httpClient = httpClient)),
) {
    fun add(
        chargeAssociationId: String,
        payerPaymentWeight: PayerPaymentWeight
    ): Result<PayerPaymentWeight> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val requestBody =
            "{\"weight\":${payerPaymentWeight.weight},\"chargeAssociation\":{\"id\":\"${chargeAssociationId}\"},\"payer\":{\"id\":\"${payerPaymentWeight.payer.id}\"}}"
        val response = httpClient.post(
            "$baseUrl/v1/payer-payment-weights/",
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
            val payerId = responseBody["payer"]!!.jsonObject["id"]!!.jsonPrimitive.content
            return Result.success(
                PayerPaymentWeight(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    weight = responseBody["weight"]!!.jsonPrimitive.double,
                    Payer(
                        payerId,
                        Instant.DISTANT_PAST,
                        payerId
                    )
                )
            )
        }
    }

    fun getPagedList(chargeAssociationId: String): Result<PagedList<PayerPaymentWeight>> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.get(
            "$baseUrl/v1/payer-payment-weights/?filter=chargeAssociation.id__$chargeAssociationId&size=999&from=0",
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            val list = ArrayList<PayerPaymentWeight>()
            for (jsonElement in responseBody["results"]!!.jsonArray) {
                val obj = jsonElement.jsonObject
                val payerId = obj["payer"]!!.jsonObject["id"]!!.jsonPrimitive.content
                list.add(
                    PayerPaymentWeight(
                        id = obj["id"]!!.jsonPrimitive.content,
                        creationDateTime = Instant.parse(obj["creationDateTime"]!!.jsonPrimitive.content),
                        weight = obj["weight"]!!.jsonPrimitive.double,
                        Payer(
                            payerId,
                            Instant.DISTANT_PAST,
                            payerId
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

    fun getOne(payerPaymentWeightId: String): Result<PayerPaymentWeight> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.get(
            "$baseUrl/v1/payer-payment-weights/${payerPaymentWeightId}",
            mapOf("Authorization" to accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            val payerId = responseBody["payer"]!!.jsonObject["id"]!!.jsonPrimitive.content
            return Result.success(
                PayerPaymentWeight(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    weight = responseBody["weight"]!!.jsonPrimitive.double,
                    Payer(
                        payerId,
                        Instant.DISTANT_PAST,
                        payerId
                    )
                )
            )
        }
    }

    fun update(payerPaymentWeight: PayerPaymentWeight): Result<PayerPaymentWeight> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val requestBody = "{\"weight\":${payerPaymentWeight.weight},\"payer\":{\"id\":\"${payerPaymentWeight.payer.id}\"}}"
        val response = httpClient.put(
            "$baseUrl/v1/payer-payment-weights/${payerPaymentWeight.id}",
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
            val payerId = responseBody["payer"]!!.jsonObject["id"]!!.jsonPrimitive.content
            return Result.success(
                PayerPaymentWeight(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    weight = responseBody["weight"]!!.jsonPrimitive.double,
                    Payer(
                        payerId,
                        Instant.DISTANT_PAST,
                        payerId
                    )
                )
            )
        }
    }

    fun delete(payerPaymentWeight: PayerPaymentWeight): Result<Boolean> {
        val result = tokensService.getUpdatedAccessToken()
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val accessToken = result.getOrThrow()

        val response = httpClient.delete(
            "$baseUrl/v1/payer-payment-weights/${payerPaymentWeight.id}",
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