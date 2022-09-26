package com.example.natifetesttask.data.repositories.gif

import com.example.natifetesttask.data.datasources.gif.CacheGifDatasource
import com.example.natifetesttask.data.datasources.gif.RemoteGifDatasource
import com.example.natifetesttask.data.db.entities.QueryInfoEntity
import com.example.natifetesttask.data.remote.responses.GifDataResponse
import com.example.natifetesttask.domain.model.gif.GifsPagesModel
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

    override suspend fun deleteOldData() {
        val currentTime = System.currentTimeMillis()
        val queryInfoEntities = cache.getQueryInfoEntities().filter {
            currentTime - it.lastQueryTime > FIVE_HOURS
        }
        cache.clearQueryData(queryInfoEntities.map { it.query })
    }

    override suspend fun addGifToBlackList(id: String) = cache.addGifToBlacklist(id)

    override suspend fun getPages(
        query: String,
        requestedPage: Int
    ): Result<Flow<GifsPagesModel>> {
        val queryInfo = cache.getQueryInfoEntity(query)
        val result = when {
            queryInfo == null -> initialLoad(query)
            requestedPage == 0 -> refreshLoad(query, requestedPage, queryInfo)
            else -> regularLoad(query, requestedPage, queryInfo)
        }
        return when (result) {
            is Result.Error -> result
            is Result.Success -> {
                val pages = List(3) { i -> requestedPage - 1 + i }.filter { page -> page >= 0 }
                Result.Success(
                    cache.getGifs(query, pages).map { entities ->
                        GifsPagesModel(
                            isFinished = result.data,
                            models = entities.map { e -> e.asGifModel() },
                        )
                    }
                )
            }
        }
    }

    private suspend fun refreshLoad(
        query: String,
        currentPage: Int,
        currentQueryInfoEntity: QueryInfoEntity,
    ): Result<Boolean> {
        val result = remote.getGifs(
            query = query,
            limit = 1,
            offset = 0,
        )
        if (result is Result.Success) {
            val remoteResult = result.data.gifs.firstOrNull()
            val cacheResult = cache.getFirstGif(query)
            return when {
                remoteResult == null || cacheResult == null ->
                    regularLoad(query, currentPage, currentQueryInfoEntity)
                remoteResult.id == cacheResult.id -> {
                    regularLoad(query, currentPage, currentQueryInfoEntity)
                }
                else -> {
                    cache.clearQueryData(listOf(query))
                    initialLoad(query)
                }
            }
        } else {
            return regularLoad(query, currentPage, currentQueryInfoEntity)
        }
    }

    private suspend fun initialLoad(
        query: String,
    ): Result<Boolean> {
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

    private suspend fun regularLoad(
        query: String,
        loadPage: Int,
        currentQueryInfo: QueryInfoEntity,
    ): Result<Boolean> {
        val allPagesInCache = loadPage <= currentQueryInfo.cachedPages - 1
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
            Result.Success(currentQueryInfo.cachedPages == currentQueryInfo.totalPages)
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
                Result.Success(cachePages == totalPages)
            }
            is Result.Error -> result
        }
}
