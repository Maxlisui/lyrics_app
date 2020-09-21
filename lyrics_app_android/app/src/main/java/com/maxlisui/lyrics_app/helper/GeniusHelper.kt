package com.maxlisui.lyrics_app.helper

import android.util.Log
import com.maxlisui.lyrics_app.GENIUS_HELPER_LOG_HELPER
import com.maxlisui.lyrics_app.StringToVoidAction
import okhttp3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class GeniusHelper(private val baseUrl: String, private val accessToken: String, private val onNewLyrics: StringToVoidAction) {
    private val client: OkHttpClient = OkHttpClient()
    private val tagRegex = Regex("(<.*?>)")
    private val newLineRegex = Regex("(<br />|<br/>|<br>)")
    private val tooMuchNewLineRegex = Regex("\\s+\\n\\n")

    fun getSongLyrics(songName: String, artistName: String) {
        var path = "search?q="
        try {
            path += URLEncoder.encode("$songName $artistName", StandardCharsets.UTF_8.toString())
        } catch (ex: Exception) {
            Log.e(GENIUS_HELPER_LOG_HELPER, "Error while converting to url", ex)
        }

        val request = prepareRequest(path).build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if(response.body != null) {
                    val lyrics = getLyricsFromResponse(JSONObject(response.body!!.string()), songName, artistName)
                    onNewLyrics(lyrics)
                }
            }

        })
    }

    private fun getLyricsFromResponse(json: JSONObject, songTitle: String, artist: String): String {
        val responseJson = json.optJSONObject("response") ?: return ""
        val hits = responseJson.optJSONArray("hits") ?: return ""

        for (i in 0 until hits.length()) {
            val hit = hits.getJSONObject(i) ?: continue

            if(hit.optString("type") == "song") {
                val result = hit.optJSONObject("result") ?: continue
                val primaryArtist = result.optJSONObject("primary_artist") ?: continue
                val url = result.optString("url") ?: continue

                if(url.isEmpty()) continue

                if(result.optString("title").toLowerCase(Locale.ROOT) == songTitle.toLowerCase(Locale.ROOT)
                    && primaryArtist.optString("name").toLowerCase(Locale.ROOT) == artist.toLowerCase(Locale.ROOT)
                ) {
                    val doc = Jsoup.connect(url).get()

                    var lyricsElement = doc.getElementById("lyrics")

                    if(lyricsElement == null) {
                        val el = doc.getElementsByClass("lyrics")
                        if(el != null && el.count() > 0) {
                            lyricsElement = el[0]
                        }
                    } else {
                        lyricsElement = lyricsElement.nextElementSibling()
                        lyricsElement = lyricsElement.nextElementSibling()
                    }

                    if(lyricsElement == null) {
                        continue
                    }

                    val lyrics = lyricsElement.html().replace(newLineRegex, "\n").replace(tagRegex, "").replace(tooMuchNewLineRegex, "").trim()
                    if(lyrics.isNotEmpty()) return lyrics
                }
            }
        }
        return ""
    }

    private fun prepareRequest(path: String): Request.Builder {
        return Request.Builder()
            .url(baseUrl + path)
            .addHeader("Authorization", "Bearer $accessToken")
    }
}