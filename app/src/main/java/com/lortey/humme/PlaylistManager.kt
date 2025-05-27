package com.lortey.humme

import android.content.Context

fun playlistFromLink(context: Context,link:String):Playlist{
    val spotifyPlaylist = getPlaylist(context,link)
    val playlist:Playlist
    playlist = Playlist(generateRandomBase48(),link,spotifyPlaylist.name, mutableListOf())
    val tracks = mutableListOf<Track>()
    spotifyPlaylist.tracks.forEach{track->
        tracks.add(Track(track.id, track.name, artist = track.artist.toMutableList()))
    }
    playlist.tracks.addAll(tracks)
    return playlist
}

fun playlistRefresh(context:Context, playlist: Playlist):Playlist{
    val refreshedPlaylist:Playlist = playlistFromLink(context, playlist.link!!)
    val newTracks = playlist.tracks
    newTracks.addAll(refreshedPlaylist.tracks.filter{it.id !in newTracks.map { it.id }})
    return playlist.copy(tracks = newTracks)
}