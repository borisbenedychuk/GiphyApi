package com.example.gif_api.data.repository

import com.example.gif_api.data.datasources.gif.CacheGifDatasource
import com.example.gif_api.data.datasources.gif.RemoteGifDatasource
import com.example.gif_api.data.datasources.gif_info.CacheGifInfoDatasource
import com.example.gif_api.data.db.entities.BlackListEntity
import com.example.gif_api.data.db.entities.GifEntity
import com.example.gif_api.data.db.entities.QueryInfoEntity
import com.example.gif_api.domain.gif.repository.GifRepository
import com.example.gif_api.domain.utils.successResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class GifRepositoryTest : BaseGifRepositoryTest() {

    @Test
    fun `test get pages`() {
        val cachedGifDatasource = mock<CacheGifDatasource> {
            on { getGifs(QUERY, PAGES) } doReturn flowOf(entities)
        }
        val repository = mockedRepository(cacheGifDatasource = cachedGifDatasource)
        runTest {
            val result = repository.getPages(QUERY, PAGES).first()
            val expected = entities.map { it.asGifModel() }
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun `test add gif to blacklist`() {
        val testId = "1"
        val gifsList = entities.toMutableList()
        val blackList = mutableListOf<BlackListEntity>()
        val cacheGifDatasource = mock<CacheGifDatasource> {
            onBlocking { deleteGifById(testId) }.then { gifsList.removeIf { it.id == testId } }
        }
        val cacheGifInfoDatasource = mock<CacheGifInfoDatasource> {
            onBlocking { addGifToBlacklist(testId) }.then { blackList.add(BlackListEntity(testId)) }
        }
        val repository = mockedRepository(
            cacheGifDatasource = cacheGifDatasource,
            cacheGifInfoDatasource = cacheGifInfoDatasource,
        )
        runTest {
            repository.addGifToBlackList(testId)
            assertThat(gifsList.filter { it.id == testId }).isEmpty()
            assertThat(blackList.filter { it.id == testId }).hasSize(1)
        }
    }

    @get:Rule
    val deleteOldTestRule = DeleteOldTestRule()

    @Test
    @GifRepositoryDeleteOldTest(timeSinceLastQuery = MORE_THEN_FIVE_HOURS)
    fun `test delete old data (data is old)`() {
        runTest {
            deleteOldTestRule.repository.deleteOldData()
            assertThat(deleteOldTestRule.entitiesList).isEmpty()
            assertThat(deleteOldTestRule.infoEntityList).isEmpty()
        }
    }

    @Test
    @GifRepositoryDeleteOldTest(timeSinceLastQuery = LESS_THEN_FIVE_HOURS)
    fun `test delete old data (data is fresh)`() {
        runTest {
            deleteOldTestRule.repository.deleteOldData()
            assertThat(deleteOldTestRule.entitiesList).isNotEmpty()
            assertThat(deleteOldTestRule.infoEntityList).isNotEmpty()
        }
    }

    @Test
    fun `test is cache fresh (fresh)`() {
        val repository = mockedRepositoryForIsCacheFreshTesting(isFresh = true)
        runTest {
            assertThat(repository.isGifCacheFresh(QUERY)).isTrue()
        }
    }

    @Test
    fun `test is cache fresh (not fresh)`() {
        val repository = mockedRepositoryForIsCacheFreshTesting(isFresh = false)
        runTest {
            assertThat(repository.isGifCacheFresh(QUERY)).isFalse()
        }
    }

    private fun mockedRepositoryForIsCacheFreshTesting(isFresh: Boolean): GifRepository {
        val remoteGifDatasource = mock<RemoteGifDatasource> {
            onBlocking { getGifs(QUERY, 1, 0) } doReturn successResult(
                gifDataResponse(responses.take(1))
            )
        }
        val cacheGifDatasource = mock<CacheGifDatasource> {
            onBlocking { getFirstGif(QUERY) } doReturn if (isFresh) entities.first() else entities[1]
        }
        return mockedRepository(
            cacheGifDatasource = cacheGifDatasource,
            remoteGifDatasource = remoteGifDatasource,
        )
    }

    inner class DeleteOldTestRule : TestRule {

        var infoEntityList: MutableList<QueryInfoEntity> = mock()
            private set
        var entitiesList: MutableList<GifEntity> = mock()
            private set
        var repository: GifRepository = mock()
            private set

        private var infoEntity: QueryInfoEntity? = null
        private val targetQuery get() = infoEntity!!.query

        private val cacheGifInfoDatasource
            get() = mock<CacheGifInfoDatasource> {
                onBlocking { getQueryInfoEntities() } doReturn infoEntityList
                onBlocking { deleteQueryInfoEntities(targetQuery) }.then {
                    infoEntityList.removeIf { it.query == targetQuery }
                }
            }

        private val cacheGifDatasource
            get() = mock<CacheGifDatasource> {
                onBlocking { deleteGifs(targetQuery) }.then {
                    entitiesList.removeIf { it.query == targetQuery }
                }
            }


        override fun apply(base: Statement, description: Description): Statement =
            object : Statement() {
                override fun evaluate() {
                    val annotation =
                        description.annotations.filterIsInstance<GifRepositoryDeleteOldTest>()
                    if (annotation.isNotEmpty()) setUp(annotation.first().timeSinceLastQuery)
                    base.evaluate()
                }
            }

        private fun infoEntity(lastQueryTime: Long = System.currentTimeMillis()): QueryInfoEntity =
            QueryInfoEntity(
                query = QUERY,
                totalSize = PAGE_SIZE * PAGES.size,
                totalPages = PAGES.size,
                cachedPages = PAGES.size,
                lastQueryTime = lastQueryTime
            )

        private fun setUp(timeSinceLastQuery: Long) {
            infoEntity = infoEntity(System.currentTimeMillis() - timeSinceLastQuery)
            infoEntityList = mutableListOf(infoEntity!!)
            entitiesList = entities.toMutableList()
            repository = mockedRepository(
                cacheGifDatasource = cacheGifDatasource,
                cacheGifInfoDatasource = cacheGifInfoDatasource,
            )
        }
    }

    private annotation class GifRepositoryDeleteOldTest(val timeSinceLastQuery: Long)
}





