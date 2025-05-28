package com.lortey.humme

import android.content.Context
import com.lortey.humme.ui.theme.playlists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

// Define progress state globally (or in a class if preferred)
data class ProgressState(
    var showPopUp: Boolean = false,
    var showProgress: Boolean = false,
    var currentProgressSong: String = "",
    var currentProgressNumber: Int = 0,
    var totalTracks: Int = 0
)

val progressState = ProgressState() // Global instance

// Function to process playlist asynchronously
suspend fun processPlaylistAsync(context: Context, playlistLink: String) {
    val playlistFromLink = playlistFromLink(context, playlistLink)
    progressState.apply {
        showPopUp = true
        showProgress = true
        totalTracks = playlistFromLink.tracks.size
    }

    playlistFromLink.tracks.forEachIndexed { index, track ->
        progressState.apply {
            currentProgressSong = "${index + 1}/$totalTracks ${track.name}"
            currentProgressNumber = index + 1
        }

        // Process track async (replace with your actual logic)
        track.lyrics = withContext(Dispatchers.IO) {
            if (track.artist.isNotEmpty()) getLyrics(context, track.name, track.artist.first())
            else null
        }
    }

    editedProfile!!.playlists.add(playlistFromLink)
    playlists = editedProfile!!.playlists.toMutableList()

    progressState.showPopUp = false
    progressState.showProgress = false
}