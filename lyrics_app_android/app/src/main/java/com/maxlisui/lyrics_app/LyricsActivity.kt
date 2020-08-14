package com.maxlisui.lyrics_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.maxlisui.lyrics_app.helper.SpotifyHelper
import com.maxlisui.lyrics_app.model.SpotifySong

class LyricsActivity : AppCompatActivity() {

    private lateinit var playingTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)
        val token = intent.getStringExtra(ACCESS_TOKEN)
        playingTextView = findViewById(R.id.playingTextView)
        if(token != null) {
            val helper = SpotifyHelper(token, getString(R.string.spotify_base_url)) {onNewSong(it)}
            helper.getCurrentSong()
        }
    }

    private fun onNewSong(song: SpotifySong) {
        Log.d(LYRICS_ACTIVITY_LOG_HELPER, song.currentlyPlayingType)
        if(song.isPlaying && song.currentlyPlayingType == "track") {
            var displayValue = song.name
            if(song.artists.count() > 0) {
                displayValue += "\n" + song.artists.joinToString(" / ", transform = {it.name})
            }

            runOnUiThread {
                playingTextView.text = displayValue
            }
        }
    }
}