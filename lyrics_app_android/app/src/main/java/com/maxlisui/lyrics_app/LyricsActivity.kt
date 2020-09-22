package com.maxlisui.lyrics_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.maxlisui.lyrics_app.helper.GeniusHelper
import com.maxlisui.lyrics_app.helper.SpotifyHelper
import com.maxlisui.lyrics_app.helper.SpotifyThread
import com.maxlisui.lyrics_app.model.SpotifySong

class LyricsActivity : AppCompatActivity() {

    private lateinit var playingTextView: TextView
    private lateinit var lyricsTextView: TextView
    private lateinit var noLyricsTextView: TextView
    private lateinit var noLyricsScrollView: ScrollView
    private lateinit var lyricsScrollView: ScrollView
    private lateinit var spotifyThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)
        val token = intent.getStringExtra(ACCESS_TOKEN)
        playingTextView = findViewById(R.id.playingTextView)
        lyricsTextView = findViewById(R.id.lyricsTextView)
        noLyricsTextView = findViewById(R.id.noLyricsTextView)
        noLyricsScrollView = findViewById(R.id.noLyricsScrollView)
        lyricsScrollView = findViewById(R.id.lyricsScrollView)
        if(token != null) {
            val helper = SpotifyHelper(token, getString(R.string.spotify_base_url))
            val sp = SpotifyThread(helper) {onNewSong(it)}
            spotifyThread = Thread(sp)
            spotifyThread.start()
        }
    }

    private fun onNewSong(song: SpotifySong) {
        Log.d(LYRICS_ACTIVITY_LOG_HELPER, song.currentlyPlayingType)
        var displayValue = getString(R.string.no_song_playing_lbl)
        if(song.isPlaying && song.currentlyPlayingType == "track") {
            displayValue = song.name
            if(song.artists.count() > 0) {
                displayValue += "\n" + song.artists.joinToString(" / ", transform = {it.name})
            }

            val helper = GeniusHelper(getString(R.string.genius_base_url), getString(R.string.genius_client_access_token)) {
                if(it.isNotEmpty()) {
                    runOnUiThread {
                        lyricsTextView.text = it
                        lyricsScrollView.visibility = View.VISIBLE
                        lyricsScrollView.scrollTo(0, 0)
                        noLyricsScrollView.visibility = View.INVISIBLE
                    }
                }
            }
            helper.getSongLyrics(song.name, song.artists[0].name)
        }
        runOnUiThread {
            playingTextView.text = displayValue
        }
    }
}