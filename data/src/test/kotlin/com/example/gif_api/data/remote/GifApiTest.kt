package com.example.gif_api.data.remote

import com.example.gif_api.data.remote.api.GifApi
import com.example.gif_api.data.remote.responses.GifDataResponse
import com.example.gif_api.data.remote.responses.GifDataResponse.GifResponse.Images
import com.example.gif_api.data.remote.responses.GifDataResponse.GifResponse.UrlHolder
import com.example.gif_api.domain.utils.Result
import com.google.common.truth.Truth.assertThat
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.create

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)
class GifApiTest {

    private lateinit var api: GifApi

    private val json = Json {
        isLenient = true
        coerceInputValues = true
        explicitNulls = true
        ignoreUnknownKeys = true
    }

    @Before
    fun setUp() {
        val mockWebServer = MockWebServer().apply { dispatcher = GifMockWebServerDispatcher() }
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        api = retrofit.create()
    }

    @Test
    fun `test get gifs response`() = runTest {
        val response = safeApiCall {
            api.searchGifs(
                TEST_STRING_VALUE,
                TEST_STRING_VALUE,
                TEST_INT_VALUE,
                TEST_INT_VALUE,
                TEST_STRING_VALUE
            )
        }
        withAssertedCast<Result.Success<GifDataResponse>>(response) {
            val expectedResult = GifDataResponse(
                pagination = GifDataResponse.Pagination(totalCount = 3768),
                gifs = listOf(
                    GifDataResponse.GifResponse(
                        id = "8FNlmNPDTo2wE",
                        title = "Test School GIF by 5-Second Films",
                        images = Images(
                            original = UrlHolder(
                                url = "https://media3.giphy.com/media/8FNlmNPDTo2wE/giphy.gif?cid=f8428fcd4avawl4d0yjxn2lcx4b4ypl39y06mdkkvewtjxx4&rid=giphy.gif&ct=g"
                            ),
                            small = UrlHolder(
                                url = "https://media3.giphy.com/media/8FNlmNPDTo2wE/200_d.gif?cid=f8428fcd4avawl4d0yjxn2lcx4b4ypl39y06mdkkvewtjxx4&rid=200_d.gif&ct=g"
                            )
                        )
                    )
                )
            )
            assertThat(data).isEqualTo(expectedResult)
        }
    }

    private inner class GifMockWebServerDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse =
            if (request.method == "GET") {
                when (request.path) {
                    "/search?" +
                            "$API_KEY=$TEST_STRING_VALUE&" +
                            "$QUERY=$TEST_STRING_VALUE&" +
                            "$LIMIT=$TEST_INT_VALUE&" +
                            "$OFFSET=$TEST_INT_VALUE&" +
                            "$LANGUAGE=$TEST_STRING_VALUE" ->
                        mockResponseFromResources("gif_response.json")
                    else -> throw AssertionError("Invalid path: ${request.path}")
                }
            } else throw AssertionError("Invalid path: ${request.path}")
    }

    private fun mockResponseFromResources(path: String) = MockResponse().apply {
        setResponseCode(200)
        setBody(javaClass.classLoader!!.getResourceAsStream(path).bufferedReader().readText())
    }

    companion object {
        private const val API_KEY = "api_key"
        private const val QUERY = "q"
        private const val LANGUAGE = "lang"
        private const val LIMIT = "limit"
        private const val OFFSET = "offset"

        private const val TEST_STRING_VALUE = "TEST"
        private const val TEST_INT_VALUE = 1
        private const val ERROR_TEST_INT_VALUE = 2
    }
}

