import ObjectMapper

class SpotifyAlbum: Mappable {
    var name: String = ""
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        name <- map["name"]
    }
}
