package com.example.gif_api.data.remote.api

import com.example.gif_api.data.remote.responses.GifDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "api_key"
private const val QUERY = "q"
private const val LANGUAGE = "lang"
private const val LIMIT = "limit"
private const val OFFSET = "offset"

private const val LANGUAGE_DEFAULT = "en"

interface GifApi {

    @GET("search")
    suspend fun searchGifs(
        @Query(API_KEY) apiKey: String,
        @Query(QUERY) query: String,
        @Query(LIMIT) limit: Int,
        @Query(OFFSET) offset: Int,
        @Query(LANGUAGE) lang: String = LANGUAGE_DEFAULT,
    ): Response<GifDataResponse>
}