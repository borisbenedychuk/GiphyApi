package com.example.natifetesttask.data.repositories.gif

import com.example.natifetesttask.data.datasources.gif.CacheGifDatasource
import com.example.natifetesttask.data.datasources.gif.RemoteGifDatasource
import com.example.natifetesttask.data.db.entities.QueryInfoEntity
import com.example.natifetesttask.data.remote.responses.GifDataResponse
import com.example.natifetesttask.domain.model.gif.GifModel
import com.example.natifetesttask.domain.repository.gif.GifRepository
import com.example.natifetesttask.domain.utils.Result
import com.example.natifetesttask.presentation.ui.gif.PAGE
import kotlinx.coroutines.Dispatchers
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

    override suspend fun getPages(query: String, currentPage: Int): Result<Flow<List<GifModel>>> =
        with(Dispatchers.Default) {
            val queryInfo = cache.getQueryInfoEntity(query)
            val result =
                queryInfo?.let { regularLoad(query, currentPage, it) } ?: initialLoad(query)
            when (result) {
                is Result.Error -> result
                is Result.Success -> {
                    val pages =
                        List(3) {
                            (currentPage - 1 + it).coerceAtLeast(0)
                        }.distinct()
                    Result.Success(
                        cache.getGifs(query, pages).map { list -> list.map { it.asGifModel() } }
                    )
                }
            }
        }

    private suspend fun initialLoad(
        query: String,
    ): Result<*> {
        val result = remote.getGifs(
            query = query,
            limit = PAGE * 3,
            offset = 0,
        )
        return handleResponse(
            result = result,
            query = query,
            cachedPages = 3,
            pageResolver = { index -> ((index + 1) / PAGE).coerceIn(0, 2) },
            queryInfoTime = System.currentTimeMillis()
        )
    }

    private suspend fun regularLoad(
        query: String,
        currentPage: Int,
        currentQueryInfoEntity: QueryInfoEntity,
    ): Result<Unit> {
        val upperBound = currentPage + 2
        val allPagesInCache = upperBound <= currentQueryInfoEntity.cachedPages - 1
        val pageInBounds = currentPage <= currentQueryInfoEntity.totalPages - 1
        return if (!allPagesInCache && pageInBounds) {
            val limit =
                (upperBound - currentQueryInfoEntity.cachedPages + 1).coerceAtMost(
                    currentQueryInfoEntity.totalPages - 1
                ) * PAGE
            val offset = currentQueryInfoEntity.cachedPages * PAGE
            val result = remote.getGifs(
                query = query,
                limit = limit,
                offset = offset,
            )
            handleResponse(
                result = result,
                query = query,
                cachedPages = currentQueryInfoEntity.cachedPages + limit / PAGE,
                pageResolver = { currentQueryInfoEntity.cachedPages + it / PAGE },
                queryInfoTime = currentQueryInfoEntity.lastQueryTime,
            )
        } else {
            Result.Success()
        }
    }

    private suspend fun handleResponse(
        result: Result<GifDataResponse>,
        query: String,
        cachedPages: Int,
        pageResolver: (index: Int) -> Int,
        queryInfoTime: Long,
    ): Result<Unit> = when (result) {
        is Result.Success -> {
            result.data?.let { data ->
                val blackList = cache.getBlacklistIds()
                data.pagination?.totalCount?.let { count ->
                    val totalPages = count / PAGE + if (count % PAGE != 0) 1 else 0
                    val newQueryInfoEntity = QueryInfoEntity(
                        query = query,
                        totalSize = count,
                        totalPages = totalPages,
                        cachedPages = cachedPages,
                        lastQueryTime = queryInfoTime,
                    )
                    cache.saveQueryInfo(newQueryInfoEntity)
                }
                data.gifs?.let { gifs ->
                    val entities = gifs
                        .filterNotNull()
                        .filter { it.id !in blackList }
                        .mapIndexed { index, gif ->
                            val gifPage = pageResolver(index)
                            gif.asGifEntity(query, gifPage)
                        }
                    cache.saveGifs(entities)
                }
            }
            Result.Success()
        }
        is Result.Error -> result
    }
}