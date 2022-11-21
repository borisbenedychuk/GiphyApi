package com.example.gif_api.gifs_screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import coil.ImageLoader
import com.example.core_ui.LIST_LANDSCAPE_TAG
import com.example.core_ui.LIST_PORTRAIT_TAG
import com.example.core_ui.PAGER_LANDSCAPE_TAG
import com.example.core_ui.PAGER_PORTRAIT_TAG
import com.example.gif_api.gifs_screen.models.gif.GifItem
import com.example.gif_api.gifs_screen.models.gif.GifSearchState
import com.example.gif_api.gifs_screen.ui.gif.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class GifSearchUIConfigurationTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var imageLoader: ImageLoader

    private val gifItems: List<GifItem>
        get() = List(30) {
            GifItem(
                id = it.toString(),
                title = it.toString(),
                originalUrl = "url$it",
                smallUrl = "smallUrl$it",
            )
        }

    @Before
    fun setUp() {
        imageLoader = ImageLoader(ApplicationProvider.getApplicationContext())
    }

    @Test
    @Config(qualifiers = "land")
    fun `test list landscape presentation`() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(
                    query = QUERY,
                ),
                onNewAction = {},
            )
        }
        composeRule.onNodeWithTag(LIST_LANDSCAPE_TAG).assertExists()
    }

    @Test
    @Config(qualifiers = "port")
    fun `test list portrait presentation`() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(query = QUERY),
                onNewAction = {},
            )
        }
        composeRule.onNodeWithTag(LIST_PORTRAIT_TAG).assertExists()
    }

    @Test
    @Config(qualifiers = "port")
    fun `test pager portrait presentation`() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(
                    query = QUERY,
                    isDetailsScreen = true,
                    items = gifItems
                ),
                onNewAction = {},
            )
        }
        composeRule.onNodeWithTag(PAGER_PORTRAIT_TAG).assertExists()
    }

    @Test
    @Config(qualifiers = "land")
    fun `test pager landscape presentation`() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(
                    query = QUERY,
                    isDetailsScreen = true,
                    items = gifItems
                ),
                onNewAction = {},
            )
        }
        composeRule.onNodeWithTag(PAGER_LANDSCAPE_TAG).assertExists()
    }

    companion object {
        private const val QUERY = "query"
    }
}