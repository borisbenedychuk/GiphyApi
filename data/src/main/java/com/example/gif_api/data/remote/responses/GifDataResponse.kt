package com.example.gif_api.data.remote.responses

import com.example.gif_api.data.db.entities.GifEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GifDataResponse(
    @SerialName("data")
    val gifs: List<GifResponse>,
    @SerialName("pagination")
    val pagination: Pagination,
) {

    @Serializable
    data class Pagination(
        @SerialName("total_count")
        val totalCount: Int,
    )

    @Serializable
    data class GifResponse(
        @SerialName("id")
        val id: String,
        @SerialName("title")
        val title: String,
        @SerialName("images")
        val images: Images,
    ) {

        @Serializable
        data class Images(
            @SerialName("original")
            val original: UrlHolder,
            @SerialName("fixed_height_downsampled")
            val small: UrlHolder,
        )

        @Serializable
        data class UrlHolder(
            @SerialName("url")
            val url: String,
        )

        fun asGifEntity(query: String, page: Int) = GifEntity(
            id = id,
            title = title,
            query = query,
            page = page,
            originalUrl = images.original.url,
            smallUrl = images.small.url,
        )
    }
}