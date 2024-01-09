package com.blackmidori.familyexpenses.android.core

import android.util.Log
import com.blackmidori.familyexpenses.core.http.HttpClient
import com.blackmidori.familyexpenses.core.http.HttpResponse
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

class HttpClientJavaImpl : HttpClient {
    override fun get(reqUrl: String?, headers: Map<String, String>): HttpResponse {
        Log.i(TAG,"GET: $reqUrl")
        var status: Int = 0
        var response: String? = null
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            for (header in headers) {
                conn.setRequestProperty(header.key,header.value)
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

    override fun post(reqUrl: String?, body: String?, headers: Map<String, String>): HttpResponse {
        Log.i(TAG,"POST: $reqUrl")
        var status: Int = 0
        var response: String? = null
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            for (header in headers) {
                conn.setRequestProperty(header.key,header.value)
            }
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
        Log.i(TAG,"PUT: $reqUrl")
        var status: Int = 0
        var response: String? = null
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "PUT"
            for (header in headers) {
                conn.setRequestProperty(header.key,header.value)
            }
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

    override fun delete(reqUrl: String?, headers: Map<String, String>): HttpResponse {
        Log.i(TAG,"DELETE: $reqUrl")
        var status: Int = 0
        var response: String? = null
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "DELETE"
            for (header in headers) {
                conn.setRequestProperty(header.key,header.value)
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
        private val TAG = HttpClientJavaImpl::class.java.simpleName
    }
}