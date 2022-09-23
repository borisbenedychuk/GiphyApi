package com.example.natifetesttask.data.repositories

import com.example.natifetesttask.data.datasources.gif.CacheGifDatasource
import com.example.natifetesttask.data.datasources.gif.RemoteGifDatasource
import com.example.natifetesttask.domain.model.GifModel
import com.example.natifetesttask.domain.repository.GifRepository
import com.example.natifetesttask.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GifRepositoryImpl @Inject constructor(
    private val remote: RemoteGifDatasource,
    private val cache: CacheGifDatasource,
) : GifRepository {

    override suspend fun getGifs(
        query: String,
        limit: Int,
        offset: Int,
    ): Result<Unit> = when (val result = remote.getGifs(query, limit, offset)) {
        is Result.Success -> {
            val entities = result.data?.map { response -> response.asGifEntity(query) }.orEmpty()
            cache.saveGifs(entities)
            Result.Success(Unit)
        }
        is Result.Error -> result
    }

    override fun observeGifs(query: String): Flow<List<GifModel>> =
        cache.getGifsFlow(query).map { list -> list.map { it.asGifModel() } }
}