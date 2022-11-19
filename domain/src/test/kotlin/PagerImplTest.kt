import com.example.gif_api.domain.gif.usecase.Pager
import com.example.gif_api.domain.utils.Result
import com.example.gif_api.domain.utils.successResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PagerImplTest {

    private lateinit var pager: Pager

    private lateinit var mockGifRepository: MockGifRepository

    @Before
    fun preparePager() {
        mockGifRepository = MockGifRepository()
        pager = Pager(mockGifRepository)
    }

    @Test
    fun `load 2 pages if cache is not fresh`(): Unit = runTest {
        mockGifRepository.cacheFresh = false
        mockGifRepository.listFinishedResult = successResult(false)
        val resultDiff = async {
            pager.newPages(QUERY, 0)
        }
        val pages = pager.pagesFlow.first()
        val result = resultDiff.await()
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(false)
        assertThat(pages.size).isEqualTo(20)
    }


    @Test
    fun `load 1 page if cache is fresh`(): Unit = runTest {
        mockGifRepository.cacheFresh = true
        mockGifRepository.listFinishedResult = successResult(false)
        val resultDiff = async {
            pager.newPages(QUERY, 0)
        }
        val pages = pager.pagesFlow.first()
        assertThat(pages.size).isEqualTo(10)
        val result = resultDiff.await()
        withAssertedCast<Result.Success<Boolean>>(result) {
            assertThat(data).isFalse()
        }
    }

    companion object {
        private const val QUERY = "query"
    }
}

inline fun <reified T> withAssertedCast(value: Any, block: T.() -> Unit) {
    assertThat(value).isInstanceOf(T::class.java)
    (value as T).block()
}

