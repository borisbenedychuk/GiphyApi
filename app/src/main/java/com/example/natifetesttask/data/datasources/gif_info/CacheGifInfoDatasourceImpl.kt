package com.example.natifetesttask.data.datasources.gif_info

import com.example.natifetesttask.data.db.dao.GifInfoDao
import com.example.natifetesttask.data.db.entities.BlackListEntity
import com.example.natifetesttask.data.db.entities.QueryInfoEntity
import javax.inject.Inject

class CacheGifInfoDatasourceImpl @Inject constructor(
    val dao: GifInfoDao
) : CacheGifInfoDatasource {

    override suspend fun getQueryInfoEntity(query: String) =
        dao.getQueryInfoEntity(query)

    override suspend fun getQueryInfoEntities(): List<QueryInfoEntity> =
        dao.getQueryInfoEntities()

    override suspend fun saveQueryInfo(queryInfoEntity: QueryInfoEntity) =
        dao.saveQueryInfo(queryInfoEntity)

    override suspend fun getBlacklistIds(): List<String> =
        dao.getBlacklistIds()

    override suspend fun addGifToBlacklist(id: String) =
        dao.addIdToBlacklist(BlackListEntity(id))

    override suspend fun deleteQueryInfoEntities(vararg queries: String) =
        dao.deleteQueryInfoEntities(*queries)
}