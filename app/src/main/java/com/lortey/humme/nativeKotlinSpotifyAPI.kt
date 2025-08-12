package com.lortey.humme


import android.content.Context
import android.content.Intent
import android.net.Uri
import com.adamratzman.spotify.SpotifyAppApi;
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.getSpotifyAuthorizationUrl
import com.adamratzman.spotify.getSpotifyPkceAuthorizationUrl
import com.adamratzman.spotify.getSpotifyPkceCodeChallenge
import com.adamratzman.spotify.javainterop.SpotifyContinuation;
import com.adamratzman.spotify.models.Album;
import com.lortey.humme.ui.theme.generateRandomBase48
import org.jetbrains.annotations.NotNull;

val redirectUri = "humme://callback"
fun startSpotifyLogin(apikeys: API, context: Context, codeVerifier:String) {
    val codeChallenge = getSpotifyPkceCodeChallenge(codeVerifier)

    if (apikeys.spotifyClientID == null) return

    apikeys.spotifyClientID?.let { clientId ->
        val url = getSpotifyPkceAuthorizationUrl(
            scopes = arrayOf(SpotifyScope.PlaylistReadPrivate),
            clientId = clientId,
            redirectUri = redirectUri,
            codeChallenge = codeChallenge
        )

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

}

