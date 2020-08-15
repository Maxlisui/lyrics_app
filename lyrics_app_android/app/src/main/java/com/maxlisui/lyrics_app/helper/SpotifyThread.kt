package com.maxlisui.lyrics_app.helper

import android.os.Process
import android.util.Log
import com.maxlisui.lyrics_app.SPOTIFY_THREAD_LOG_HELPER
import com.maxlisui.lyrics_app.SpotifySongToVoidAction
import java.util.concurrent.CountDownLatch

class SpotifyThread(private val spotifyHelper: SpotifyHelper, private val onNewSong: SpotifySongToVoidAction) : Runnable {

    private var currentlyPlayingId = ""
    private var isPlaying = false

    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
        try {
            while(true) {
                try {
                    val count = CountDownLatch(1)
                    spotifyHelper.getCurrentSong {
                        synchronized(SPOTIFY_THREAD_LOG_HELPER) {
                            if(currentlyPlayingId != it.id || isPlaying != it.isPlaying) {
                                currentlyPlayingId = it.id
                                isPlaying = it.isPlaying
                                onNewSong(it)
                            }
                            count.countDown()
                        }
                    }
                    count.await()
                    Thread.sleep(2000)
                } catch (e: Exception) {
                    Log.e(SPOTIFY_THREAD_LOG_HELPER, "Error while refreshing song", e)
                }
            }
        } catch (ex: Exception) {
            Log.e(SPOTIFY_THREAD_LOG_HELPER, "Error while running thread", ex)
        }
    }
}