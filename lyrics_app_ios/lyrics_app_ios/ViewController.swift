import UIKit
import SpotifyLogin

class ViewController: UIViewController {

    var loginButton: UIButton?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        let button = SpotifyLoginButton(viewController: self, scopes: [.userReadPlaybackState, .userReadCurrentlyPlaying])
        self.loginButton = button
        self.view.addSubview(button)
        
        NotificationCenter.default.addObserver(self, selector: #selector(loginSucessful), name: .SpotifyLoginSuccessful, object: nil)
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        loginButton?.center = self.view.center
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    @objc func loginSucessful() {
        print("YEAH")
    }
}

