package com.lortey.humme

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lortey.cardflare.ui.theme.HumMeTheme
import com.lortey.humme.ui.theme.MainMenuRender

import com.lortey.humme.ui.theme.PlaylistScreen
import com.lortey.humme.ui.theme.apikeys

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkStoragePermissions()
        apikeys = loadAPI(applicationContext)
        InitializeSp(applicationContext, apikeys!!)
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
                        startDestination = "playlists"
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
