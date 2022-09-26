package com.example.natifetesttask.domain.utils

sealed interface Result<out T> {
    class Success<T>(val data: T) : Result<T>
    open class Error(val msg: String? = null, val code: Int? = null) : Result<Nothing>
}
