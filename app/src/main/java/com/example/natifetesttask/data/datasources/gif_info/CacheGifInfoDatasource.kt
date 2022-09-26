package com.example.natifetesttask.data.datasources.gif_info

import com.example.natifetesttask.data.db.entities.QueryInfoEntity

interface CacheGifInfoDatasource {

    suspend fun getQueryInfoEntity(query: String): QueryInfoEntity?

    suspend fun getQueryInfoEntities(): List<QueryInfoEntity>

    suspend fun saveQueryInfo(queryInfoEntity: QueryInfoEntity)

    suspend fun deleteQueryInfoEntities(vararg queries: String)

    suspend fun addGifToBlacklist(id: String)

    suspend fun getBlacklistIds(): List<String>
}