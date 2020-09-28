import UIKit
import SpotifyLogin

class ViewController: UIViewController {
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        SpotifyLogin.shared.getAccessToken { [weak self] (token, error) in
            if error != nil, token == nil {
                self?.gotToLogin()
            }
        }
    }
    
    func gotToLogin() {
        self.performSegue(withIdentifier: "go_to_login", sender: self)
    }
}

