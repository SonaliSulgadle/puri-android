package com.puri.app.domain.model

data class SolveResult(
    val id: Long = 0L,
    val whatThisIs: String,
    val description: String,
    val steps: List<SolveStep>,
    val warning: String?,
    val koreaTip: String?,
    val category: Category,
    val confidenceLevel: ConfidenceLevel,
    val imageUri: String?,
    val inputQuery: String?,
    val timestamp: Long = System.currentTimeMillis()
)

data class SolveStep(
    val order: Int,
    val title: String,
    val description: String
)

enum class ConfidenceLevel {
    HIGH,      // structured response returned — show ResponseCard
    LOW,       // AI expressed uncertainty — show RetryCard
    UNSAFE     // medication/chemicals detected — show safety message
}