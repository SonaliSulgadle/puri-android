package com.puri.app.domain.model

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    KOREAN("ko", "한국어");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
    // V2: expand to Chinese, Japanese, Vietnamese, Thai, Tagalog, Hindi
    // Rename this enum to ResponseLanguage when expanding
}