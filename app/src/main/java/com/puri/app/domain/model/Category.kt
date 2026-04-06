package com.puri.app.domain.model

enum class Category(val emoji: String) {
    TRASH("♻️"),
    APPLIANCE("⚡"),
    TRANSPORT("🚇"),
    FOOD("🍽️"),
    GENERAL("💡");

    companion object {
        fun fromString(value: String): Category =
            entries.firstOrNull {
                it.name.equals(value, ignoreCase = true)
            } ?: GENERAL
    }
}