package com.lortey.humme

import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.putLong
import android.provider.Settings.Global.putString
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lortey.humme.ui.theme.apikeys
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.util.logging.Logger

class SpotifyCallbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data
        if (uri?.scheme == "humme" && uri.host == "callback") {
            val code = uri.getQueryParameter("code")
            val error = uri.getQueryParameter("error")

            if (code != null) {
                exchangeCodeForToken(code, this)
            } else if (error != null) {
                setResult(RESULT_CANCELED)
                finish()
            }
        } else {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun exchangeCodeForToken(authorizationCode: String, context: Context) {
        // Retrieve the code verifier you saved when starting the login
        val sharedPref = context.getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE)
        val codeVerifier = sharedPref.getString("code_verifier", null) ?: run {
            finish()
            return
        }

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", authorizationCode)
            .add("redirect_uri", redirectUri)
            .add("client_id", apikeys!!.spotifyClientID ?: "")
            .add("code_verifier", codeVerifier)
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    return
                }

                val responseBody = response.body?.string()
                try {
                    apikeys = loadAPI(applicationContext)
                    val json = JSONObject(responseBody ?: "")
                    val accessToken = json.getString("access_token")
                    val refreshToken = json.getString("refresh_token")
                    val expiresIn = json.getInt("expires_in")

                    // Calculate expiration time (current time + expiresIn seconds)
                    val expirationTime = System.currentTimeMillis() + expiresIn * 1000

                    // Save tokens securely
                    with(sharedPref.edit()) {
                        putString("access_token", accessToken)
                        putString("refresh_token", refreshToken)
                        putLong("expiration_time", expirationTime)
                        apply()
                    }
                    apikeys!!.spotifyRefreshToken = refreshToken
                    apikeys!!.spotifyRefreshTokenExpiryTime = expirationTime
                    saveAPI(apikeys!!,context)
                    // Return to main activity with success
                    runOnUiThread {
                        setResult(RESULT_OK)
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        })
    }
}