package com.example.natifetesttask.data.remote

import com.example.natifetesttask.domain.utils.Result
import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> =
    try {
        val response: Response<T> = call()
        if (response.isSuccessful) {
            Result.Success(response.body())
        } else {
            Result.Error(response.message(), response.code())
        }
    } catch (e: HttpException) {
        e.printStackTrace()
        Result.Error(e.message(), e.code())
    } catch (e: Exception) {
        e.printStackTrace()
        Result.Error(e.message)
    }