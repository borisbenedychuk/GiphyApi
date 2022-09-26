package com.example.natifetesttask.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.natifetesttask.presentation.theme.ui.NatifeTestTaskTheme
import com.example.natifetesttask.presentation.ui.gif.GifSearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        setContent {
            NatifeTestTaskTheme {
                GifSearchScreen()
            }
        }
    }
}
