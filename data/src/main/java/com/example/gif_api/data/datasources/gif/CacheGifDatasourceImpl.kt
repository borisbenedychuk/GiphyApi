package com.example.gif_api.data.datasources.gif

import com.example.gif_api.data.db.dao.GifDao
import com.example.gif_api.data.db.entities.GifEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CacheGifDatasourceImpl @Inject constructor(
    private val gifDao: GifDao,
) : CacheGifDatasource {

    override fun getGifs(query: String, pages: List<Int>): Flow<List<GifEntity>> =
        gifDao.getGifs(query, pages)

    override suspend fun getFirstGif(query: String): GifEntity? =
        gifDao.getFirstGif(query)

    override suspend fun saveGifs(entities: List<GifEntity>) =
        gifDao.saveGifs(entities)

    override suspend fun deleteGifs(vararg queries: String) =
        gifDao.deleteQueryGifs(*queries)

    override suspend fun deleteGifById(id: String) =
        gifDao.deleteGif(id)
}