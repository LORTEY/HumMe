package com.lortey.humme

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

public val APIFileName = "APIKeys.json"
private val jsonFormat = Json { prettyPrint = true } // jsonFormat

@Serializable
public data class API(
    var spotifyClientID:String?,
    var spotifyClientSecret:String?,
    //var spotifyRedirectURI:String?,
    var geniusAccessToken:String?,
    var spotifyRefreshToken:String?,
    var spotifyRefreshTokenExpiryTime:Long?,
)

public fun saveAPI(apiKeys:API, context: Context){
    val file = File(context.filesDir, APIFileName)
    val jsonString = jsonFormat.encodeToString(apiKeys)
    file.writeText(jsonString)
}

public fun loadAPI(context: Context):API{
    try {
        val file = File(context.filesDir, APIFileName)
        val content = file.readText()
        return jsonFormat.decodeFromString<API>(content)
    }catch(e:Exception){
        return API(null,null,null,null,null)
    }

}