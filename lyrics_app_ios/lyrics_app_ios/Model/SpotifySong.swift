import ObjectMapper

class SpotifySong: Mappable {
    var isPlaying: Bool = false
    var currentlyPlayingType: String = ""
    var id: String = ""
    var name: String = ""
    var album: SpotifyAlbum?
    var artists: [SpotifyArtist] = [SpotifyArtist]()
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        isPlaying <- map["is_playing"]
        currentlyPlayingType <- map["currently_playing_type"]
        name <- map["item.name"]
        id <- map["item.id"]
        album <- map["item.album"]
        artists <- map["item.artists"]
    }
}
