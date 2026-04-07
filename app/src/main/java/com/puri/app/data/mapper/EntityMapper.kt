package com.puri.app.data.mapper

import com.puri.app.data.local.db.HistoryEntity
import com.puri.app.data.local.db.RoomConverters
import com.puri.app.data.local.db.SavedGuideEntity
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.model.SolveResult

// HistoryEntity ↔ HistoryItem
fun HistoryEntity.toDomain(): HistoryItem = HistoryItem(
    id = id,
    solveResult = SolveResult(
        id = id,
        whatThisIs = whatThisIs,
        description = description,
        steps = RoomConverters().toSteps(stepsJson),
        warning = warning,
        koreaTip = koreaTip,
        category = category,
        confidenceLevel = confidenceLevel,
        imageUri = imageUri,
        inputQuery = inputQuery,
        timestamp = timestamp
    ),
    timestamp = timestamp,
    isSaved = isSaved
)

fun HistoryItem.toEntity(): HistoryEntity = HistoryEntity(
    id = id,
    whatThisIs = solveResult.whatThisIs,
    description = solveResult.description,
    stepsJson = RoomConverters().fromSteps(solveResult.steps),
    warning = solveResult.warning,
    koreaTip = solveResult.koreaTip,
    category = solveResult.category,
    confidenceLevel = solveResult.confidenceLevel,
    imageUri = solveResult.imageUri,
    inputQuery = solveResult.inputQuery,
    timestamp = timestamp,
    isSaved = isSaved
)

// SavedGuideEntity ↔ SavedGuide
fun SavedGuideEntity.toDomain(): SavedGuide = SavedGuide(
    id = id,
    title = title,
    description = description,
    category = category,
    solveResult = null,
    isPreBundled = isPreBundled,
    isFeatured = isFeatured,
    imageUri = imageUri,
    savedAt = savedAt
)

fun SavedGuide.toEntity(): SavedGuideEntity = SavedGuideEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    stepsJson = RoomConverters().fromSteps(
        solveResult?.steps ?: emptyList()
    ),
    warning = solveResult?.warning,
    koreaTip = solveResult?.koreaTip,
    imageUri = imageUri,
    isPreBundled = isPreBundled,
    isFeatured = isFeatured,
    savedAt = savedAt
)