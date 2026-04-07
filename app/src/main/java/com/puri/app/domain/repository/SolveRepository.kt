package com.puri.app.domain.repository

import android.graphics.Bitmap
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.SolveResult

interface SolveRepository {
    suspend fun solveImage(
        bitmap: Bitmap,
        additionalContext: String? = null,
        imageUri: String?
    ): Resource<SolveResult>

    suspend fun solveText(
        query: String
    ): Resource<SolveResult>
}