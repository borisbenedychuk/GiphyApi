package com.example.natifetesttask.data.datasources.gif

import com.example.natifetesttask.data.db.entities.GifEntity
import com.example.natifetesttask.data.remote.responses.GifDataResponse
import com.example.natifetesttask.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface RemoteGifDatasource {
    suspend fun getGifs(
        query: String,
        limit: Int,
        offset: Int
    ): Result<List<GifDataResponse.GifResponse>>
}

interface CacheGifDatasource {

    fun getGifsFlow(query: String): Flow<List<GifEntity>>

    suspend fun saveGifs(entities: List<GifEntity>)
}

