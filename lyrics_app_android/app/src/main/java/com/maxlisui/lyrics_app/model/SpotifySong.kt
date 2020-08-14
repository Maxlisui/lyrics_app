package com.maxlisui.lyrics_app.model

class SpotifySong {
    var isPlaying = false
    var currentlyPlayingType = ""
    var name = ""
    var album = SpotifyAlbum()
    var artists = mutableListOf<SpotifyArtist>()
}