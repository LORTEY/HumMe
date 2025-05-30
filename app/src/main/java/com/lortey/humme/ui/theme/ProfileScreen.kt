package com.lortey.humme.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.lortey.humme.LyricsRequest
import com.lortey.humme.Playlist
import com.lortey.humme.Profile
import com.lortey.humme.R
import com.lortey.humme.Track
import com.lortey.humme.addRequest
import com.lortey.humme.getLyrics
import com.lortey.humme.loadProfiles
import com.lortey.humme.playlistFromLink
import com.lortey.humme.refreshLyrics
import com.lortey.humme.saveLyrics
import com.lortey.humme.saveProfile
import java.util.Base64
import java.security.SecureRandom

var editedProfile: Profile? = null
var editedPlaylist: Playlist? = null

@Composable
fun ProfileScreen(context: Context, navController: NavController){
    val profiles by remember { mutableStateOf(loadProfiles(context)) }

    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(WindowInsets.systemBars.asPaddingValues())){
        LazyColumn {
            items(profiles){profile->
                var enabled by remember { mutableStateOf(profile.enabled) }
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .background(MaterialTheme.colorScheme.inverseOnSurface)){
                    UniversalTextEntry(profile.name, profile.playlists.map{it.name}.joinToString (", "),
                        Modifier.fillMaxWidth().weight(1.5f),/*justforquickeditting*/
                        {clickedProfile->
                            editedProfile = clickedProfile as Profile
                            navController.navigate("edit_profile")
                        },
                        profile)
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    Switch(
                        checked = enabled,
                        onCheckedChange = { newValue ->
                            profile.enabled = newValue
                            enabled = newValue
                            saveProfile(profile,context)
                        },
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { editedProfile = Profile(generateRandomBase48(),"", true, mutableListOf()); navController.navigate("edit_profile")}){
                        Text(
                            text = "Add Profile",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfile(context: Context, navController: NavController){
    var name by remember { mutableStateOf(editedProfile!!.name) }
    LaunchedEffect(Unit) {
        if(editedProfile == null){
            editedProfile = Profile(generateRandomBase48(),"", true, mutableListOf())
        }
    }
    var playlists by remember { mutableStateOf(editedProfile!!.playlists) }

    var showPopUp by remember { mutableStateOf(false) }

    var playlistLink by remember { mutableStateOf("") }

    var showProgress by remember { mutableStateOf(false) }

    var currentProgressSong by remember{ mutableStateOf("")}
    var currentProgressNumber by remember{ mutableStateOf(0)}
    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(WindowInsets.systemBars.asPaddingValues())) {
        OutlinedTextField(
            value = name,
            onValueChange = { editedProfile!!.name = it;
                            name = it},
            label = {
                Text(
                    "Profile Name",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

                cursorColor = MaterialTheme.colorScheme.primary,

                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            ),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )
        LazyColumn {
            items(playlists) { playlist ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    UniversalTextEntry(
                        playlist.name,
                        playlist.tracks.map { it.name }.joinToString (", "),
                        Modifier.fillMaxWidth().weight(1.5f),/*justforquickeditting*/
                        { clickedPlaylist ->
                            editedPlaylist = clickedPlaylist as Playlist
                            navController.navigate("edit_playlist")
                        },
                        playlist
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    IconButton(
                        onClick = {
                            editedProfile =
                                editedProfile!!.copy(playlists = editedProfile!!.playlists.filterNot { it == playlist }
                                    .toMutableList())

                            playlists = mutableListOf()
                            playlists.addAll(editedProfile!!.playlists)
                        },
                        modifier = Modifier.background(
                            shape = RoundedCornerShape(128.dp),
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        ).padding(horizontal = 10.dp).size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "remove",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }

            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        editedPlaylist = Playlist(generateRandomBase48(),null, "", mutableListOf()); navController.navigate("edit_playlist")
                    }) {
                        Text(
                            text = "Add Empty Playlist",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        showPopUp = true
                    }) {
                        Text(
                            text = "Add Playlist From Spotify",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                saveProfile(editedProfile!!, context)
                refreshLyrics(context)
                navController.popBackStack()
            }) {
                Text(
                    text = "Save",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    if(showPopUp) {
        Box(modifier = Modifier.fillMaxWidth().background(color=Color.Black.copy(alpha = 0.5f))){        }
        Popup(
            onDismissRequest = { showPopUp = false },
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true
            ),
            alignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(WindowInsets.systemBars.asPaddingValues())
            ) {
                OutlinedTextField(
                    value = playlistLink,
                    onValueChange = { playlistLink = it },
                    label = {
                        Text(
                            "Playlist Or Artist Link",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

                        cursorColor = MaterialTheme.colorScheme.primary,

                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                    ),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        showPopUp = false
                        val playlistFromLink = playlistFromLink(context, playlistLink)

                        editedProfile!!.playlists.add(playlistFromLink)
                        playlists = mutableListOf()
                        playlists.addAll(editedProfile!!.playlists)
                        addRequest(context,
                            buildList { playlistFromLink.tracks.forEach{ add(LyricsRequest(
                                generateRandomBase48(), it.artist.first(), it.name, editedProfile!!.id, PlaylistId = playlistFromLink.id, it.id))} })
                    }) {
                        Text(
                            text = "Add",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditPlaylist(context: Context, navController: NavController) {
    var name by remember { mutableStateOf(editedPlaylist!!.name) }
    LaunchedEffect(Unit) {
        if (editedPlaylist == null) {
            editedPlaylist = Playlist(generateRandomBase48(),null, "", mutableListOf())
        }
    }
    var trackList by remember { mutableStateOf(editedPlaylist!!.tracks) }
    var showPopUp by remember { mutableStateOf(false) }
    var editedTrack by remember { mutableStateOf<Track?>(null) }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        if (editedPlaylist!!.link == null) {
            OutlinedTextField(
                value = name,
                onValueChange = { editedPlaylist!!.name = it;
                                name = it},
                label = {
                    Text(
                        "Playlist Name",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

                    cursorColor = MaterialTheme.colorScheme.primary,

                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                ),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
        LazyColumn(modifier = Modifier.weight(0.8f)) {
            items(trackList) { track ->
                var enabled by remember { mutableStateOf(track.enabled) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .background(if(enabled) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.inverseOnSurface.copy(alpha=0.5f))
                ) {

                    UniversalTextEntry(
                        track.name, track.artist.joinToString (", "),
                        Modifier.fillMaxWidth().weight(1.5f),/*justforquickeditting*/
                        { clickedTrack ->
                            if (editedPlaylist!!.link == null) {
                                editedTrack = clickedTrack as Track
                                showPopUp = true
                            }
                        },
                        track,
                        enabled
                    )

                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    IconButton(
                        onClick = {
                            if(editedPlaylist!!.link != null) {
                                editedPlaylist = editedPlaylist!!.copy(
                                    tracks =
                                        editedPlaylist!!.tracks.map { item ->
                                            if (item.id == track.id) {
                                                item.copy(enabled = !enabled)
                                            } else {
                                                item
                                            }
                                        }.toMutableList()
                                )
                                trackList = mutableListOf()
                                trackList.addAll(editedPlaylist!!.tracks)
                                enabled = !enabled
                            }else{
                                editedPlaylist = editedPlaylist!!.copy(
                                    tracks =
                                        editedPlaylist!!.tracks.filterNot { it.id == track.id }.toMutableList()
                                )
                                trackList = mutableListOf()
                                trackList.addAll(editedPlaylist!!.tracks)

                            }
                        },
                        modifier = Modifier.background(
                            shape = RoundedCornerShape(128.dp),
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        ).padding(horizontal = 10.dp).size(32.dp)
                    ) {

                        Icon(
                            painter = painterResource(id = if(editedPlaylist!!.link != null){
                                if (enabled){
                                    R.drawable.visible
                                }else{
                                    R.drawable.visibility_off
                                }
                            }else{
                                R.drawable.delete
                            }),
                            contentDescription = "disable",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(32.dp)
                        )
                    }
                }
            }
            if (editedPlaylist!!.link == null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            editedTrack = Track(
                                id = generateRandomBase48(),
                                name = "",
                                artist = mutableListOf()
                            );
                            showPopUp = true
                        }) {
                            Text(
                                text = "Add Track",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                if(editedProfile!!.playlists.firstOrNull{it.id == editedPlaylist!!.id} == null){
                    editedProfile!!.playlists.add(editedPlaylist!!)
                }else{
                    editedProfile = editedProfile!!.copy(playlists = editedProfile!!.playlists.map{if(it.id == editedPlaylist!!.id) editedPlaylist!! else it}.toMutableList())
                }
                navController.popBackStack()
            }){
                Text(
                    text = "Save",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    if(showPopUp) {
        var name by remember { mutableStateOf(editedTrack!!.name ) }
        var artists by remember { mutableStateOf(editedTrack!!.artist.firstOrNull() ?: "") }
        var lyrics by remember { mutableStateOf(getLyrics(editedTrack!!.id,context)) }
        Box(modifier = Modifier.fillMaxWidth().background(color=Color.Black.copy(alpha = 0.5f))){        }
        Popup(
            onDismissRequest = { showPopUp = false },
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true
            ),
            alignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(WindowInsets.systemBars.asPaddingValues())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { editedTrack!!.name = it
                                    name = it},
                    label = {
                        Text(
                            "Track Name",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

                        cursorColor = MaterialTheme.colorScheme.primary,

                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                    ),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    value = artists,
                    onValueChange = {
                        if(editedTrack!!.artist.size > 0){
                            editedTrack!!.artist[0] = it
                        }else{
                            editedTrack!!.artist.add(it)
                        }
                        artists = it
                                    },
                    label = {
                        Text(
                            "Artist",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

                        cursorColor = MaterialTheme.colorScheme.primary,

                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                    ),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    value = lyrics ?: "",
                    onValueChange = { lyrics = it},
                    label = {
                        Text(
                            "Lyrics",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

                        cursorColor = MaterialTheme.colorScheme.primary,

                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                    ),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {

                        if(lyrics == null || lyrics!!.isEmpty()){
                            lyrics = getLyrics(context, name, artists)
                        }
                        if(editedPlaylist!!.tracks.firstOrNull{it.id == editedTrack!!.id} == null){
                            editedPlaylist!!.tracks.add(editedTrack!!)
                        }else{
                            editedPlaylist = editedPlaylist!!.copy(tracks = editedPlaylist!!.tracks.map{if(it.id == editedTrack!!.id) editedTrack!! else it}.toMutableList())
                        }
                        saveLyrics(editedTrack!!.id, context ,lyrics)
                        trackList = mutableListOf()
                        trackList.addAll(editedPlaylist!!.tracks)
                        showPopUp = false
                    }){
                        Text(
                            text = "Save",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

}
@Composable
fun UniversalTextEntry(textA:String, textB:String, modifier: Modifier, onClickAction:(Any) -> Unit, identity:Any, visible:Boolean = true){
    fun fadeColor(color:Color):Color{
        return if(visible) color else color.copy(alpha=0.5f)
    }
    Row(modifier = modifier.padding(vertical = 5.dp, horizontal = 4.dp).height(80.dp).clickable { onClickAction(identity) },   ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.background(Color.Transparent)
                .padding(vertical = 5.dp, horizontal = 4.dp).fillMaxWidth().fillMaxHeight()
        ) {
            Text(
                text = textA,
                modifier = Modifier.weight(1f).fillMaxSize().padding(horizontal = 10.dp),
                color = fadeColor(MaterialTheme.colorScheme.primary),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = textB,
                modifier = Modifier.weight(1f).fillMaxSize().padding(horizontal = 10.dp),
                color = fadeColor(MaterialTheme.colorScheme.onSurfaceVariant),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun generateRandomBase48(): String {
    val random = SecureRandom()
    val bytes = ByteArray(36) // 36 bytes → 48 Base64 chars
    random.nextBytes(bytes)

    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(bytes)
        .take(48) // Ensure exactly 48 characters
}