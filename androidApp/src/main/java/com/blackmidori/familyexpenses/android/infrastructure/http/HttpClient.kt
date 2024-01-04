package com.blackmidori.familyexpenses.android.infrastructure.http

interface HttpClient {
    fun get(reqUrl: String?, headers: Map<String, String> = HashMap()): HttpResponse
    fun post(reqUrl: String?, body: String? = null, headers: Map<String, String> = HashMap()): HttpResponse
    fun put(reqUrl: String?, body: String? = null, headers: Map<String, String> = HashMap()): HttpResponse
    fun delete(reqUrl: String?, headers: Map<String, String> = HashMap()): HttpResponse
}