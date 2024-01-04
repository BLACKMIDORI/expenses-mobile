package com.blackmidori.familyexpenses.android.repositories

import com.blackmidori.familyexpenses.android.Config
import com.blackmidori.familyexpenses.android.infrastructure.http.HttpClient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.OffsetDateTime

class AuthRepository(val baseUrl: String = Config.apiBaseUrl, val httpClient: HttpClient) {

    class SignInWithTokenResponseUser(val id:String)
    class SignInWithTokenResponse(val user: SignInWithTokenResponseUser, val accessTokenExpirationDateTime:Instant, val accessToken: String, val refreshToken: String)
    fun signInWithToken(idToken: String): Result<SignInWithTokenResponse> {
        val requestBody = "{\"clientId\":\"com.blackmidori.familyexpenses\",\"idToken\": \"$idToken\"}"
        val response = httpClient.post("$baseUrl/v1/auth/tokensignin", requestBody)
        if (response.status!=200){
            return Result.failure(Exception(response.body))
        }
        val apiTokens = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (apiTokens == null) {
            return Result.failure(Exception("http fetch error"))
        } else{
            return Result.success(SignInWithTokenResponse(
                SignInWithTokenResponseUser(apiTokens["appUser"]!!.jsonObject["id"]!!.jsonPrimitive.content),
                Instant.parse(apiTokens["accessTokenExpirationDateTime"]!!.jsonPrimitive.content),
                apiTokens["accessToken"]!!.jsonPrimitive.content,
                apiTokens["refreshToken"]!!.jsonPrimitive.content
            ))
        }
    }

}