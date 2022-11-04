package com.example.gif_api.data.db

import androidx.room.Room
import com.example.gif_api.data.db.dao.GifDao
import com.example.gif_api.data.db.entities.GifEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GifDaoQueriesTest {

    private lateinit var gifDao: GifDao

    private val entities: List<GifEntity>
        get() = List(PAGE_SIZE * 3) {
            GifEntity(
                id = it.toString(),
                page = it / PAGE_SIZE,
                originalUrl = "url$it",
                smallUrl = "smallUrl$it",
                title = it.toString(),
                query = QUERY,
            )
        }

    @Before
    fun setUp() {
        val room = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            AppDB::class.java
        ).build()
        gifDao = room.gifDao
        runTest { gifDao.saveGifs(entities) }
    }

    @Test
    fun `test get gifs`() = runTest {
        val gifs = gifDao.getGifs(QUERY, listOf(0, 1)).first()
        assertThat(gifs).hasSize(PAGE_SIZE * 2)
        assertThat(gifs).isEqualTo(entities.take(PAGE_SIZE * 2))
    }

    @Test
    fun `test delete gif by id`() = runTest {
        val gifs = gifDao.getGifs(QUERY, listOf(0))
        launch {
            delay(1000)
            gifDao.deleteGif(1.toString())
        }
        launch {
            val list = gifs.drop(1).first()
            assertThat(list).hasSize(24)
            assertThat(list).isEqualTo(entities.take(PAGE_SIZE).filter { it.id != "1" })
        }
    }

    @Test
    fun `test get first gif`() = runTest {
        val gif = gifDao.getFirstGif(QUERY)
        assertThat(gif).isEqualTo(entities.first())
    }

    @Test
    fun `test delete gifs by query`() = runTest {
        gifDao.deleteQueryGifs(QUERY)
        val gif = gifDao.getFirstGif(QUERY)
        assertThat(gif).isNull()
        val gifs = gifDao.getGifs(QUERY, listOf(0)).first()
        assertThat(gifs).isNotNull()
        assertThat(gifs).isEmpty()
    }

    companion object {
        private const val PAGE_SIZE = 25
        private const val QUERY = "query"
    }
}