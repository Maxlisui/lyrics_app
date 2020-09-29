import Foundation

class SpotifyHelper {
    let accessToken: String
    let baseUrl: String
    
    init(accessToken: String, baseUrl: String) {
        self.accessToken = accessToken;
        self.baseUrl = baseUrl;
    }
    
    func getCurrentSong(completion: @escaping (_ song: SpotifySong) -> Void) {
        let url = String(format: self.baseUrl + "me/player")
        guard let serviceUrl = URL(string: url) else { return }
        
        var request = URLRequest(url: serviceUrl)
        request.httpMethod = "GET"
        request.setValue("Bearer " + self.accessToken, forHTTPHeaderField: "Authorization")
        let session = URLSession.shared
        
        session.dataTask(with: request) { (data, response, error) in
            if let response = response {
                print(response)
            }
            
            if let data = data {
                if let song = SpotifySong(JSONString: String(decoding: data, as: UTF8.self)) {
                    completion(song)
                }
            }
        }.resume()
    }
}
