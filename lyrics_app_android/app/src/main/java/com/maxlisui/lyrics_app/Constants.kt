package com.maxlisui.lyrics_app

import com.maxlisui.lyrics_app.model.SpotifySong

const val ACCESS_TOKEN = "com.maxlisui.lyrics_app.ACCESS_TOKEN"
const val SPOTIFY_HELPER_LOG_HELPER = "Spotify Helper"
const val LYRICS_ACTIVITY_LOG_HELPER = "Lyrics Activity"
const val SPOTIFY_THREAD_LOG_HELPER = "Spotify Thread"
const val GENIUS_HELPER_LOG_HELPER = "Genius Helper"

typealias SpotifySongToVoidAction = (SpotifySong) -> Unit
typealias StringToVoidAction = (String) -> Unit