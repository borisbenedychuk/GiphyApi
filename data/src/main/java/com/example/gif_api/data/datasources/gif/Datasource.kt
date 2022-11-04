package com.example.gif_api.data.datasources.gif

import com.example.gif_api.data.db.entities.GifEntity
import com.example.gif_api.data.remote.responses.GifDataResponse
import com.example.gif_api.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface RemoteGifDatasource {
    suspend fun getGifs(
        query: String,
        limit: Int,
        offset: Int
    ): Result<GifDataResponse>
}

interface CacheGifDatasource {

    fun getGifs(query: String, pages: List<Int>): Flow<List<GifEntity>>

    suspend fun getFirstGif(query: String): GifEntity?

    suspend fun saveGifs(entities: List<GifEntity>)

    suspend fun deleteGifs(vararg queries: String)

    suspend fun deleteGifById(id: String)
}

