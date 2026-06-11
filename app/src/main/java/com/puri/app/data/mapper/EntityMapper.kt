package com.puri.app.data.mapper

import com.puri.app.data.local.db.HistoryEntity
import com.puri.app.data.local.db.SavedGuideEntity
import com.puri.app.data.local.db.StepsSerializer
import com.puri.app.data.local.db.VisibleTextsSerializer
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.model.PreBundledGuideKey
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.model.SolveResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun HistoryEntity.toDomain(): HistoryItem = HistoryItem(
    id = id,
    solveResult = SolveResult(
        id = id,
        whatThisIs = whatThisIs,
        description = description,
        steps = StepsSerializer.fromJson(stepsJson),
        visibleTexts = VisibleTextsSerializer.fromJson(visibleTextsJson),
        recommendedAction = recommendedAction,
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
    visibleTextsJson = VisibleTextsSerializer.toJson(solveResult.visibleTexts),
    recommendedAction = solveResult.recommendedAction,
    warning = solveResult.warning,
    koreaTip = solveResult.koreaTip,
    category = solveResult.category,
    confidenceLevel = solveResult.confidenceLevel,
    imageUri = solveResult.imageUri,
    inputQuery = solveResult.inputQuery,
    timestamp = timestamp,
    isSaved = isSaved
)

fun SavedGuideEntity.toDomain(): SavedGuide = SavedGuide(
    id = id,
    title = title,
    description = description,
    guideKey = guideKey?.let {
        try {
            PreBundledGuideKey.valueOf(it)
        } catch (e: Exception) {
            null
        }
    },
    category = category,
    solveResult = solveResult,
    isPreBundled = isPreBundled,
    isFeatured = isFeatured,
    imageUri = imageUri,
    savedAt = savedAt
)

fun SavedGuide.toEntity(): SavedGuideEntity = SavedGuideEntity(
    id = id,
    title = title,
    description = description,
    guideKey = guideKey?.name,
    category = category,
    stepsJson = StepsSerializer.toJson(solveResult?.steps ?: emptyList()),
    visibleTextsJson = VisibleTextsSerializer.toJson(
        solveResult?.visibleTexts ?: emptyList()
    ),
    recommendedAction = solveResult?.recommendedAction,
    warning = solveResult?.warning,
    koreaTip = solveResult?.koreaTip,
    imageUri = imageUri,
    isPreBundled = isPreBundled,
    isFeatured = isFeatured,
    savedAt = savedAt,
    solveResult = solveResult
)

fun HistoryItem.toSavedGuideEntity(): SavedGuideEntity = SavedGuideEntity(
    id = 0,
    title = solveResult.whatThisIs,
    description = solveResult.description,
    guideKey = null,
    category = solveResult.category,
    stepsJson = Json.encodeToString(solveResult.steps),
    visibleTextsJson = Json.encodeToString(solveResult.visibleTexts),
    recommendedAction = solveResult.recommendedAction,
    warning = solveResult.warning,
    koreaTip = solveResult.koreaTip,
    imageUri = solveResult.imageUri,
    isPreBundled = false,
    isFeatured = false,
    savedAt = System.currentTimeMillis(),
    historyItemId = id,
    solveResult = solveResult
)