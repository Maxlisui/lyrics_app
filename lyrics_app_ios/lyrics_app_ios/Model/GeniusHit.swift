import ObjectMapper

class GeniusHit: Mappable {
    var url: String = ""
    var title: String = ""
    var artist: String = ""
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        url <- map["result.url"]
        title <- map["result.title"]
        artist <- map["result.primary_artist.name"]
    }
}
