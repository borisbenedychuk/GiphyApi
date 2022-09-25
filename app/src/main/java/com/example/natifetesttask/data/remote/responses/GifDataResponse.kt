package com.example.natifetesttask.data.remote.responses

import com.example.natifetesttask.data.db.entities.GifEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GifDataResponse(
    @SerialName("data")
    val gifs: List<GifResponse?>? = null,
    @SerialName("pagination")
    val pagination: Pagination? = null,
) {

    @Serializable
    data class Pagination(
        @SerialName("total_count")
        val totalCount: Int? = null,
    )

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
            val original: UrlHolder? = null,
            @SerialName("preview_gif")
            val small: UrlHolder? = null,
            @SerialName("fixed_height_small_still")
            val preview: UrlHolder? = null,
        )

        @Serializable
        data class UrlHolder(
            @SerialName("url")
            val url: String? = null
        )

        fun asGifEntity(query: String, page: Int) = GifEntity(
            id = id.orEmpty(),
            title = title.orEmpty(),
            query = query,
            page = page,
            originalUrl = images?.original?.url.orEmpty(),
            previewUrl = images?.preview?.url.orEmpty(),
            smallUrl = images?.small?.url.orEmpty(),
        )
    }
}