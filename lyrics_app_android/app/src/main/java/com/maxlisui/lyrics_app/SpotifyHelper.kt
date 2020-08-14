package com.maxlisui.lyrics_app

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SpotifyHelper(private val accessToken: String, private val baseUrl: String) {
    private val client: OkHttpClient = OkHttpClient()

    fun getCurrentSong() {
        val request = prepareRequest("me/player").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                if(response.body != null) {
                    val j = JSONObject(response.body!!.string())
                    val s = j.toString()
                }
            }
        })
    }

    private fun prepareRequest(path: String): Request.Builder {
        return Request.Builder()
            .url(baseUrl + path)
            .addHeader("Authorization", "Bearer $accessToken")
    }
}