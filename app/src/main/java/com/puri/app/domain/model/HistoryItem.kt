package com.puri.app.domain.model

data class HistoryItem(
    val id: Long = 0L,
    val solveResult: SolveResult,
    val timestamp: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false
)