package com.example.natifetesttask.presentation.models.gif

sealed interface GifSearchAction {
    class NewQuery(val query: String) : GifSearchAction
    class NewCurrentItem(val id: String) : GifSearchAction
    class BoundsReached(val signal: BoundSignal) : GifSearchAction
    class DeleteItem(val id: String) : GifSearchAction
    class NavigateToPager(val info: TransitionInfo) : GifSearchAction
    object NavigateToGrid : GifSearchAction
    object RetryLoad : GifSearchAction
}