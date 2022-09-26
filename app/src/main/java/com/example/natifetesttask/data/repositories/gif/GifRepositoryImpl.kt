package com.example.natifetesttask.data.repositories.gif

import com.example.natifetesttask.data.datasources.gif.CacheGifDatasource
import com.example.natifetesttask.data.datasources.gif.RemoteGifDatasource
import com.example.natifetesttask.data.db.entities.QueryInfoEntity
import com.example.natifetesttask.data.remote.responses.GifDataResponse
import com.example.natifetesttask.domain.model.gif.GifModel
import com.example.natifetesttask.domain.repository.gif.GifRepository
import com.example.natifetesttask.domain.utils.Result
import com.example.natifetesttask.presentation.ui.gif.PAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val FIVE_HOURS = 1000 * 60 * 60 * 5

class GifRepositoryImpl @Inject constructor(
    private val remote: RemoteGifDatasource,
    private val cache: CacheGifDatasource,
) : GifRepository {

    override suspend fun getPages(query: String, pages: List<Int>): Flow<List<GifModel>> {
        return cache.getGifs(query, pages).map { list -> list.map { it.asGifModel() } }
    }

    override suspend fun isGifCacheFresh(query: String): Boolean {
        val result = remote.getGifs(
            query = query,
            limit = 1,
            offset = 0,
        )
        return if (result is Result.Success) {
            val remoteResult = result.data.gifs.firstOrNull()
            val cacheResult = cache.getFirstGif(query)
            when {
                remoteResult == null -> true
                cacheResult == null -> false
                remoteResult.id == cacheResult.id -> true
                else -> false
            }
        } else {
            true
        }
    }

    override suspend fun deleteOldData() {
        val currentTime = System.currentTimeMillis()
        val queryInfoEntities = cache.getQueryInfoEntities().filter {
            currentTime - it.lastQueryTime > FIVE_HOURS
        }
        cache.clearQueryData(queryInfoEntities.map { it.query })
    }

    override suspend fun addGifToBlackList(id: String) = cache.addGifToBlacklist(id)


    override suspend fun loadInitialPagesAndCheckIfHasMore(
        query: String,
    ): Result<Boolean> {
        cache.clearQueryData(listOf(query))
        val result = remote.getGifs(
            query = query,
            limit = PAGE * 2,
            offset = 0,
        )
        return handleResponse(
            result = result,
            query = query,
            pageResolver = { index -> index / PAGE },
            queryInfoTime = System.currentTimeMillis(),
            cachePagesCount = 2,
        )
    }

    override suspend fun loadPageAndCheckIfHasMore(query: String, page: Int): Result<Boolean> {
        val currentQueryInfo = cache.getQueryInfoEntity(query) ?: return Result.Error()
        val allPagesInCache = page * PAGE <= currentQueryInfo.cachedPages - 1
        return if (!allPagesInCache) {
            val offset = currentQueryInfo.cachedPages * PAGE
            val result = remote.getGifs(
                query = query,
                limit = PAGE,
                offset = offset,
            )
            handleResponse(
                result = result,
                query = query,
                cachePagesCount = currentQueryInfo.cachedPages + 1,
                pageResolver = { currentQueryInfo.cachedPages },
                queryInfoTime = currentQueryInfo.lastQueryTime,
            )
        } else {
            Result.Success(currentQueryInfo.cachedPages < currentQueryInfo.totalPages)
        }
    }

    private suspend fun handleResponse(
        result: Result<GifDataResponse>,
        query: String,
        cachePagesCount: Int,
        pageResolver: (index: Int) -> Int,
        queryInfoTime: Long,
    ): Result<Boolean> =
        when (result) {
            is Result.Success -> {
                val blackList = cache.getBlacklistIds()
                val count = result.data.pagination.totalCount
                val totalPages = count / PAGE + if (count % PAGE != 0) 1 else 0
                val cachePages = if (cachePagesCount > totalPages) totalPages else cachePagesCount
                val newQueryInfoEntity = QueryInfoEntity(
                    query = query,
                    totalSize = count,
                    totalPages = totalPages,
                    cachedPages = cachePages,
                    lastQueryTime = queryInfoTime,
                )
                val entities = result.data.gifs
                    .mapIndexed { index, gif ->
                        val gifPage = pageResolver(index)
                        gif.asGifEntity(query, gifPage)
                    }
                    .filter { it.id !in blackList }
                cache.saveGifs(entities)
                cache.saveQueryInfo(newQueryInfoEntity)
                Result.Success(cachePages < totalPages)
            }
            is Result.Error -> result
        }
}
