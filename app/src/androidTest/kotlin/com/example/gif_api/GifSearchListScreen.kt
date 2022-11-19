package com.example.gif_api

import android.content.res.Configuration
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.example.gif_api.gifs_screen.ui.gif.ITEM_DELETE_TAG
import com.example.gif_api.gifs_screen.ui.gif.LIST_LANDSCAPE_TAG
import com.example.gif_api.gifs_screen.ui.gif.LIST_PORTRAIT_TAG
import com.example.gif_api.gifs_screen.ui.gif.QUERY_TAG

class GifSearchListScreen(private val composeRule: ComposeTestRule) {

    var screenOrientation = Configuration.ORIENTATION_PORTRAIT
    val queryTextNode get() = composeRule.onNodeWithTag(QUERY_TAG)
    val list get() = composeRule.onNodeWithTag(listTag)

    val listItems: List<GifSearchListItemNode>
        get() {
            val listNode = composeRule.onNodeWithTag(listTag)
            return List(listNode.onChildren().fetchSemanticsNodes().size) { i ->
                GifSearchListItemNode(listNode.onChildAt(i))
            }
        }

    private val listVisible: Boolean
        get() {
            val listNodes = composeRule.onAllNodesWithTag(listTag)
            return listNodes.fetchSemanticsNodes().isNotEmpty()
        }

    private val listTag: String
        get() = if (screenOrientation == Configuration.ORIENTATION_PORTRAIT)
            LIST_PORTRAIT_TAG else LIST_LANDSCAPE_TAG

    fun clearTextInput() {
        queryTextNode.performTextInput("")
        composeRule.waitUntil(TIMEOUT) { !listVisible }
    }

    fun writeTextInput(text: String = "query", waitTillItemsLoaded: Boolean = true) {
        queryTextNode.performTextInput(text)
        composeRule.waitUntil(TIMEOUT) { listVisible }
        if (waitTillItemsLoaded) composeRule.waitUntil(TIMEOUT) { listItems.firstOrNull()?.hasDeleteButton == true }
    }

    companion object {
        private const val TIMEOUT = 30_000L
    }
}

class GifSearchListItemNode(private val itemInteraction: SemanticsNodeInteraction) {
    val tag: String
        get() = itemInteraction.fetchSemanticsNode().config[SemanticsProperties.TestTag]
    val deleteButton: SemanticsNodeInteraction
        get() = itemInteraction.childWithTag(ITEM_DELETE_TAG)!!
    val hasDeleteButton: Boolean
        get() = itemInteraction.childWithTag(ITEM_DELETE_TAG) != null
}

fun SemanticsNodeInteraction.childWithTag(testTag: String) =
    onChildren().filter(hasTestTag(testTag))
        .run { if (fetchSemanticsNodes().isEmpty()) null else onFirst() }