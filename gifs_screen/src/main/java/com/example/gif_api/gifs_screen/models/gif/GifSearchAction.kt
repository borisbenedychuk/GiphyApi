package com.example.gif_api.gifs_screen.models.gif

sealed interface GifSearchAction {
    class NewQuery(val query: String) : GifSearchAction
    class NewCurrentItem(val id: String) : GifSearchAction
    class BoundsReached(val signal: BoundSignal) : GifSearchAction
    class DeleteItem(val id: String) : GifSearchAction
    class NavigateToPager(val info: ListPositionInfo) : GifSearchAction
    object NavigateToList : GifSearchAction
    object RetryLoad : GifSearchAction
}