import Foundation

class SpotifyThread {
    private var isPlaying = false
    private var currentlyPlayingId = ""
    private let spotifyHelper: SpotifyHelper
    private let onNewSong: (SpotifySong) -> Void
    
    
    init(spotifyHelper: SpotifyHelper, onNewSong: @escaping (SpotifySong) -> Void) {
        self.spotifyHelper = spotifyHelper
        self.onNewSong = onNewSong
    }
    
    func run() {
        DispatchQueue.global(qos: .background).async {
            while true {
                let semaphore = DispatchSemaphore(value: 1)
                self.spotifyHelper.getCurrentSong() { (song) in
                    if self.currentlyPlayingId != song.id || self.isPlaying != song.isPlaying {
                        self.currentlyPlayingId = song.id
                        self.isPlaying = song.isPlaying
                        self.onNewSong(song)
                        semaphore.signal()
                    }
                }
                semaphore.wait()
                sleep(2)
            }
        }
    }
}
