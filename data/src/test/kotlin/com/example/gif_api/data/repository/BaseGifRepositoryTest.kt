package com.example.gif_api.data.repository

import com.example.gif_api.data.datasources.gif.CacheGifDatasource
import com.example.gif_api.data.datasources.gif.RemoteGifDatasource
import com.example.gif_api.data.datasources.gif_info.CacheGifInfoDatasource
import com.example.gif_api.data.db.entities.GifEntity
import com.example.gif_api.data.gif.GifRepositoryImpl
import com.example.gif_api.data.remote.responses.GifDataResponse
import com.example.gif_api.domain.gif.repository.GifRepository
import org.mockito.kotlin.mock

abstract class BaseGifRepositoryTest {

    protected fun mockedRepository(
        cacheGifDatasource: CacheGifDatasource = mock(),
        remoteGifDatasource: RemoteGifDatasource = mock(),
        cacheGifInfoDatasource: CacheGifInfoDatasource = mock()
    ): GifRepository =
        GifRepositoryImpl(remoteGifDatasource, cacheGifDatasource, cacheGifInfoDatasource)

    protected fun gifDataResponse(
        gifs: List<GifDataResponse.GifResponse> = responses,
    ) = GifDataResponse(
        gifs = gifs,
        pagination = GifDataResponse.Pagination(PAGE_SIZE * 3 + 1)
    )

    protected val entities: List<GifEntity>
        get() = List(PAGE_SIZE * PAGES.size) {
            GifEntity(
                id = it.toString(),
                page = it / PAGE_SIZE,
                originalUrl = "url$it",
                smallUrl = "smallUrl$it",
                title = it.toString(),
                query = QUERY,
            )
        }

    protected val responses: List<GifDataResponse.GifResponse>
        get() = entities.map {
            GifDataResponse.GifResponse(
                id = it.id,
                title = it.title,
                images = GifDataResponse.GifResponse.Images(
                    original = GifDataResponse.GifResponse.UrlHolder(it.originalUrl),
                    small = GifDataResponse.GifResponse.UrlHolder(it.smallUrl),
                )
            )
        }

    protected companion object {
        const val QUERY = "query"
        const val PAGE_SIZE = 25
        const val MORE_THEN_FIVE_HOURS: Long = 5 * 60 * 60 * 1000 + 1
        const val LESS_THEN_FIVE_HOURS: Long = 2 * 60 * 60 * 1000
        val PAGES = List(3) { it }
    }
}