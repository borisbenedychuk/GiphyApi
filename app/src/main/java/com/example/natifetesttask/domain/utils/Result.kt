package com.example.natifetesttask.domain.utils

sealed interface Result<out T> {
    class Success<T>(val data: T? = null) : Result<T>
    class Error(val msg: String? = null, val code: Int? = null) : Result<Nothing>

    fun <R> map(block: (T) -> R): Result<R> =
        when (this) {
            is Error -> this
            is Success -> Success(data = data?.let(block))
        }
}
