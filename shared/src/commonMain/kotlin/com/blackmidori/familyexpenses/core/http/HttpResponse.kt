package com.blackmidori.familyexpenses.core.http

class HttpResponse(val status: Int, val body: String? = null, val headers: Map<String,String> = HashMap())