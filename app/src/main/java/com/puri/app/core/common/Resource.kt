package com.puri.app.core.common

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val error: PuriError) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

inline fun <T, R> Resource<T>.mapSuccess(transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error -> this
    is Resource.Loading -> this
}