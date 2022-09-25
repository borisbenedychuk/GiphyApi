package com.example.natifetesttask.data.datasources.gif

import com.example.natifetesttask.data.db.entities.GifEntity
import com.example.natifetesttask.data.db.entities.QueryInfoEntity
import com.example.natifetesttask.data.remote.responses.GifDataResponse
import com.example.natifetesttask.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface RemoteGifDatasource {
    suspend fun getGifs(
        query: String,
        limit: Int,
        offset: Int
    ): Result<GifDataResponse>
}

interface CacheGifDatasource {

    suspend fun getGifs(query: String, pages: List<Int>): Flow<List<GifEntity>>

    suspend fun saveGifs(entities: List<GifEntity>)

    suspend fun getQueryInfo(query: String): QueryInfoEntity?

    suspend fun saveQueryInfo(queryInfoEntity: QueryInfoEntity)

    suspend fun addGifToBlacklist(id: String)

    suspend fun getBlacklistIds(): List<String>
}

