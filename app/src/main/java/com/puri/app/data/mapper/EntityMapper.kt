package com.puri.app.data.mapper

import com.puri.app.data.local.db.HistoryEntity
import com.puri.app.data.local.db.SavedGuideEntity
import com.puri.app.data.local.db.StepsSerializer
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
        steps = StepsSerializer.fromJson(stepsJson),
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
    stepsJson = StepsSerializer.toJson(solveResult.steps),
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
    stepsJson = StepsSerializer.toJson(solveResult?.steps ?: emptyList()),
    warning = solveResult?.warning,
    koreaTip = solveResult?.koreaTip,
    imageUri = imageUri,
    isPreBundled = isPreBundled,
    isFeatured = isFeatured,
    savedAt = savedAt
)

// Convert a HistoryEntity to a SavedGuideEntity when user saves a result
fun HistoryEntity.toSavedGuideEntity(): SavedGuideEntity = SavedGuideEntity(
    title = whatThisIs,
    description = description,
    category = category,
    stepsJson = stepsJson,
    warning = warning,
    koreaTip = koreaTip,
    imageUri = imageUri,
    isPreBundled = false,
    isFeatured = false,
    savedAt = System.currentTimeMillis(),
    historyItemId = id
)