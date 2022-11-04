package com.example.gif_api.data.repository

import com.example.gif_api.data.datasources.gif.CacheGifDatasource
import com.example.gif_api.data.datasources.gif.RemoteGifDatasource
import com.example.gif_api.data.datasources.gif_info.CacheGifInfoDatasource
import com.example.gif_api.data.db.entities.QueryInfoEntity
import com.example.gif_api.data.remote.withAssertedCast
import com.example.gif_api.domain.gif.repository.GifRepository
import com.example.gif_api.domain.utils.Result
import com.example.gif_api.domain.utils.successResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class GifRepositoryLoadPageTest : BaseGifRepositoryTest() {

    @Test
    fun `test load pages (page is cached and last one)`() {
        var inserted = false
        val repository = mockedRepositoryForLoadPageTesting(
            cachedSize = 3 * PAGE_SIZE + 1,
            onNewGifsInsertedToCache = { inserted = true }
        )
        runTest {
            val result = repository.loadPageAndCheckIfHasMore(QUERY, 3)
            assertThat(inserted).isFalse()
            withAssertedCast<Result.Success<Boolean>>(result) {
                assertThat(data).isFalse()
            }
        }
    }

    @Test
    fun `test load pages (page is not cached and last one)`() {
        var inserted = false
        val repository = mockedRepositoryForLoadPageTesting(
            cachedSize = 3 * PAGE_SIZE,
            onNewGifsInsertedToCache = { inserted = true }
        )
        runTest {
            val result = repository.loadPageAndCheckIfHasMore(QUERY, 3)
            assertThat(inserted).isTrue()
            withAssertedCast<Result.Success<Boolean>>(result) {
                assertThat(data).isFalse()
            }
        }
    }

    @Test
    fun `test load pages (page is not cached and not last one)`() {
        var inserted = false
        val repository = mockedRepositoryForLoadPageTesting(
            cachedSize = 2 * PAGE_SIZE,
            onNewGifsInsertedToCache = { inserted = true }
        )
        runTest {
            val result = repository.loadPageAndCheckIfHasMore(QUERY, 2)
            assertThat(inserted).isTrue()
            withAssertedCast<Result.Success<Boolean>>(result) {
                assertThat(data).isTrue()
            }
        }
    }

    @Test
    fun `test load initial pages`() {
        val entities = entities.takeLast(PAGE_SIZE).toMutableList()
        val cacheGifInfoDatasource = mock<CacheGifInfoDatasource> {
            onBlocking { getBlacklistIds() } doReturn emptyList()
        }
        val cacheGifDatasource = mock<CacheGifDatasource> {
            onBlocking { deleteGifs(QUERY) } doAnswer { entities.clear() }
            onBlocking { saveGifs(any()) } doAnswer { entities.addAll(it.getArgument(0)); Unit }
        }
        val remoteGifDatasource = mock<RemoteGifDatasource> {
            onBlocking { getGifs(QUERY, PAGE_SIZE * 2, 0) } doReturn
                    successResult(gifDataResponse(gifs = responses.takeLast(PAGE_SIZE * 2)))
        }
        val repository = mockedRepository(
            cacheGifDatasource = cacheGifDatasource,
            remoteGifDatasource = remoteGifDatasource,
            cacheGifInfoDatasource = cacheGifInfoDatasource,
        )
        runTest {
            val result = repository.loadInitialPagesAndCheckIfHasMore(QUERY)
            withAssertedCast<Result.Success<Boolean>>(result) {
                assertThat(data).isTrue()
            }
            assertThat(entities).hasSize(PAGE_SIZE * 2)
            assertThat(entities).isEqualTo(entities.take(PAGE_SIZE * 2))
        }
    }

    private fun mockedRepositoryForLoadPageTesting(
        cachedSize: Int,
        onNewGifsInsertedToCache: () -> Unit,
    ): GifRepository {
        val totalSize = PAGE_SIZE * 3 + 1
        val remoteGifDatasource = mock<RemoteGifDatasource> {
            onBlocking {
                getGifs(
                    QUERY,
                    PAGE_SIZE,
                    cachedSize
                )
            } doReturn successResult(
                gifDataResponse(
                    gifs = responses.takeLast(
                        (totalSize - cachedSize).coerceAtMost(
                            PAGE_SIZE
                        )
                    ),
                )
            )
        }
        val cacheGifDatasource = mock<CacheGifDatasource> {
            onBlocking { saveGifs(any()) } doAnswer { onNewGifsInsertedToCache() }
        }
        val cacheGifInfoDatasource = mock<CacheGifInfoDatasource> {
            onBlocking { getBlacklistIds() } doReturn emptyList()
            onBlocking { getQueryInfoEntity(QUERY) } doReturn QueryInfoEntity(
                QUERY,
                totalSize = totalSize,
                totalPages = kotlin.run {
                    val rest = totalSize % PAGE_SIZE
                    var pages = totalSize / PAGE_SIZE
                    if (rest > 0) pages += 1
                    pages
                },
                cachedPages = kotlin.run {
                    val rest = cachedSize % PAGE_SIZE
                    var pages = cachedSize / PAGE_SIZE
                    if (rest > 0) pages += 1
                    pages
                },
                lastQueryTime = System.currentTimeMillis(),
            )
        }
        return mockedRepository(
            remoteGifDatasource = remoteGifDatasource,
            cacheGifInfoDatasource = cacheGifInfoDatasource,
            cacheGifDatasource = cacheGifDatasource,
        )
    }
}