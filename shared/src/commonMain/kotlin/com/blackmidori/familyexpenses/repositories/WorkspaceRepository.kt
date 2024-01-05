package com.blackmidori.familyexpenses.repositories

import com.blackmidori.familyexpenses.Config
import com.blackmidori.familyexpenses.Session
import com.blackmidori.familyexpenses.core.http.HttpClient
import com.blackmidori.familyexpenses.models.Workspace
import com.blackmidori.familyexpenses.core.PagedList
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class WorkspaceRepository(val baseUrl: String = Config.apiBaseUrl, val httpClient: HttpClient) {
    fun add(workspace: Workspace): Result<Workspace> {
        val requestBody = "{\"name\":\"${workspace.name}\"}"
        val response = httpClient.post(
            "$baseUrl/v1/workspaces/",
            requestBody,
            mapOf("Authorization" to Session.appUser.tokens.accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            return Result.success(
                Workspace(
                    id = responseBody["id"]!!.jsonPrimitive.content,
                    creationDateTime = Instant.parse(responseBody["creationDateTime"]!!.jsonPrimitive.content),
                    name = responseBody["name"]!!.jsonPrimitive.content
                )
            )
        }
    }

    fun getPagedList(): Result<PagedList<Workspace>> {
        val response = httpClient.get(
            "$baseUrl/v1/workspaces/",
            mapOf("Authorization" to Session.appUser.tokens.accessToken),
        )
        if (response.status != 200) {
            return Result.failure(Exception(response.body))
        }
        val responseBody = response.body?.let { Json.decodeFromString<JsonObject>(it) }

        if (responseBody == null) {
            return Result.failure(Exception("http fetch error"))
        } else {
            val list = ArrayList<Workspace>()
            for (jsonElement in responseBody["results"]!!.jsonArray) {
                val obj = jsonElement.jsonObject
                list.add(
                    Workspace(
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

}