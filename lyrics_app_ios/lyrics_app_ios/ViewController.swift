import UIKit
import SpotifyLogin

class ViewController: UIViewController {
    
    private var spotifyThread: SpotifyThread!
    @IBOutlet weak var songLabel: UILabel!
    @IBOutlet weak var lyricsLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        songLabel.text = NSLocalizedString("no_song_playing_lbl", comment: "")
        lyricsLabel.text = NSLocalizedString("no_song_playing_lbl", comment: "")
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        SpotifyLogin.shared.getAccessToken { [weak self] (token, error) in
            if error != nil, token == nil {
                self?.gotToLogin()
                return
            }
            
            let helper = SpotifyHelper(accessToken: token!, baseUrl: Constants.spotifyBaseUrl)
            self?.spotifyThread = SpotifyThread(spotifyHelper: helper) { (song) in self?.onNewSong(song: song) }
            self?.spotifyThread.run()
        }
    }
    
    func gotToLogin() {
        self.performSegue(withIdentifier: "go_to_login", sender: self)
    }
    
    func onNewSong(song: SpotifySong) {
        var displayValue = NSLocalizedString("no_song_playing_lbl", comment: "")
        if song.isPlaying && song.currentlyPlayingType == "track" {
            displayValue = song.name
            if song.artists.count > 0 {
                displayValue += "\n" + song.artists.map { $0.name }.joined(separator: "/")
            }
        }
        DispatchQueue.main.async {
            self.songLabel.text = displayValue
        }
    }
}

