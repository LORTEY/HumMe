package com.lortey.humme.ui.theme

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.lortey.humme.API
import com.lortey.humme.R
import com.lortey.humme.saveAPI

public var apikeys:API? = null
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuRender(context: Context, navController: NavHostController){
    var showPopup by remember{ mutableStateOf(false)}

    var spotifyClientID by remember{mutableStateOf("")}
    var spotifyClientSecret by remember{mutableStateOf("")}
    var spotifyRedirectURI by remember{mutableStateOf("")}
    var geniusClientID by remember{mutableStateOf("")}
    var geniusClientSecret by remember{mutableStateOf("")}
    LaunchedEffect(Unit) {
        spotifyClientID = apikeys?.spotifyClientID ?: ""
        spotifyClientSecret = apikeys?.spotifyClientSecret ?: ""
        spotifyRedirectURI = apikeys?.spotifyRedirectURI ?: ""
        geniusClientID = apikeys?.geniusClientID ?: ""
        geniusClientSecret = apikeys?.geniusClientSecret ?: ""
    }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Icon(
            painter = painterResource(id = R.drawable.apps),
            contentDescription = "Apis",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { showPopup = !showPopup }
                .width(80.dp)
                .align(Alignment.TopStart)
        )
    }
    if(showPopup){
        Popup(
            onDismissRequest = {showPopup = false},
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
                    value = spotifyClientID,
                    onValueChange = { spotifyClientID = it },
                    label = {
                        Text(
                            "Spotify Client ID",
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
                    value = spotifyClientSecret,
                    onValueChange = { spotifyClientSecret = it },
                    label = {
                        Text(
                            "Spotify Client Secret",
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
                    value = spotifyRedirectURI,
                    onValueChange = { spotifyRedirectURI = it },
                    label = {
                        Text(
                            "Spotify Redirect URI",
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
                    value = geniusClientID,
                    onValueChange = { geniusClientID = it },
                    label = {
                        Text(
                            "Genius Client ID",
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
                    value = geniusClientSecret,
                    onValueChange = { geniusClientSecret = it },
                    label = {
                        Text(
                            "Genius Client Secret",
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
                    Button(onClick = {apikeys = API(
                        spotifyClientID = if(spotifyClientID.isEmpty()) null else spotifyClientID,
                        spotifyClientSecret = if(spotifyClientSecret.isEmpty()) null else spotifyClientSecret,
                        spotifyRedirectURI = if(spotifyRedirectURI.isEmpty()) null else spotifyRedirectURI,
                        geniusClientID = if(geniusClientID.isEmpty()) null else geniusClientID,
                        geniusClientSecret = if(geniusClientSecret.isEmpty()) null else geniusClientSecret
                    )
                            showPopup= false
                        apikeys?.let{ saveAPI(it, context) }
                    }){
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