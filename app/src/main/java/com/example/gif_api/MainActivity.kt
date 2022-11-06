package com.example.gif_api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gif_api.gifs_screen.theme.ui.GifApiTheme
import com.example.gif_api.gifs_screen.ui.gif.GifSearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GifApiTheme {
                GifSearchScreen()
            }
        }
    }
}
