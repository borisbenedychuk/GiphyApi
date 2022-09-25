package com.example.natifetesttask.data.datasources.gif

import androidx.room.withTransaction
import com.example.natifetesttask.data.db.AppDB
import com.example.natifetesttask.data.db.dao.GifDao
import com.example.natifetesttask.data.db.entities.BlackListEntity
import com.example.natifetesttask.data.db.entities.GifEntity
import com.example.natifetesttask.data.db.entities.QueryInfoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CacheGifDatasourceImpl @Inject constructor(
    private val db: AppDB,
    private val gifDao: GifDao,
) : CacheGifDatasource {

    override suspend fun getGifs(query: String, pages: List<Int>): Flow<List<GifEntity>> =
        gifDao.getGifs(query, pages)

    override suspend fun saveGifs(entities: List<GifEntity>) =
        gifDao.saveGifs(entities)

    override suspend fun getQueryInfo(query: String) =
        gifDao.getQueryInfo(query)

    override suspend fun saveQueryInfo(queryInfoEntity: QueryInfoEntity) =
        gifDao.saveQueryInfo(queryInfoEntity)

    override suspend fun getBlacklistIds(): List<String> =
        gifDao.getBlacklistIds()

    override suspend fun addGifToBlacklist(id: String) =
        db.withTransaction {
            gifDao.deleteGif(id)
            gifDao.addIdToBlacklist(BlackListEntity(id))
        }
}