package com.example.gif_api.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gif_api.presentation.theme.ui.GifApiTheme
import com.example.gif_api.presentation.ui.gif.GifSearchScreen

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
