package com.example.gif_api.data.remote

import com.example.gif_api.domain.utils.Result
import com.example.gif_api.domain.utils.emptyResult
import com.example.gif_api.domain.utils.errorResult
import com.example.gif_api.domain.utils.successResult
import retrofit2.HttpException
import retrofit2.Response

inline fun <T> safeApiCall(
    isEmptyPredicate: (Response<T>) -> Boolean = { it.body() == null },
    call: () -> Response<T>,
): Result<T> =
    try {
        val response: Response<T> = call()
        response.run {
            when {
                !isSuccessful -> errorResult(message(), code())
                isEmptyPredicate(this) -> emptyResult()
                else -> body()?.let(::successResult) ?: errorResult(message(), code())
            }
        }
    } catch (e: HttpException) {
        e.printStackTrace()
        errorResult(e.message(), e.code())
    } catch (e: Exception) {
        e.printStackTrace()
        errorResult(e.message)
    }