package com.lortey.humme

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private val jsonFormat = Json { prettyPrint = true }
private const val lyricsDirectory = "Lyrics"

fun getLyrics(trackId:String, context: Context):String{
    val directory = File(context.getExternalFilesDir(null), lyricsDirectory)
    if(!directory.exists()){
        directory.mkdir()
    }
    val file = File(directory, trackId)
    return if(file.exists()) file.readText() else ""
}

public fun saveLyrics(trackId:String, context: Context, lyrics:String){
    val directory = File(context.getExternalFilesDir(null), lyricsDirectory)
    if(!directory.exists()){
        directory.mkdir()
    }
    val file = File(directory, trackId)
    file.writeText(lyrics)
}