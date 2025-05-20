package com.lortey.humme

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
                    }
                }
            }
        }
    }
}
