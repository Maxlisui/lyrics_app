import ObjectMapper

class SpotifyArtist: Mappable {
    var name: String = ""
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        name <- map["name"]
    }
}
