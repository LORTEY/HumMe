package com.lortey.humme

import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private val jsonFormat = Json { prettyPrint = true } // jsonFormat
private const val profileDirectory = "Profiles"


public fun saveProfile(profile:Profile, context: Context){
    val dir = File(context.getExternalFilesDir(null), profileDirectory)
    if (!dir.exists()) {
        dir.mkdirs()  // Creates parent directories if needed
    }
    val file = File(dir, URLEncoder.encode(profile.id, StandardCharsets.UTF_8.toString()))
    val jsonString = jsonFormat.encodeToString(profile)
    file.writeText(jsonString)
}

public fun loadProfiles(context: Context):List<Profile> {
    val dir = File(context.getExternalFilesDir(null), profileDirectory)
    val listOfProfiles:MutableList<Profile> = mutableListOf()
    val loadedFiles = listFilesInFilesDir(context)
    loadedFiles.forEach { filename->
        val file = File(dir, filename)
        val content = file.readText()
        listOfProfiles.add( jsonFormat.decodeFromString<Profile>(content))
    }
    return listOfProfiles.toList()
}
// lists all files in external files dir
fun listFilesInFilesDir(context: Context, folderName: String = profileDirectory): Array<String> {
    try {
        val directory = File(context.getExternalFilesDir(null), folderName) // Use getExternalFilesDir() here
        if (directory.exists()) {
            val filenames = directory.list()
            if (filenames != null) {
                return filenames
            }
        }
    } catch (e: Exception) {
        Log.e("FilesDir", "Error reading files directory: ${e.message}")
    }
    return arrayOf()
}