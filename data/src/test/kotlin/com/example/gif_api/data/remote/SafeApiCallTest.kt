package com.example.gif_api.data.remote

import com.example.gif_api.domain.utils.Result.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SafeApiCallTest {

    @Test
    fun `test empty result`() = runTest {
        val result = safeApiCall {
            Response.success<Boolean>(null)
        }
        assertThat(result).isInstanceOf(Empty::class.java)
    }

    @Test
    fun `test empty result with custom predicate`() = runTest {
        val result = safeApiCall(
            isEmptyPredicate = { !it.body()!! }
        ) {
            Response.success(false)
        }
        assertThat(result).isInstanceOf(Empty::class.java)
    }

    @Test
    fun `test error result with custom predicate`() = runTest {
        val result = safeApiCall(
            isEmptyPredicate = { it.body() ?: false }
        ) {
            Response.success(null)
        }
        assertThat(result).isInstanceOf(Error::class.java)
    }

    @Test
    fun `test error result`() = runTest {
        val result = safeApiCall<Boolean> {
            throw Exception(TEST)
        }
        withAssertedCast<Error>(result) {
            assertThat(msg).isEqualTo(TEST)
            assertThat(code).isNull()
        }
    }

    @Test
    fun `test successful result`() = runTest {
        val result = safeApiCall<Boolean> {
            Response.success(true)
        }
        withAssertedCast<Success<Boolean>>(result) {
            assertThat(data).isTrue()
        }
    }

    @Test
    fun `test not successful result`() = runTest {
        val result = safeApiCall<Boolean> {
            Response.error(404, ResponseBody.create(null, false.toString()))
        }
        withAssertedCast<Error>(result) {
            assertThat(code).isEqualTo(404)
        }
    }

    @Test
    fun `test http exception predicate`() = runTest {
        val result = safeApiCall<Boolean> {
            throw HttpException(
                Response.error<Boolean>(500, ResponseBody.create(null, ""))
            )
        }
        withAssertedCast<Error>(result) {
            assertThat(code).isEqualTo(500)
        }
    }

    companion object {
        const val TEST = "Test"
    }
}

inline fun <reified T> withAssertedCast(value: Any, block: T.() -> Unit) {
    assertThat(value).isInstanceOf(T::class.java)
    (value as T).block()
}