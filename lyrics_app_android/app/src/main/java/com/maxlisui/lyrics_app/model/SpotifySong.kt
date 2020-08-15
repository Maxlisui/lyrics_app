package com.maxlisui.lyrics_app.model

class SpotifySong {
    var isPlaying = false
    var currentlyPlayingType = ""
    var id = ""
    var name = ""
    var album = SpotifyAlbum()
    var artists = mutableListOf<SpotifyArtist>()
}