import Foundation
import SwiftSoup
import SwiftLvDB

class GeniusHelper {
    private let baseUrl: String
    private let accessToken: String
    private let onNewLyrics: (String) -> Void
    private let linkRegex: NSRegularExpression
    private let newLineRegex: NSRegularExpression
    private let tagRegex: NSRegularExpression
    private let tooMuchNewLineRegex: NSRegularExpression
    private let lyricsDB: SwiftLvDB
    
    init(baseUrl: String, accessToken: String, lyricsDB: SwiftLvDB, onNewLyrics: @escaping (String) -> Void) throws {
        self.baseUrl = baseUrl
        self.accessToken = accessToken
        self.onNewLyrics = onNewLyrics
        self.lyricsDB = lyricsDB
        
        self.linkRegex = try NSRegularExpression(pattern: "(<a.*?>)", options: .dotMatchesLineSeparators)
        self.newLineRegex = try NSRegularExpression(pattern: "(<br />|<br/>|<br>)")
        self.tagRegex = try NSRegularExpression(pattern: "(<.*?>)")
        self.tooMuchNewLineRegex = try NSRegularExpression(pattern: "\\s+\\n\\n")
        
    }
    
    func getSongLyrics(songId: String, songName: String, artistName: String) {
        if let value = lyricsDB.string(forKey: songId) {
            self.onNewLyrics(value)
        }
        
        guard let url = String(format: self.baseUrl + "search?q=" + songName + " " + artistName).addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) else { return }
        guard let serviceUrl = URL(string: url) else { return }
        
        var request = URLRequest(url: serviceUrl)
        request.httpMethod = "GET"
        request.setValue("Bearer " + self.accessToken, forHTTPHeaderField: "Authorization")
        
        let session = URLSession.shared
        session.dataTask(with: request) { (data, response, error) in
            guard let data = data else {
                return
            }
            
            guard let response = GeniusResponse(JSONString: String(decoding: data, as: UTF8.self)) else {
                return
            }
            
            for hit in response.hits {
                if hit.artist.lowercased() == artistName.lowercased() && hit.title.lowercased() == songName.lowercased() {
                    guard let url = URL(string: hit.url) else {
                        continue
                    }
                    
                    do {
                        let html = try String(contentsOf: url, encoding: .utf8)
                        let doc = try SwiftSoup.parseBodyFragment(html)
                        
                        var lyricsElement = try doc.getElementById("lyrics")
                        
                        if lyricsElement == nil {
                            let el = try doc.getElementsByClass("lyrics")
                            if el.count > 0 {
                                lyricsElement = el[0]
                            }
                        } else {
                            guard let sib1 = try lyricsElement!.nextElementSibling() else {
                                continue
                            }
                            
                            guard let sib2 = try sib1.nextElementSibling() else {
                                continue
                            }
                            
                            lyricsElement = sib2
                        }
                        
                        if lyricsElement == nil {
                            continue
                        }
                        
                        let content: String = try lyricsElement!.html()
                        
                        let noLinks = self.linkRegex.stringByReplacingMatches(in: content, options: [], range: NSMakeRange(0, content.count), withTemplate: "")
                        let noNewLine = self.newLineRegex.stringByReplacingMatches(in: noLinks, options: [], range: NSMakeRange(0, noLinks.count), withTemplate: "\n")
                        let noTag = self.tagRegex.stringByReplacingMatches(in: noNewLine, options: [], range: NSMakeRange(0, noNewLine.count), withTemplate: "")
                        let lyrics = self.tooMuchNewLineRegex.stringByReplacingMatches(in: noTag, options: [], range: NSMakeRange(0, noTag.count), withTemplate: "")
                        
                        self.lyricsDB.setString(lyrics, forKey: songId)
                        
                        self.onNewLyrics(lyrics)
                        break
                        
                    } catch Exception.Error(let type, let message) {
                        print(type)
                        print(message)
                    } catch {
                        // Ignored
                    }
                }
            }
        }.resume()
    }
}
