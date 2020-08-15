package com.maxlisui.lyrics_app.helper

import android.os.Process
import android.util.Log
import com.maxlisui.lyrics_app.SPOTIFY_THREAD_LOG_HELPER
import com.maxlisui.lyrics_app.SpotifySongToVoidAction
import java.util.concurrent.CountDownLatch

class SpotifyThread(private val spotifyHelper: SpotifyHelper, private val onNewSong: SpotifySongToVoidAction) : Runnable {
    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
        try {
            var currentlyPlayingId = ""
            var isPaying = false
            while(true) {
                try {
                    val count = CountDownLatch(1)
                    spotifyHelper.getCurrentSong {
                        if(currentlyPlayingId != it.id || isPaying != it.isPlaying) {
                            currentlyPlayingId = it.id
                            isPaying = it.isPlaying
                            onNewSong(it)
                        }
                        count.countDown()
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