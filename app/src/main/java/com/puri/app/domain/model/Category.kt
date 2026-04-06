package com.puri.app.domain.model

enum class Category(val displayName: String, val emoji: String) {
    TRASH("Eco Help", "♻️"),
    APPLIANCE("Utility", "⚡"),
    TRANSPORT("Transit", "🚇"),
    FOOD("Food & Dining", "🍽️"),
    GENERAL("General", "💡");

    companion object {
        fun fromString(value: String): Category =
            entries.firstOrNull {
                it.name.equals(value, ignoreCase = true)
            } ?: GENERAL
    }
}