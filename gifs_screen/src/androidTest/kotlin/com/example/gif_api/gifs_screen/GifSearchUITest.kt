package com.example.gif_api.gifs_screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil.ImageLoader
import com.example.gif_api.gifs_screen.models.gif.GifItem
import com.example.gif_api.gifs_screen.models.gif.GifSearchState
import com.example.gif_api.gifs_screen.ui.gif.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifSearchUITest {

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
    fun testQueryState() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(query = QUERY),
                onNewAction = {},
            )
        }
        composeRule.onNode(hasText(QUERY))
    }

    @Test
    fun testListFooterPresentation() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(
                    query = QUERY,
                    showFooter = true,
                    items = gifItems.take(1),
                ),
                onNewAction = {},
            )
        }
        composeRule
            .onNodeWithTag(LIST_PORTRAIT_TAG)
            .onChildren()
            .onLast()
            .assert(hasTestTag(FOOTER_TAG))
    }

    @Test
    fun testListRetryPresentation() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(
                    query = QUERY,
                    errorMsg = ERROR,
                    showFooter = true,
                    items = gifItems.take(1),
                ),
                onNewAction = {},
            )
        }
        composeRule
            .onNodeWithTag(LIST_PORTRAIT_TAG)
            .onChildren()
            .onLast()
            .assert(hasTestTag(RETRY_TAG))
    }

    @Test
    fun testDeleteButton() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(
                    query = QUERY,
                    showFooter = true,
                    items = listOf(
                        GifItem(
                            id = "1",
                            title = "1",
                            originalUrl = "",
                            smallUrl = "https://media3.giphy.com/media/8FNlmNPDTo2wE/giphy.gif?cid=f8428fcd4avawl4d0yjxn2lcx4b4ypl39y06mdkkvewtjxx4&rid=giphy.gif&ct=g"
                        )
                    ),
                ),
                onNewAction = {},
            )
        }
        composeRule.waitUntil(timeoutMillis = 5000L) {
            composeRule
                .onAllNodesWithTag(DELETE_TAG)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun testGlobalLoading() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(
                    query = QUERY,
                    loading = true,
                ),
                onNewAction = {},
            )
        }
        composeRule
            .onNodeWithTag(GLOBAL_LOADING_TAG)
            .assertExists()
    }

    companion object {
        private const val QUERY = "query"
        private const val ERROR = "error"
    }
}