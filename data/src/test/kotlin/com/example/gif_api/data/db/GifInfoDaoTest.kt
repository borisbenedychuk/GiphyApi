package com.example.gif_api.data.db

import androidx.room.Room
import com.example.gif_api.data.db.dao.GifInfoDao
import com.example.gif_api.data.db.entities.BlackListEntity
import com.example.gif_api.data.db.entities.QueryInfoEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GifInfoDaoTest {

    private lateinit var gifDao: GifInfoDao

    private lateinit var queryInfo: QueryInfoEntity

    @Before
    fun setUp() {
        val room = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            AppDB::class.java
        ).build()
        gifDao = room.gifInfoDao
        queryInfo = QueryInfoEntity(
            query = QUERY,
            totalSize = PAGE_SIZE * TOTAL_PAGES,
            totalPages = TOTAL_PAGES,
            cachedPages = CACHED_PAGES,
            lastQueryTime = System.currentTimeMillis(),
        )
        runTest { gifDao.saveQueryInfo(queryInfo) }
    }

    @Test
    fun `test get query info`() = runTest {
        val queryInfoEntity = gifDao.getQueryInfoEntity(QUERY)
        assertThat(queryInfoEntity).isEqualTo(queryInfo)
    }

    @Test
    fun `test delete query info`() = runTest {
        gifDao.deleteQueryInfoEntities(QUERY)
        val queryInfoEntity = gifDao.getQueryInfoEntity(QUERY)
        assertThat(queryInfoEntity).isNull()
    }

    @Test
    fun `test get query info entities`() = runTest {
        val queryInfoEntities = gifDao.getQueryInfoEntities()
        assertThat(queryInfoEntities).hasSize(1)
        assertThat(queryInfoEntities.first()).isEqualTo(queryInfo)
    }

    @Test
    fun `test blacklist`() = runTest {
        assertThat(gifDao.getBlacklistIds()).isEmpty()
        gifDao.addIdToBlacklist(BlackListEntity(TEST_ID))
        val blacklist = gifDao.getBlacklistIds()
        assertThat(blacklist).hasSize(1)
        assertThat(blacklist.first()).isEqualTo(TEST_ID)
    }

    companion object {
        private const val PAGE_SIZE = 25
        private const val TOTAL_PAGES = 4
        private const val CACHED_PAGES = 3
        private const val QUERY = "query"
        private const val TEST_ID = "TEST"
    }
}