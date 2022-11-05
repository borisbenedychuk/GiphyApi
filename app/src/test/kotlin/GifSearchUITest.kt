import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import coil.ImageLoader
import com.example.core_ui.getDependencies
import com.example.gif_api.gifs_screen.MainActivity
import com.example.gif_api.gifs_screen.models.gif.GifItem
import com.example.gif_api.gifs_screen.models.gif.GifSearchState
import com.example.gif_api.gifs_screen.ui.gif.*
import com.example.gif_apigifs_screen.presentation.ui.gif.*
import com.example.gif_api.gifs_screen.ui.gif.di.GifSearchDependencies
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class GifSearchUITest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var imageLoader: ImageLoader

    private lateinit var activityController: ActivityController<MainActivity>

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
        val dependencies: GifSearchDependencies = context.getDependencies()
        imageLoader = dependencies.imageLoader
        activityController = Robolectric.buildActivity(MainActivity::class.java).create()
    }

    @After
    fun tearDown() {
        activityController.close()
    }

    @Test
    fun `test state query`() {
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
    fun `test list landscape presentation`() {
        activityController.changeOrientation(portrait = false)
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
    fun `test list portrait presentation`() {
        activityController.changeOrientation(portrait = true)
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
    fun `test pager portrait presentation`() {
        activityController.changeOrientation(portrait = true)
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
    fun `test pager landscape presentation`() {
        activityController.changeOrientation(portrait = false)
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

    @Test
    fun `test list footer presentation`() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(query = QUERY, showFooter = true),
                onNewAction = {},
            )
        }
        composeRule
            .onNodeWithTag(LIST_PORTRAIT_TAG)
            .onChildAt(0)
            .assert(hasTestTag(FOOTER_TAG))
    }

    @Test
    fun `test list retry presentation`() {
        composeRule.setContent {
            GifSearchUI(
                imageLoader = imageLoader,
                state = GifSearchState(
                    query = QUERY,
                    errorMsg = ERROR,
                    showFooter = true
                ),
                onNewAction = {},
            )
        }
        composeRule
            .onNodeWithTag(LIST_PORTRAIT_TAG)
            .onChildAt(0)
            .assert(hasTestTag(RETRY_TAG))
    }

    @Test
    fun `test global loading`() {
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

// Call before setContent
private fun ActivityController<*>.changeOrientation(portrait: Boolean = true) =
    configurationChange(
        Configuration().apply {
            orientation = if (portrait) ORIENTATION_PORTRAIT else ORIENTATION_LANDSCAPE
        }
    )

val context: Context
    get() = RuntimeEnvironment.getApplication()