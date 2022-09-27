package com.example.gif_api.domain.utils

sealed interface Result<out T> {
    class Success<T>(val data: T) : Result<T>
    class Error(val msg: String? = null, val code: Int? = null) : Result<Nothing>
    object Empty : Result<Nothing>
}
