package com.example.gif_apigifs_screen.presentation.models.gif

sealed interface GifSearchAction {
    class NewQuery(val query: String) : GifSearchAction
    class NewCurrentItem(val id: String) : GifSearchAction
    class BoundsReached(val signal: BoundSignal) : GifSearchAction
    class DeleteItem(val id: String) : GifSearchAction
    class NavigateToPager(val info: ListPositionInfo) : GifSearchAction
    object NavigateToGrid : GifSearchAction
    object RetryLoad : GifSearchAction
}