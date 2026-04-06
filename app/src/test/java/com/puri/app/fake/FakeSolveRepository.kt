package com.puri.app.fake

import android.graphics.Bitmap
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.model.SolveResult
import com.puri.app.domain.repository.SolveRepository

class FakeSolveRepository : SolveRepository {

    var shouldReturnError = false
    var solveResult = SolveResult(
        whatThisIs     = "Test Item",
        description    = "Test description",
        steps          = emptyList(),
        warning        = null,
        koreaTip       = null,
        category       = Category.GENERAL,
        confidenceLevel = ConfidenceLevel.HIGH,
        imageUri       = null,
        inputQuery     = null
    )

    override suspend fun solveImage(
        bitmap: Bitmap,
        additionalContext: String?
    ): Resource<SolveResult> = if (shouldReturnError) {
        Resource.Error(PuriError.Unknown())
    } else {
        Resource.Success(solveResult)
    }

    override suspend fun solveText(query: String): Resource<SolveResult> =
        if (shouldReturnError) {
            Resource.Error(PuriError.Unknown())
        } else {
            Resource.Success(solveResult)
        }
}