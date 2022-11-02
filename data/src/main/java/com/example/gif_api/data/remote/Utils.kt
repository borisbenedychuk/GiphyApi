package com.example.gif_api.data.remote

import com.example.gif_api.domain.utils.Result
import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> safeApiCall(
    isEmptyPredicate: (Response<T>) -> Boolean = { it.body() == null },
    call: suspend () -> Response<T>,
): Result<T> =
    try {
        val response: Response<T> = call()
        if (response.isSuccessful) {
            val isEmpty = isEmptyPredicate(response)
            if (isEmpty) {
                Result.Empty
            } else {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error(response.message(), response.code())
            }
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