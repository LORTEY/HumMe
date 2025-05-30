package com.lortey.humme

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable

@Serializable
data class Profile( // The data class used for profile
    val id: String,
    var name:String,//name of profile
    var enabled: Boolean = true, // Is profile currently active
    val playlists: MutableList<Playlist>
)

@Serializable
data class Track(
    val id: String, //Spotify id or null for user added
    var name:String,
    var artist:MutableList<String>,
    //var lyrics:String? = null,
    var enabled: Boolean = true // if song was disabled by user
)

@Serializable
data class Playlist(
    val id: String,
    val link:String?, //Link In spotify or null for user added
    var name:String,
    val tracks:MutableList<Track>
)