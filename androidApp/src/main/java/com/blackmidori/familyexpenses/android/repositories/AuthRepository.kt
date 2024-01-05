package com.blackmidori.familyexpenses.android.repositories

import com.blackmidori.familyexpenses.Config
import com.blackmidori.familyexpenses.core.http.HttpClient
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AuthRepository(val baseUrl: String = Config.apiBaseUrl, val httpClient: HttpClient) {

    class TokensResponseUser(val id:String)
    class TokensResponse(val user: TokensResponseUser, val accessTokenExpirationDateTime:Instant, val accessToken: String, val refreshToken: String)
    fun signInWithToken(idToken: String): Result<TokensResponse> {
        val requestBody = "{\"clientId\":\"com.blackmidori.familyexpenses\",\"idToken\": \"$idToken\"}"
        val response = httpClient.post("$baseUrl/v1/auth/tokensignin", requestBody)
        if (response.status!=200){
            return Result.failure(Exception(response.body))
        }
        val apiTokens = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (apiTokens == null) {
            return Result.failure(Exception("http fetch error"))
        } else{
            return Result.success(
                TokensResponse(
                TokensResponseUser(apiTokens["appUser"]!!.jsonObject["id"]!!.jsonPrimitive.content),
                Instant.parse(apiTokens["accessTokenExpirationDateTime"]!!.jsonPrimitive.content),
                apiTokens["accessToken"]!!.jsonPrimitive.content,
                apiTokens["refreshToken"]!!.jsonPrimitive.content
            )
            )
        }
    }
    fun renewTokens(refreshToken: String): Result<TokensResponse> {
        val requestBody = "{\"clientId\":\"com.blackmidori.familyexpenses\",\"refreshToken\": \"$refreshToken\"}"
        val response = httpClient.post("$baseUrl/v1/auth/renew", requestBody)
        if (response.status!=200){
            return Result.failure(Exception(response.body))
        }
        val apiTokens = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (apiTokens == null) {
            return Result.failure(Exception("http fetch error"))
        } else{
            return Result.success(
                TokensResponse(
                TokensResponseUser(apiTokens["appUser"]!!.jsonObject["id"]!!.jsonPrimitive.content),
                Instant.parse(apiTokens["accessTokenExpirationDateTime"]!!.jsonPrimitive.content),
                apiTokens["accessToken"]!!.jsonPrimitive.content,
                apiTokens["refreshToken"]!!.jsonPrimitive.content
            )
            )
        }
    }

}