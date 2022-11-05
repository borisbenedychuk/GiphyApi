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

    private lateinit var gifInfoDao: GifInfoDao
    private lateinit var queryInfo: QueryInfoEntity

    @Before
    fun setUp() {
        val room = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            AppDB::class.java
        ).build()
        gifInfoDao = room.gifInfoDao
        queryInfo = QueryInfoEntity(
            query = QUERY,
            totalSize = PAGE_SIZE * TOTAL_PAGES,
            totalPages = TOTAL_PAGES,
            cachedPages = CACHED_PAGES,
            lastQueryTime = System.currentTimeMillis(),
        )
        runTest { gifInfoDao.saveQueryInfo(queryInfo) }
    }

    @Test
    fun `test get query info`() = runTest {
        val queryInfoEntity = gifInfoDao.getQueryInfoEntity(QUERY)
        assertThat(queryInfoEntity).isEqualTo(queryInfo)
    }

    @Test
    fun `test delete query info`() = runTest {
        gifInfoDao.deleteQueryInfoEntities(QUERY)
        val queryInfoEntity = gifInfoDao.getQueryInfoEntity(QUERY)
        assertThat(queryInfoEntity).isNull()
    }

    @Test
    fun `test get query info entities`() = runTest {
        val queryInfoEntities = gifInfoDao.getQueryInfoEntities()
        assertThat(queryInfoEntities).hasSize(1)
        assertThat(queryInfoEntities.first()).isEqualTo(queryInfo)
    }

    @Test
    fun `test blacklist`() = runTest {
        assertThat(gifInfoDao.getBlacklistIds()).isEmpty()
        gifInfoDao.addIdToBlacklist(BlackListEntity(TEST_ID))
        val blacklist = gifInfoDao.getBlacklistIds()
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