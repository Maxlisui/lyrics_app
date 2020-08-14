package com.maxlisui.lyrics_app.helper

import android.util.Log
import com.maxlisui.lyrics_app.SPOTIFY_HELPER_LOG_HELPER
import com.maxlisui.lyrics_app.SpotifySongToVoidAction
import com.maxlisui.lyrics_app.model.SpotifyAlbum
import com.maxlisui.lyrics_app.model.SpotifyArtist
import com.maxlisui.lyrics_app.model.SpotifySong
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class SpotifyHelper(private val accessToken: String, private val baseUrl: String, private val onNewSong: SpotifySongToVoidAction) {
    private val client: OkHttpClient = OkHttpClient()

    fun getCurrentSong() {
        val request = prepareRequest("me/player").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                if(response.body != null) {
                    try {
                        val s = parseSpotifySong(JSONObject(response.body!!.string()))
                        onNewSong(s)
                    } catch (ex: Exception) {
                        Log.e(SPOTIFY_HELPER_LOG_HELPER, "Error while parsing JSON\n" + response.body, ex)
                    }

                }
            }
        })
    }

    private fun prepareRequest(path: String): Request.Builder {
        return Request.Builder()
            .url(baseUrl + path)
            .addHeader("Authorization", "Bearer $accessToken")
    }

    private fun parseSpotifySong(json: JSONObject): SpotifySong {
        val s = SpotifySong()
        s.isPlaying = json.optBoolean("is_playing", false)
        s.currentlyPlayingType = json.optString("currently_playing_type")

        val songJson = json.optJSONObject("item")
        if(songJson != null) {
            s.name = songJson.optString("name")

            val jsonAlbum = songJson.optJSONObject("album")
            if(jsonAlbum != null) {
                s.album.name = jsonAlbum.optString("name")
                s.album.releaseDate = SimpleDateFormat("yyyy-MM-dd").parse(jsonAlbum.optString("release_date")).toInstant()
            }

            val artistsArray = songJson.optJSONArray("artists")
            if(artistsArray != null) {
                for (i in 0 until artistsArray.length()) {
                    val a = SpotifyArtist()
                    a.name = artistsArray.getJSONObject(i).optString("name")
                    s.artists.add(a)
                }
            }
        }
        return s
    }
}