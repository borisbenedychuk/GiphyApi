package com.example.natifetesttask.domain.utils

import retrofit2.HttpException
import retrofit2.Response

sealed interface Result<out T> {
    class Success<T>(val data: T? = null) : Result<T>
    class Error(val msg: String? = null, val code: Int? = null) : Result<Nothing>

    fun <R> map(block: (T) -> R): Result<R> =
        when (this) {
            is Error -> this
            is Success -> Success(data = data?.let(block))
        }
}

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> = try {
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
