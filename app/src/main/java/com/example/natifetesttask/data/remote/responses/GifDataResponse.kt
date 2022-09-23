package com.example.natifetesttask.data.remote.responses

import com.example.natifetesttask.data.db.entities.GifEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GifDataResponse(
    @SerialName("data")
    val gifs: List<GifResponse>
) {

    @Serializable
    data class GifResponse(
        @SerialName("id")
        val id: String? = null,
        @SerialName("title")
        val title: String? = null,
        @SerialName("images")
        val images: Images? = null,
    ) {

        @Serializable
        data class Images(
            @SerialName("original")
            val original: Original? = null
        )

        @Serializable
        data class Original(
            val url: String? = null
        )

        fun asGifEntity(query: String) = GifEntity(
            id = id.orEmpty(),
            gifUrl = images?.original?.url.orEmpty(),
            title = title.orEmpty(),
            query = query,
        )
    }
}