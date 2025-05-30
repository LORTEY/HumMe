package com.lortey.humme

import android.content.Context

fun getActiveTracks(context:Context):List<Track>{
    val profiles = loadProfiles(context)
    val playlists = mutableListOf<Playlist>()
    profiles.forEach{profile->
        if(profile.enabled){
            playlists.addAll(profile.playlists)
        }
    }
    val tracks = mutableListOf<Track>()
    playlists.forEach{playlist->
        tracks.addAll(buildList { playlist.tracks.forEach{track-> if(track.enabled) add(track)} })
    }
    return tracks
}

fun getNextSong(tracksToPick:MutableList<Track>):Pair<MutableList<Track>, Track>?{
    if(tracksToPick.size > 0){
        val randomSong = tracksToPick.random()
        return Pair(tracksToPick.filterNot { it == randomSong }.toMutableList(), randomSong)
    }
    return null
}

fun getPreviousSong(previousSongs:MutableList<Pair<Track,Boolean>>):Pair<MutableList<Pair<Track,Boolean>>, Track>?{
    val previousSong = previousSongs.lastOrNull()
    if(previousSong != null){
        return  Pair(previousSongs.dropLast(1).toMutableList(), previousSong.first)
    }
    return null
}

fun rateSong(song:Track, isCorrect:Boolean,previousSongs:MutableList<Pair<Track,Boolean>>):MutableList<Pair<Track,Boolean>>{
    val listToReturn = previousSongs.toMutableList()
    listToReturn.add(Pair(song,isCorrect))
    return listToReturn
}