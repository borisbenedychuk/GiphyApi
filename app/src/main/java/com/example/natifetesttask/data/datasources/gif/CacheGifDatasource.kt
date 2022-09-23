package com.example.natifetesttask.data.datasources.gif

import com.example.natifetesttask.data.db.dao.GifDao
import com.example.natifetesttask.data.db.entities.GifEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CacheGifDatasourceImpl @Inject constructor(
    private val dao: GifDao,
) : CacheGifDatasource {
    override fun getGifsFlow(query: String): Flow<List<GifEntity>> =
        dao.getGifs(query)

    override suspend fun saveGifs(entities: List<GifEntity>) {
        dao.save(entities)
    }
}