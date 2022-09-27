package com.example.gif_api.data.repositories.gif

import com.example.gif_api.data.datasources.gif.CacheGifDatasource
import com.example.gif_api.data.datasources.gif.RemoteGifDatasource
import com.example.gif_api.data.datasources.gif_info.CacheGifInfoDatasource
import com.example.gif_api.data.db.entities.QueryInfoEntity
import com.example.gif_api.data.remote.responses.GifDataResponse
import com.example.gif_api.domain.model.gif.GifModel
import com.example.gif_api.domain.repository.gif.GifRepository
import com.example.gif_api.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val FIVE_HOURS = 1000 * 60 * 60 * 5
private const val PAGE = 25

class GifRepositoryImpl @Inject constructor(
    private val remote: RemoteGifDatasource,
    private val gifCache: CacheGifDatasource,
    private val gifInfoCache: CacheGifInfoDatasource,
) : GifRepository {

    override suspend fun getPages(query: String, pages: List<Int>): Flow<List<GifModel>> {
        return gifCache.getGifs(query, pages).map { list -> list.map { it.asGifModel() } }
    }

    override suspend fun isGifCacheFresh(query: String): Boolean {
        val result = remote.getGifs(
            query = query,
            limit = 1,
            offset = 0,
        )
        return if (result is Result.Success) {
            val remoteResult = result.data.gifs.firstOrNull()
            val cacheResult = gifCache.getFirstGif(query)
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
        val queryInfoEntities = gifInfoCache.getQueryInfoEntities().filter {
            currentTime - it.lastQueryTime > FIVE_HOURS
        }
        val queries = queryInfoEntities.map { it.query }.toTypedArray()
        gifInfoCache.deleteQueryInfoEntities(*queries)
        gifCache.deleteGifs(*queries)
    }

    override suspend fun addGifToBlackList(id: String) {
        gifCache.deleteGifById(id)
        gifInfoCache.addGifToBlacklist(id)
    }

    override suspend fun loadInitialPagesAndCheckIfHasMore(
        query: String,
    ): Result<Boolean> {
        gifInfoCache.deleteQueryInfoEntities(query)
        gifCache.deleteGifs(query)
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
        val currentQueryInfo = gifInfoCache.getQueryInfoEntity(query) ?: return Result.Empty
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
                val blackList = gifInfoCache.getBlacklistIds()
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
                gifCache.saveGifs(entities)
                gifInfoCache.saveQueryInfo(newQueryInfoEntity)
                Result.Success(cachePages < totalPages)
            }
            is Result.Error -> result
            is Result.Empty -> result
        }
}
