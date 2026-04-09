package com.puri.app.core.ui.mapper

import androidx.annotation.StringRes
import com.puri.app.R
import com.puri.app.core.common.PuriError

@StringRes
fun PuriError.toMessageRes(): Int = when (this) {
    PuriError.DailyLimitReached -> R.string.error_daily_limit_reached
    PuriError.EmptyQuery -> R.string.error_empty_query
    PuriError.ImageTooBlurry -> R.string.error_image_too_blurry
    PuriError.UnsafeContent -> R.string.error_unsafe_content
    PuriError.NoInternet -> R.string.error_no_internet
    is PuriError.ApiError -> when (this.code) {
        429 -> R.string.error_api_quota
        503 -> R.string.error_api_overloaded
        else -> R.string.error_api_generic
    }

    is PuriError.Unknown -> R.string.error_unknown
}