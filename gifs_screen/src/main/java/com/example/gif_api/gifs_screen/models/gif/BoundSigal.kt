package com.example.gif_api.gifs_screen.models.gif

enum class BoundSignal {
    TOP_REACHED,
    BOTTOM_REACHED,
    NONE;

    val isBoundReached: Boolean get() = this == TOP_REACHED || this == BOTTOM_REACHED
}
