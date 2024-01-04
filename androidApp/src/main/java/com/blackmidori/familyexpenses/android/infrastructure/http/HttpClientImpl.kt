package com.blackmidori.familyexpenses.android.infrastructure.http

import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL


// Source: https://stackoverflow.com/a/74643791

class HttpClientImpl : HttpClient{
    override fun get(reqUrl: String?, headers: Map<String, String>): HttpResponse {
        var status: Int = 0
        var response: String? = null
        if(headers.isNotEmpty()){
            Log.e(TAG, "Headers not yet implemented")
            return HttpResponse(status = 0);
        }
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            status = conn.responseCode
            try{
                val `in`: InputStream = BufferedInputStream(conn.inputStream)
                response = convertStreamToString(`in`)
            } catch (e: IOException) {
                Log.w(TAG, "IOException: [$status] $e")
                val `in`: InputStream = BufferedInputStream(conn.errorStream)
                response = convertStreamToString(`in`)
            }
        } catch (e: MalformedURLException) {
            Log.e(TAG, "MalformedURLException: $e")
        } catch (e: ProtocolException) {
            Log.e(TAG, "ProtocolException: $e")
        } catch (e: IOException) {
            Log.e(TAG, "IOException: [$status] $e")
        } catch (e: Exception) {
            Log.e(TAG, "Exception: $e")
        }
        return HttpResponse(status = status, response);
    }

    override fun post(reqUrl: String?, body: String?, headers: Map<String, String>): HttpResponse {
        var status: Int = 0
        var response: String? = null
        if(headers.isNotEmpty()){
            Log.e(TAG, "Headers not yet implemented")
            return HttpResponse(status = 0);
        }
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            Log.w("HttpClient","Posting: "+body)
            if( body != null){
                conn.setRequestProperty("Content-Type", "application/json")
                conn.outputStream.use { os ->
                    val input: ByteArray = body.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }
            }
            status = conn.responseCode
            try{
                val `in`: InputStream = BufferedInputStream(conn.inputStream)
                response = convertStreamToString(`in`)
            } catch (e: IOException) {
                Log.w(TAG, "IOException: [$status] $e")
                val `in`: InputStream = BufferedInputStream(conn.errorStream)
                response = convertStreamToString(`in`)
            }
        } catch (e: MalformedURLException) {
            Log.e(TAG, "MalformedURLException: $e")
        } catch (e: ProtocolException) {
            Log.e(TAG, "ProtocolException: $e")
        } catch (e: IOException) {
            Log.e(TAG, "IOException: [$status] $e")
        } catch (e: Exception) {
            Log.e(TAG, "Exception: $e")
        }
        return HttpResponse(status = status, response);
    }

    override fun put(reqUrl: String?, body: String?, headers: Map<String, String>): HttpResponse {
        TODO("Not yet implemented")
    }

    override fun delete(reqUrl: String?, headers: Map<String, String>): HttpResponse {
        TODO("Not yet implemented")
    }

    private fun convertStreamToString(`is`: InputStream): String {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    companion object {
        private val TAG = HttpClientImpl::class.java.simpleName
    }
}