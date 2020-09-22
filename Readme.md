# Lyris App

Small Android and IOS App which lets you display the lyrics of the currently playing song in spotify.

Everything is WIP 🚧

## Development

### IOS
Not yet started. 🚧🚧🚧

### Android

Assuming your have [Android Studio](https://developer.android.com/studio) and all that good stuff installed.

1. Head over to [Spotify's Developer Dashboard](https://developer.spotify.com/dashboard/applications) and create a new App.
2. Create a new Android Package with `com.maxlisui.lyrics_app` as package name and your fingerprint. Your development fingerprint can be accessed using

```sh
# Bash
$ keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v | grep SHA1

# Windows Powershell
$ keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore -list -v | grep SHA1
```

3. Also think of a redirect URI. This should be unique. E.g. `my-cool-spotify-app://auth`
4. Head over to [Genius](https://genius.com/api-clients) and create a new App. Also create a new client access token.
5. Now you should have a few keys, we don't need all of them. Create a new file in `res/values`. I suggest nameing it `res.xml`, so it gets ignored by git. The file should look something like this.
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="spotify_client_id">{{spotify client id}}</string>
    <integer name="requestCode">{{random int -> does not matter}}</integer>
    <string name="spotify_redirect_uri">{{redirect uri}}</string>
    <string name="genius_client_access_token">{{ genius client access token }}</string>
</resources>
```
- `spotify_client_id`: Created in step 1
- `spotify_redirect_uri`: Created in step 3
- `spotify_redirect_uri`: Created in step 4
  
6. Now Android Studio should be able to compile everything.

## Wanna help?
Sure 😁

## License

[MIT](LICENSE.md)