import com.example.gif_api.domain.gif.model.GifModel
import com.example.gif_api.domain.gif.repository.GifRepository
import com.example.gif_api.domain.utils.Result
import com.example.gif_api.domain.utils.successResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class MockGifRepository : GifRepository {

    var cacheFresh = false
    var listFinishedResult = successResult(false)
    var pageSize = 10

    private val flow = MutableStateFlow<List<GifModel>>(emptyList())

    override fun getPages(query: String, pages: List<Int>): Flow<List<GifModel>> = flow

    override suspend fun addGifToBlackList(id: String) = Unit

    override suspend fun deleteOldData() = Unit

    override suspend fun isGifCacheFresh(query: String): Boolean = cacheFresh

    override suspend fun loadPageAndCheckIfHasMore(query: String, page: Int): Result<Boolean> {
        flow.emit(List(pageSize, ::mockGifModel))
        return listFinishedResult
    }

    override suspend fun loadInitialPagesAndCheckIfHasMore(query: String): Result<Boolean> {
        flow.emit(List(pageSize * 2, ::mockGifModel))
        return listFinishedResult

    }

    private fun mockGifModel(index: Int) = GifModel(
        id = index.toString(),
        originalUrl = "url$index",
        smallUrl = "smallUrl$index",
        title = index.toString()
    )
}