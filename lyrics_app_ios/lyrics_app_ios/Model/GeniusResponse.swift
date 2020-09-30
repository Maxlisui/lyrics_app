import ObjectMapper

class GeniusResponse: Mappable {
    var hits: [GeniusHit] = [GeniusHit]()
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        hits <- map["response.hits"]
    }
}
