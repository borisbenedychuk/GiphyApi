package com.example.gif_api.domain.utils

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    class Error(val msg: String? = null, val code: Int? = null) : Result<Nothing>
    object Empty : Result<Nothing>
}

fun <T> successResult(value: T) = Result.Success(value)
fun emptyResult() = Result.Empty
fun errorResult(msg: String? = null, code: Int? = null) = Result.Error(msg, code)
