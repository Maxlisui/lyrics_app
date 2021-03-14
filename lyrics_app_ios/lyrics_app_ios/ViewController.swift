import UIKit
import SpotifyLogin
import SwiftLvDB

class ViewController: UIViewController {
    
    private var spotifyThread: SpotifyThread!
    private var lyricsDB: SwiftLvDB!
    @IBOutlet weak var songLabel: UILabel!
    @IBOutlet weak var lyricsTextView: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        songLabel.text = NSLocalizedString("no_song_playing_lbl", comment: "")
        lyricsTextView.changeTextAndCenter(text: NSLocalizedString("no_song_playing_lbl", comment: ""))
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        SpotifyLogin.shared.getAccessToken { [weak self] (token, error) in
            if error != nil, token == nil {
                self?.gotToLogin()
                return
            }
            
            self?.lyricsDB = SwiftLvDB(subName: "lyrics_app_db")
            
            let helper = SpotifyHelper(accessToken: token!, baseUrl: Constants.spotifyBaseUrl)
            self?.spotifyThread = SpotifyThread(spotifyHelper: helper) { (song) in self?.onNewSong(song: song) }
            self?.spotifyThread.run()
        }
    }
    
    func gotToLogin() {
        self.performSegue(withIdentifier: "go_to_login", sender: self)
    }
    
    func onNewSong(song: SpotifySong) {
        var labelText = NSLocalizedString("no_song_playing_lbl", comment: "")
        var lyricsText = ""
        if song.isPlaying && song.currentlyPlayingType == "track" {
            labelText = song.name
            lyricsText = NSLocalizedString("fetching_new_lyrics_lbl", comment: "")
            if song.artists.count > 0 {
                labelText += "\n" + song.artists.map { $0.name }.joined(separator: "/")
                
                do {
                    let helper = try GeniusHelper(baseUrl: Constants.geniusBaseUrl, accessToken: Constants.geniusAccessToken, lyricsDB: self.lyricsDB) { lyrics in
                        DispatchQueue.main.async {
                            self.lyricsTextView.changeTextAndCenter(text: lyrics)
                        }
                    }
                    helper.getSongLyrics(songId: song.id, songName: song.name, artistName: song.artists[0].name)
                } catch {
                    // Ingore
                }
            }
        }
        DispatchQueue.main.async {
            self.songLabel.text = labelText
            if !lyricsText.isEmpty {
                self.lyricsTextView.changeTextAndCenter(text: lyricsText)
            }
        }
    }
}

