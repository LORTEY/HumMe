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