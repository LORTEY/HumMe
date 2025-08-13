package com.lortey.humme.ui.theme

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lortey.humme.APIFileName
import com.lortey.humme.Playlist
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.lortey.humme.playlistFromLink

var playlists:MutableList<Playlist>? = null

private const val playlistFile:String = "Playlists"
private val jsonFormat = Json { prettyPrint = true }

@Composable
fun PlaylistScreen(context: Context, navController: NavHostController) {
    LaunchedEffect(Unit) {
        loadPlaylists(context)
    }
    var showPopup by remember { mutableStateOf(false)}
    var playlistUrl by remember { mutableStateOf("") }
    var list:List<Playlist> by remember (playlists){ mutableStateOf(playlists ?: listOf()) }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        LazyColumn {
            items(list) { playlistName ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.clickable {}
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .height(100.dp)
                        .fillMaxWidth()) {
                    Text(
                        text = playlistName.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
            item {
                Button(onClick = { showPopup = true; playlistUrl = "" }) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxWidth()) {
                        Text(
                            text = "Add Playlist",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }


        }
        if (showPopup) {
            Popup(
                onDismissRequest = { showPopup = false },
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
                        value = playlistUrl,
                        onValueChange = { playlistUrl = it },
                        label = {
                            Text(
                                "Spotify Playlist Url",
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
                            val newPlaylistContent = playlistFromLink(context, playlistUrl)
                            if(newPlaylistContent!=null) {
                                if (playlists == null) {
                                    playlists = mutableListOf(newPlaylistContent)
                                } else {
                                    playlists!!.add(newPlaylistContent)
                                }
                                writePlaylists(context)
                                loadPlaylists(context)
                                showPopup = false
                            }
                        }
                        ) {
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
}

public fun loadPlaylists(context: Context){
    val file = File(context.getExternalFilesDir(null), playlistFile)
    if(file.exists()) {
        playlists = jsonFormat.decodeFromString<MutableList<Playlist>?>(file.readText())
    }
}

public fun writePlaylists(context: Context){
    val file = File(context.getExternalFilesDir(null), playlistFile)
    file.writeText(jsonFormat.encodeToString(playlists))
}