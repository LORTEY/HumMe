package com.lortey.humme

import android.content.Context
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private var moduleSpotify: PyObject? = null
private var moduleGenius: PyObject? = null
private val jsonFormat = Json { prettyPrint = true }

@Serializable
data class Track(
    val name:String,
    val artist:List<String>
)

@Serializable
data class Playlist(
    val name:String,
    val tracks:List<Track>
)

private fun getPython(context: Context): Python {
    if (!Python.isStarted()) {
        Python.start(AndroidPlatform(context))
    }

    val python = Python.getInstance()
    return python
}
private fun getModuleSpotify(context: Context): PyObject {
    return moduleSpotify ?: getPython(context).getModule("spotify")
}

private fun getModuleGenius(context: Context): PyObject {
    return moduleGenius ?: getPython(context).getModule("genius")
}

public fun InitializeSp(context:Context, apikeys:API):Boolean{
    val module = getModuleSpotify(context)
    val initialized = module.callAttr("set_global_sp", apikeys.spotifyClientID, apikeys.spotifyClientSecret).toBoolean()

    return initialized
}

public fun getPlaylist(context:Context,playlistUri:String):Playlist{
    val module = getModuleSpotify(context)
    val playlistStr = module.callAttr("get_playlist_tracks",playlistUri).toString()
    return jsonFormat.decodeFromString<Playlist>(playlistStr)
}
