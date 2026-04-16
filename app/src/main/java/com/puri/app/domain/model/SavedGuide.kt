package com.puri.app.domain.model

data class SavedGuide(
    val id: Long = 0L,
    val title: String,
    val description: String,
    val guideKey: PreBundledGuideKey? = null,
    val category: Category,
    val solveResult: SolveResult?,
    val isPreBundled: Boolean = false,
    val isFeatured: Boolean = false,
    val imageUri: String?,
    val savedAt: Long = System.currentTimeMillis()
)