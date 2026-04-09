package com.puri.app.core.common

sealed class PuriError {
    // Solve errors
    data object DailyLimitReached : PuriError()
    data object EmptyQuery : PuriError()
    data object ImageTooBlurry : PuriError()
    data object UnsafeContent : PuriError()

    // Network errors
    data object NoInternet : PuriError()
    data class ApiError(
        val code: Int,
        val isRetryable: Boolean = code == 429 || code == 503
    ) : PuriError()

    // Generic
    data class Unknown(val throwable: Throwable? = null) : PuriError()
}