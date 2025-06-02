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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.lortey.humme.API
import com.lortey.humme.InitializeSp
import com.lortey.humme.R
import com.lortey.humme.initializeGenius
import com.lortey.humme.saveAPI

public var apikeys:API? = null
@Composable
fun MainMenuRender(context: Context, navController: NavHostController){
    var showPopup by remember{ mutableStateOf(false)}

    var spotifyClientID by remember{mutableStateOf("")}
    var spotifyClientSecret by remember{mutableStateOf("")}
    var geniusAccessToken by remember{mutableStateOf("")}
    LaunchedEffect(Unit) {
        spotifyClientID = apikeys?.spotifyClientID ?: ""
        spotifyClientSecret = apikeys?.spotifyClientSecret ?: ""
        geniusAccessToken = apikeys?.geniusAccessToken ?: ""
    }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.align(Alignment.TopStart).background(MaterialTheme.colorScheme.inverseOnSurface, shape = RoundedCornerShape(128.dp)).padding(horizontal = 20.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.api),
                    contentDescription = "Apis",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { showPopup = !showPopup }
                        .size(64.dp)
                )
                Text("APIs", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "ProfileSettings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { navController.navigate("profile_view") }
                        .size(64.dp)
                )
                Text("Profiles", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { navController.navigate("settings") }
                        .size(64.dp)
                )
                Text("Settings", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
        IconButton(
            onClick = {savedGame = null; navController.navigate("game_screen") },
            modifier = Modifier
            .size(128.dp)
            .align(Alignment.Center)) {
            Icon(
                painter = painterResource(id = R.drawable.play),
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.Center)
            )
        }

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
                    .verticalScroll(rememberScrollState())
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
                    value = geniusAccessToken,
                    onValueChange = { geniusAccessToken = it },
                    label = {
                        Text(
                            "Genius Access Token",
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
                        geniusAccessToken = if(geniusAccessToken.isEmpty()) null else geniusAccessToken,
                    )
                            showPopup= false
                        apikeys?.let{ saveAPI(it, context) }
                        InitializeSp(context, apikeys!!)
                        initializeGenius(context, apikeys!!)
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