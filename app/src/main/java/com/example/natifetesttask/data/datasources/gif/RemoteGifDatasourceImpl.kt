package com.example.natifetesttask.data.datasources.gif

import com.example.natifetesttask.BuildConfig
import com.example.natifetesttask.data.remote.api.GifApi
import com.example.natifetesttask.data.remote.responses.GifDataResponse
import com.example.natifetesttask.data.remote.safeApiCall
import com.example.natifetesttask.domain.utils.Result
import javax.inject.Inject

class RemoteGifDatasourceImpl @Inject constructor(
    private val api: GifApi,
) : RemoteGifDatasource {

    override suspend fun getGifs(
        query: String,
        limit: Int,
        offset: Int,
    ): Result<GifDataResponse> =
        safeApiCall(
            isEmptyPredicate = { it.body()?.gifs?.isEmpty() == true }
        ) {
            api.searchGifs(
                apiKey = BuildConfig.API_KEY,
                query = query,
                limit = limit,
                offset = offset
            )
        }
}