package com.blackmidori.familyexpenses.android.infrastructure.http

class HttpResponse(val status: Int, val body: String? = null, val headers: Map<String,String> = HashMap())