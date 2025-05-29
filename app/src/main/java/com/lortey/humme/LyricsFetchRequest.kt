package com.lortey.humme

import android.content.Context
import com.lortey.humme.ui.theme.editedPlaylist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Queue

private const val lyricsRequestFile: String = "LyricsToFetch.txt"

private val jsonFormat = Json { prettyPrint = true }

@Serializable
data class LyricsRequest(
    val id:String,
    val Artist:String,
    val SongName:String,
    val ProfileId:String,
    val PlaylistId:String,
    val TrackId:String,
)

data class handleReguestsReturn(
    var running: Boolean = false,
    var stopHandling: Boolean = false,
    var processedIDList: MutableList<String>,
    var processedList: MutableList<LyricsRequest>,

)

var requestHandler : handleReguestsReturn? = null
suspend fun handelRequests(queue:List<LyricsRequest>, context:Context){
    val processedIDList:MutableList<String> = mutableListOf<String>()
    queue.forEach{ request ->
        requestHandler!!.running = true
        if(requestHandler == null){
            return
        }
        if(requestHandler!!.stopHandling){
            requestHandler!!.stopHandling = false
            requestHandler!!.processedIDList = processedIDList.toMutableList()
            requestHandler!!.processedList = queue.toMutableList()
            requestHandler!!.running = false
            return
        }
        var profile = loadProfiles(context, request.ProfileId).firstOrNull()
        if(profile != null) {
            var playlist = profile.playlists.find { it.id == request.PlaylistId }
            if (playlist != null) {
                val track = playlist.tracks.find { it.id == request.TrackId}
                if(track!= null){
                    if(track.lyrics == null || track.lyrics!!.isEmpty()){
                        val lyrics = getLyrics(context,request.SongName,request.Artist)
                        if (lyrics != "ERROR: REQUEST TIMED OUT"){

                            playlist = playlist.copy(
                                tracks =
                                    playlist.tracks.map { item ->
                                        if (item.id == request.TrackId) {
                                            item.copy(lyrics = lyrics)
                                        } else {
                                            item
                                        }
                                    }.toMutableList()
                            )
                            profile = profile.copy(
                                playlists =
                                    profile.playlists.map { item ->
                                        if (item.id == request.PlaylistId) {
                                            playlist
                                        } else {
                                            item
                                        }
                                    }.toMutableList()
                            )
                            saveProfile(profile,context)
                            processedIDList.add(request.TrackId)
                        }
                    }else{
                        processedIDList.add(request.TrackId)
                    }
                }else{
                    processedIDList.add(request.TrackId)
                }
            }else{
                processedIDList.add(request.TrackId)
            }
        }else{
            processedIDList.add(request.TrackId)
        }

    }
    requestHandler!!.stopHandling = false
    requestHandler!!.processedIDList = processedIDList.toMutableList()
    requestHandler!!.processedList = queue.toMutableList()
    requestHandler!!.running = false
    saveRequests(context, mergeID(loadRequests(context),requestHandler!!.processedList,processedIDList.toMutableList() ))
    return

}

fun addRequest(context:Context, request: List<LyricsRequest>, dontRefresh:Boolean = false){
    val oldData = loadRequests(context)
    oldData.addAll(request)
    saveRequests(context, oldData)
    if(dontRefresh){
        refreshLyrics(context)
    }
}
fun refreshLyrics(context: Context){
    val scope = CoroutineScope(Dispatchers.Default)
    if(requestHandler == null){
        requestHandler = handleReguestsReturn(running = true, stopHandling = false, processedIDList = mutableListOf(), processedList = mutableListOf())
        scope.launch{ handelRequests(loadRequests(context), context) }
        return
    }
    if(requestHandler!!.running){
        scope.launch{merge(context)}
        return
    }else{
        requestHandler = handleReguestsReturn(running = true, stopHandling = false, processedIDList = mutableListOf(), processedList = mutableListOf())
        scope.launch{ handelRequests(loadRequests(context), context) }
        return
    }
}
suspend fun merge(context: Context){
    if(requestHandler!!.running){
        requestHandler!!.stopHandling = true
    }
    var x = 0
    while (requestHandler!!.running) {
        delay(1000)
        x++
        if(x >= 30){
            return
        }
    }
    val newData = mergeID(loadRequests(context),requestHandler!!.processedList,
        requestHandler!!.processedIDList.toMutableList() )
    saveRequests(context, newData)
    requestHandler = handleReguestsReturn(running = true, stopHandling = false, processedIDList = mutableListOf(), processedList = mutableListOf())
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch{ handelRequests(newData, context) }
}
fun mergeID(listA:MutableList<LyricsRequest>, listB:MutableList<LyricsRequest>, exclude:List<String>):MutableList<LyricsRequest>{
    val list = mutableListOf<LyricsRequest>()
    list.addAll(listA)
    listB.forEach { entry->
        if(entry !in list){
            list.add(entry)
        }
    }
    val listToRemove = mutableListOf<LyricsRequest>()
    exclude.forEach { entry->
        val entryInList = list.find { it.TrackId == entry }
        if(entryInList != null){
            listToRemove.add(entryInList)
        }
    }
    list.removeAll(listToRemove)
    return list
}
fun loadRequests(context: Context):MutableList<LyricsRequest>{
    val file = File(context .getExternalFilesDir(null), lyricsRequestFile)
    if(!file.exists()){
        saveRequests(context ,mutableListOf())
        return mutableListOf()
    }
    return jsonFormat.decodeFromString<MutableList<LyricsRequest>>(file.readText())

}

fun saveRequests(context: Context, content:MutableList<LyricsRequest>){
    val file = File(context .getExternalFilesDir(null), lyricsRequestFile)
    file.writeText(jsonFormat.encodeToString(content))
}