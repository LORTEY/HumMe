package com.lortey.humme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.lortey.humme.ui.theme.loadPlaylists
import com.lortey.humme.ui.theme.playlists
import com.lortey.humme.ui.theme.writePlaylists
import java.util.Base64
import java.security.SecureRandom

var editedProfile:Profile? = null
var editedPlaylist:Playlist? = null

@Composable
fun ProfileScreen(context: Context, navController: NavController){
    val profiles by remember { mutableStateOf(loadProfiles(context)) }

    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(WindowInsets.systemBars.asPaddingValues())){
        LazyColumn {
            items(profiles){profile->
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)){
                    UniversalTextEntry(profile.name, profile.playlists.map{it.name}.joinToString { ", " },
                        {clickedProfile->
                            editedProfile = clickedProfile as Profile
                            navController.navigate("edit_profile")
                        },
                        profile)
                    Spacer(modifier = Modifier.weight(1f).padding(horizontal = 20.dp))
                    Switch(
                        checked = profile.enabled.value,
                        onCheckedChange = { newValue ->
                            profile.enabled = mutableStateOf(newValue)
                            saveProfile(profile,context)
                        }
                    )
                }

            }
        }
    }
}

@Composable
fun EditProfile(context: Context, navController: NavController){
    LaunchedEffect(Unit) {
        if(editedProfile == null){
            editedProfile = Profile("", mutableStateOf(true), mutableListOf())
        }
    }

    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(WindowInsets.systemBars.asPaddingValues())){
        OutlinedTextField(
            value = editedProfile!!.name,
            onValueChange = { editedProfile!!.name = it },
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
            items(editedProfile!!.playlists){playlist->
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)){
                    UniversalTextEntry(playlist.name, playlist.tracks.map{it.name}.joinToString { ", " },
                        {clickedPlaylist->
                            editedPlaylist = clickedPlaylist as Playlist
                            navController.navigate("edit_playlist")
                        },
                        playlist)
                    Spacer(modifier = Modifier.weight(1f).padding(horizontal = 20.dp))
                    IconButton(onClick = { editedProfile = editedProfile!!.copy(playlists = editedProfile!!.playlists.filterNot { it == playlist }.toMutableList())},
                        modifier = Modifier.background(
                        shape = RoundedCornerShape(128.dp),
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    ) .size(32.dp)){
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
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { editedPlaylist = null; navController.navigate("edit_playlist")}){
                        Text(
                            text = "Add Empty Playlist",
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
    LaunchedEffect(Unit) {
        if (editedPlaylist == null) {
            editedPlaylist = Playlist(null, "", mutableListOf())
        }
    }
    var showPopUp by remember { mutableStateOf(false) }
    var editedTrack by remember { mutableStateOf<Track?>(null) }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        if (editedPlaylist!!.link == null) {
            OutlinedTextField(
                value = editedPlaylist!!.name,
                onValueChange = { editedPlaylist!!.name = it },
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
        LazyColumn {
            items(editedPlaylist!!.tracks) { track ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    UniversalTextEntry(
                        track.name, track.artist.joinToString { ", " },
                        { clickedTrack ->
                            if (editedPlaylist!!.link == null) {
                                editedTrack = clickedTrack as Track
                                showPopUp = true
                            }
                        },
                        track
                    )
                    Spacer(modifier = Modifier.weight(1f).padding(horizontal = 20.dp))
                    IconButton(
                        onClick = {
                            editedPlaylist = editedPlaylist!!.copy(
                                tracks =
                                    editedPlaylist!!.tracks.map { item ->
                                        if (item.id == track.id) {
                                            item.copy(enabled = mutableStateOf(!track.enabled.value))
                                        } else {
                                            item
                                        }
                                    }.toMutableList())
                        },
                        modifier = Modifier.background(
                            shape = RoundedCornerShape(128.dp),
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        ).size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = if (track.enabled.value) R.drawable.visible else R.drawable.visibility_off),
                            contentDescription = "disable",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
            if(editedPlaylist!!.link == null){
                item{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {editedTrack = null; showPopUp = true}){
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
        if(showPopUp){
            LaunchedEffect(editedTrack) {
                if(editedTrack == null){
                    editedTrack = Track(
                        id= generateRandomBase48(),
                        name = "",
                        artist = mutableListOf()
                    )
                }
            }
            Popup(
                onDismissRequest = { showPopUp = false },
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true
                )
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(WindowInsets.systemBars.asPaddingValues())
                ) {
                    OutlinedTextField(
                        value = editedTrack!!.name,
                        onValueChange = { editedTrack!!.name = it },
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
                        value = editedTrack!!.artist.joinToString { ", " },
                        onValueChange = { editedTrack!!.artist[0] = it },
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
                        value = editedTrack!!.lyrics ?: "",
                        onValueChange = { editedTrack!!.lyrics = it },
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
                            if(editedPlaylist!!.tracks.firstOrNull{it.id == editedTrack!!.id} == null){
                                editedPlaylist!!.tracks.add(editedTrack!!)
                            }else{
                                editedPlaylist = editedPlaylist!!.copy(tracks = editedPlaylist!!.tracks.map{if(it.id == editedTrack!!.id) editedTrack!! else it}.toMutableList())
                            }
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                editedProfile!!.playlists.
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
@Composable
fun UniversalTextEntry(textA:String, textB:String, onClickAction:(Any) -> Unit, identity:Any){
    Surface(modifier = Modifier.padding(vertical = 5.dp, horizontal = 4.dp).height(80.dp).clickable { onClickAction(identity) },  shadowElevation = 4.dp, color = MaterialTheme.colorScheme.inverseOnSurface) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.background(Color.Transparent)
                .padding(vertical = 5.dp, horizontal = 4.dp).fillMaxWidth().fillMaxHeight()
        ) {
            Text(
                text = textA,
                modifier = Modifier.weight(1f).fillMaxSize().padding(horizontal = 10.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = textB,
                modifier = Modifier.weight(1f).fillMaxSize().padding(horizontal = 10.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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