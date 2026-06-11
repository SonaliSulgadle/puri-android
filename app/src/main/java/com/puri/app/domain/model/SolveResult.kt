package com.puri.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SolveResult(
    val id: Long = 0L,
    val whatThisIs: String,
    val description: String,
    val steps: List<SolveStep>,
    val visibleTexts: List<VisibleTextItem> = emptyList(),
    val recommendedAction: String? = null,
    val warning: String?,
    val koreaTip: String?,
    val category: Category,
    val confidenceLevel: ConfidenceLevel,
    val imageUri: String?,
    val inputQuery: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class SolveStep(
    val order: Int,
    val title: String,
    val description: String
)

enum class ConfidenceLevel {
    HIGH,
    LOW,
    UNSAFE
}