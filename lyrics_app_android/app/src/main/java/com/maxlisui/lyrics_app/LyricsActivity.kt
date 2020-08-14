package com.maxlisui.lyrics_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LyricsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)
        val token = intent.getStringExtra(ACCESS_TOKEN)
        val helper = token?.let { SpotifyHelper(it, getString(R.string.spotify_base_url)) }
        helper?.getCurrentSong()
    }
}