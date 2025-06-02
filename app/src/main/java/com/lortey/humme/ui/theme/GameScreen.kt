package com.lortey.humme.ui.theme

import android.content.Context
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lortey.humme.AppSettings
import com.lortey.humme.Playlist
import com.lortey.humme.R
import com.lortey.humme.Track
import com.lortey.humme.getActiveTracks
import com.lortey.humme.getLyrics
import com.lortey.humme.getNextSong
import com.lortey.humme.getPreviousSong
import com.lortey.humme.loadProfiles
import com.lortey.humme.rateSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class savedGameInstance(
    var tracksToPick :MutableList<Track>,
    var previousSongs :MutableList<Pair<Track,Boolean>>,
    var currentSong: Track,
    var timeRemaining: Int
)
public var savedGame:savedGameInstance? = null
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(context: Context, navController: NavController) {
    val defaultTime = remember{ if(AppSettings["Infinite Answer Time"]!!.state == true) null else (AppSettings["Answer Time"]!!.state as Float).toInt()}
    var tracksToPick by remember{ mutableStateOf(getActiveTracks(context).toMutableList()) }
    var previousSongs by remember { mutableStateOf(mutableListOf<Pair<Track,Boolean>>()) }
    var currentSong by remember { mutableStateOf(Track("","", mutableListOf(),true)) }
    var timeRemaining by remember { mutableStateOf(defaultTime ?: 0) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var currentColor by remember{ mutableStateOf(Color.Green)}
    val scope = rememberCoroutineScope()
    var showFade by remember{ mutableStateOf(false)}


    var currentJob by remember { mutableStateOf<Job?>(null) }
    if(defaultTime != null){
        LaunchedEffect(currentSong) {
            currentJob?.cancel()
            currentJob = coroutineScope {
                launch {
                    while (timeRemaining > -1) {
                        delay(1000L) // wait 1 second

                        timeRemaining--
                        if (timeRemaining < 0) {
                            previousSongs = rateSong(currentSong, false, previousSongs)
                            val newSong = getNextSong(tracksToPick)
                            if (newSong != null) {
                                currentColor = Color.Red
                                showFade = true
                                scope.launch {
                                    delay(200)
                                    delay(200)
                                    showFade = false
                                }
                                currentSong = newSong.second
                                tracksToPick = newSong.first
                                timeRemaining = defaultTime ?: 0
                                savedGame = savedGameInstance(
                                    tracksToPick,
                                    previousSongs,
                                    currentSong,
                                    timeRemaining
                                )

                            } else {
                                savedGame = savedGameInstance(
                                    tracksToPick,
                                    previousSongs,
                                    currentSong,
                                    timeRemaining
                                )
                                navController.navigate("end_screen")
                            }
                        }
                    }
                }
            }
        }
    }



    LaunchedEffect(Unit) {
        if(savedGame == null) {
            val nextSongValues = getNextSong(tracksToPick)
            if (nextSongValues != null) {
                currentSong = nextSongValues.second
                tracksToPick = nextSongValues.first
            } else {
                navController.popBackStack()
            }
            savedGame = savedGameInstance(
                tracksToPick,
                previousSongs,
                currentSong,
                timeRemaining
            )
        }else{
            tracksToPick = savedGame!!.tracksToPick
            previousSongs = savedGame!!.previousSongs
            currentSong = savedGame!!.currentSong
        }
    }

    LaunchedEffect(currentSong) {
        if(currentSong == null){
            savedGame = savedGameInstance(
                tracksToPick,
                previousSongs,
                currentSong,
                timeRemaining
            )
            navController.navigate("end_screen")
        }
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {//Wrong answer
                        previousSongs = rateSong(currentSong, false, previousSongs)
                        val newSong = getNextSong(tracksToPick)
                        if(newSong != null){
                            currentColor = Color.Red
                            showFade = true
                            scope.launch {
                                delay(200)
                                delay(200)
                                showFade = false
                            }
                            currentSong = newSong.second
                            tracksToPick = newSong.first
                            timeRemaining = defaultTime ?: 0
                            savedGame = savedGameInstance(
                                tracksToPick,
                                previousSongs,
                                currentSong,
                                timeRemaining
                            )

                        }else{
                            savedGame = savedGameInstance(
                                tracksToPick,
                                previousSongs,
                                currentSong,
                                timeRemaining
                            )
                            navController.navigate("end_screen")
                        }

                    },
                    onDoubleTap = { //go to previous
                        val previousData = getPreviousSong(previousSongs)
                        if(previousData != null){
                            tracksToPick.add(currentSong)
                            currentColor = Color.Cyan
                            showFade = true
                            scope.launch {
                                delay(200)
                                delay(200)
                                showFade = false
                            }
                            timeRemaining = defaultTime ?: 0
                            currentSong = previousData.second
                            previousSongs = previousData.first
                            savedGame = savedGameInstance(
                                tracksToPick,
                                previousSongs,
                                currentSong,
                                timeRemaining
                            )

                        }

                    },
                    onLongPress = {//go to previous
                        val previousData = getPreviousSong(previousSongs)
                        if(previousData != null){
                            tracksToPick.add(currentSong)
                            currentColor = Color.Cyan
                            showFade = true
                            scope.launch {
                                delay(200)
                                delay(200)
                                showFade = false
                            }
                            timeRemaining = defaultTime ?: 0
                            currentSong = previousData.second
                            previousSongs = previousData.first
                            savedGame = savedGameInstance(
                                tracksToPick,
                                previousSongs,
                                currentSong,
                                timeRemaining
                            )

                        } }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { println("Drag started") },
                    onDragEnd = { //correct answer
                        previousSongs = rateSong(currentSong, true, previousSongs)
                        val newSong = getNextSong(tracksToPick)
                        if(newSong != null){
                            currentColor = Color.Green
                            showFade = true
                            scope.launch {
                                delay(200)
                                delay(200)
                                showFade = false
                            }
                            timeRemaining = defaultTime ?: 0
                            currentSong = newSong.second
                            tracksToPick = newSong.first
                            savedGame = savedGameInstance(
                                tracksToPick,
                                previousSongs,
                                currentSong,
                                timeRemaining
                            )

                        }else{
                            savedGame = savedGameInstance(
                                tracksToPick,
                                previousSongs,
                                currentSong,
                                timeRemaining
                            )
                            navController.navigate("end_screen")

                        } },
                    onDragCancel = { println("Drag cancelled") },
                    onDrag = { x,y-> }
                )
            }
    ) {
        IconButton(
            onClick = {
                scope.launch {
                    showBottomSheet = true
                    sheetState.expand()

                }
            },
            modifier = Modifier.background(
                shape = RoundedCornerShape(128.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface
            ).padding(10.dp).size(60.dp).align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.lyrics),
                contentDescription = "lyrics",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxSize()
            )
        }


        IconButton(
            onClick = {
                navController.navigate("end_screen")
            },
            modifier = Modifier.background(
                shape = RoundedCornerShape(128.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface
            ).padding(10.dp).size(58.dp).align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.trophy),
                contentDescription = "end",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxSize()
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.7f), verticalArrangement = Arrangement.Center){
            Text(currentSong.name,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier =  Modifier.fillMaxWidth())

            if(defaultTime != null){
                Text("${(timeRemaining - timeRemaining%60)/60}:${if (timeRemaining%60 < 10) "0" else ""}${timeRemaining%60}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }

        }

        Row( horizontalArrangement = Arrangement.End,modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp).fillMaxWidth(0.5f)){
            Text(currentSong.artist.joinToString ("\n" ).replace(",","\n"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }

    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
            showBottomSheet = false
        },
            sheetState = sheetState) {
            LazyColumn {
                item{
                    Text(
                    getLyrics(currentSong.id, context).let{ if(it.isEmpty()) "No Lyrics Added Or Found" else it},
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding((5.dp)))}
            }


        }
    }

    AnimatedVisibility(
        visible = showFade,
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(400))
    ) { val darken = 0.5f
        Box(modifier = Modifier.fillMaxSize().background(color = currentColor.copy(red = currentColor.red *darken,green = currentColor.green *darken, blue = currentColor.blue *darken,))){
            Icon(
                painter = painterResource(id = if(currentColor == Color.Red) R.drawable.wrong else if(currentColor == Color.Green) R.drawable.ok else R.drawable.back),
                contentDescription = "lyrics",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxSize(0.5f).align(Alignment.Center)
            )
        }
    }
}
@Composable
fun EndScreen(context: Context, navController: NavController){
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())){
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.Center)) {
            Icon(
                painter = painterResource(id = R.drawable.trophy),
                contentDescription = "trophy",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxSize(0.3f)
            )
            Text(
                text = "${savedGame!!.previousSongs.count{it.second}}/${savedGame!!.previousSongs.size}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding((5.dp)))
            Button(onClick = {
                navController.clearBackStack("main_menu")
                navController.navigate("main_menu")
            }) {
                Text(
                    text = "Close",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}