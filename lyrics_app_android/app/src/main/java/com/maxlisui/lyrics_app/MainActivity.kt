package com.maxlisui.lyrics_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.spotify.sdk.android.auth.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private var requestCode: Int by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestCode = resources.getInteger(R.integer.requestCode)
        login()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == this.requestCode) {
            val response = AuthorizationClient.getResponse(resultCode, data)

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> redirectToLyricsActivity(response.accessToken)
                else -> Toast.makeText(applicationContext, R.string.login_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onLoginClick(@Suppress("UNUSED_PARAMETER") view: View) {
        login()
    }

    private fun login() {
        val builder = AuthorizationRequest.Builder(getString(R.string.spotify_client_id), AuthorizationResponse.Type.TOKEN, getString(R.string.spotify_redirect_uri))
        builder.setScopes(arrayOf("user-read-playback-state", "user-read-currently-playing"))
        val request = builder.build()

        AuthorizationClient.openLoginActivity(this, requestCode, request)
    }

    private fun redirectToLyricsActivity(token: String) {
        val intent = Intent(this, LyricsActivity::class.java).apply {
            putExtra(ACCESS_TOKEN, token)
        }
        startActivity(intent)
    }
}