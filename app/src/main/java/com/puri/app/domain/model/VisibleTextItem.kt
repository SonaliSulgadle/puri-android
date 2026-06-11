package com.puri.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class VisibleTextItem(
    val original: String,
    val translation: String,
    val explanation: String
)