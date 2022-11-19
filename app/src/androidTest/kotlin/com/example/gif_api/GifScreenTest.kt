package com.example.gif_api

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.gif_api.gifs_screen.theme.ui.GifApiTheme
import com.example.gif_api.gifs_screen.ui.gif.*
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class GifScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var gifSearchListScreen: GifSearchListScreen

    @Before
    fun setUp() {
        gifSearchListScreen = GifSearchListScreen(composeRule)
        composeRule.setContent {
            GifApiTheme {
                GifSearchScreen()
            }
        }
    }

    @Test
    fun sample() {
        gifSearchListScreen.writeTextInput()
        val firstItemTagBeforeDelete = gifSearchListScreen.listItems[0].tag
        val secondItemTagBeforeDelete = gifSearchListScreen.listItems[1].tag
        gifSearchListScreen.listItems.first().deleteButton.performClick()
        gifSearchListScreen.clearTextInput()
        gifSearchListScreen.writeTextInput()
        val firstItemTagAfterDelete = gifSearchListScreen.listItems[0].tag
        assertThat(firstItemTagAfterDelete).isNotEqualTo(firstItemTagBeforeDelete)
        assertThat(firstItemTagAfterDelete).isEqualTo(secondItemTagBeforeDelete)
    }
}

