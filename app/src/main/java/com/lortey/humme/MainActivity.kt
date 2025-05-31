package com.lortey.humme

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lortey.cardflare.ui.theme.HumMeTheme
import com.lortey.humme.ui.theme.EditPlaylist
import com.lortey.humme.ui.theme.EditProfile
import com.lortey.humme.ui.theme.EndScreen
import com.lortey.humme.ui.theme.GameScreen
import com.lortey.humme.ui.theme.MainMenuRender

import com.lortey.humme.ui.theme.PlaylistScreen
import com.lortey.humme.ui.theme.ProfileScreen
import com.lortey.humme.ui.theme.SettingsMenu
import com.lortey.humme.ui.theme.apikeys

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkStoragePermissions()
        apikeys = loadAPI(applicationContext)

        InitializeSp(applicationContext, apikeys!!)
        initializeGenius(applicationContext, apikeys!!)

        loadSettings(context = applicationContext)

        enableEdgeToEdge()
        setContent {
            HumMeTheme {
                val navController = rememberNavController()
                val colorScheme = MaterialTheme.colorScheme
                Log.d("ThemeDebug", MaterialTheme.colorScheme.primary.toString())
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "main_menu"
                    ) {
                        //home screen
                        composable("main_menu") {
                            MainMenuRender(
                                navController = navController,
                                context = LocalContext.current
                            )
                        }
                        //playlist manager screen
                        composable("playlists") {
                            PlaylistScreen(
                                navController = navController,
                                context = LocalContext.current
                            )
                        }
                        //Profile browsing
                        composable("profile_view"){
                            ProfileScreen(context = LocalContext.current,
                                navController = navController)
                        }
                        //Settings
                        composable("settings"){
                            SettingsMenu(context = LocalContext.current,
                                navController = navController)
                        }
                        //Profile Editing screen
                        composable("edit_profile"){
                            EditProfile(context = LocalContext.current,
                                navController = navController)
                        }
                        //Playlist Editing screen
                        composable("edit_playlist"){
                            EditPlaylist(context = LocalContext.current,
                                navController = navController)
                        }
                        //Game Screen
                        composable("game_screen"){
                            GameScreen(context = LocalContext.current,
                                navController = navController)
                        }
                        //Sum up screen
                        composable("end_screen"){
                            EndScreen(context = LocalContext.current,
                                navController = navController)
                        }
                    }
                }
            }
        }
    }
    private fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                false
            }
        } else {
            true
        }
    }
}
