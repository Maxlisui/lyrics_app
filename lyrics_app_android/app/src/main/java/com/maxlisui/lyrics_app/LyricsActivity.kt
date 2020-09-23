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
import com.snappydb.DB
import com.snappydb.DBFactory
import java.util.logging.Logger

class LyricsActivity : AppCompatActivity() {

    private lateinit var playingTextView: TextView
    private lateinit var lyricsTextView: TextView
    private lateinit var noLyricsTextView: TextView
    private lateinit var noLyricsScrollView: ScrollView
    private lateinit var lyricsScrollView: ScrollView
    private lateinit var lyricsDB: DB
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

        try {
            lyricsDB = DBFactory.open(this)
        } catch (ex: Exception) {
            Log.e(LYRICS_ACTIVITY_LOG_HELPER, "Error while opening DB", ex)
        }
    }

    override fun onDestroy() {
        try {
            lyricsDB.close()
        } catch (ex: Exception) {
            Log.e(LYRICS_ACTIVITY_LOG_HELPER, "Error while closing DB", ex)
        }

        super.onDestroy()
    }

    private fun onNewSong(song: SpotifySong) {
        Log.d(LYRICS_ACTIVITY_LOG_HELPER, song.currentlyPlayingType)
        var displayValue = getString(R.string.no_song_playing_lbl)
        var lyricsDone = false
        if(song.isPlaying && song.currentlyPlayingType == "track") {
            displayValue = song.name
            if(song.artists.count() > 0) {
                displayValue += "\n" + song.artists.joinToString(" / ", transform = {it.name})
            }

            val helper = GeniusHelper(getString(R.string.genius_base_url), getString(R.string.genius_client_access_token), lyricsDB) {
                if(it.isNotEmpty()) {
                    runOnUiThread {
                        lyricsTextView.text = it
                        lyricsScrollView.visibility = View.VISIBLE
                        lyricsScrollView.scrollTo(0, 0)
                        noLyricsScrollView.visibility = View.INVISIBLE
                        lyricsDone = true
                    }
                }
            }
            helper.getSongLyrics(song.id, song.name, song.artists[0].name)
        }
        runOnUiThread {
            if(!lyricsDone) {
                lyricsScrollView.visibility = View.INVISIBLE
                lyricsScrollView.scrollTo(0, 0)
                noLyricsScrollView.visibility = View.VISIBLE
                if(song.isPlaying) {
                    noLyricsTextView.text = getString(R.string.fetching_lyrics)
                } else {
                    noLyricsTextView.text = getString(R.string.no_song_playing_lbl)
                }
            }
            playingTextView.text = displayValue
        }
    }
}