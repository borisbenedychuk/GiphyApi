package com.example.gif_apigifs_screen.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gif_apigifs_screen.presentation.theme.ui.GifApiTheme
import com.example.gif_apigifs_screen.presentation.ui.gif.GifSearchScreen

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
