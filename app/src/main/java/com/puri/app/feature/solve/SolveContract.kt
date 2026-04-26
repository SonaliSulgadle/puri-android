package com.puri.app.feature.solve

import android.graphics.Bitmap
import com.puri.app.core.common.PuriError
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.model.SolveResult

const val DAILY_LIMIT = 10

sealed interface SolveIntent {
    data object OpenCamera : SolveIntent
    data object OpenGallery : SolveIntent
    data class ImageCaptured(
        val bitmap: Bitmap, val imageUri: String?
    ) : SolveIntent

    data class GalleryImageSelected(
        val bitmap: Bitmap, val imageUri: String?
    ) : SolveIntent

    data class TextQueryChanged(val query: String) : SolveIntent
    data object SubmitTextQuery : SolveIntent
    data object Retry : SolveIntent
    data object SaveResult : SolveIntent
    data object ClearResult : SolveIntent
    data class AdditionalContextChanged(val context: String) : SolveIntent
}

sealed interface SolveUiState {
    data class Idle(
        val dailySolvesRemaining: Int = 10,
        val dailySolvesLimit: Int = DAILY_LIMIT,
        val recentSolves: List<HistoryItem> = emptyList(),
        val currentQuery: String = ""
    ) : SolveUiState

    data object CameraOpen : SolveUiState

    data object Loading : SolveUiState

    data class Success(
        val result: SolveResult, val isSaved: Boolean = false
    ) : SolveUiState

    data class Uncertain(val bitmap: Bitmap?) : SolveUiState

    data object UnsafeContent : SolveUiState

    data object DailyLimitReached : SolveUiState

    data class Error(val error: PuriError) : SolveUiState

    data class AddressResult(val result: AddressResult) : SolveUiState
}

sealed interface SolveUiEffect {
    data class ShowSnackbar(val messageRes: Int) : SolveUiEffect
    data object ShowSaveConfirmation : SolveUiEffect
    data object NavigateToHistory : SolveUiEffect
    data object TriggerHaptic : SolveUiEffect
}