package com.puri.app.fake

import android.graphics.Bitmap
import com.puri.app.core.common.PuriError
import com.puri.app.core.common.Resource
import com.puri.app.domain.model.SolveResult
import com.puri.app.domain.repository.SolveRepository
import com.puri.app.util.TestFixtures.applianceSolveResult

class FakeSolveRepository : SolveRepository {

    var shouldReturnError = false

    override suspend fun solveImage(
        bitmap: Bitmap,
        additionalContext: String?
    ): Resource<SolveResult> = if (shouldReturnError) {
        Resource.Error(PuriError.Unknown())
    } else {
        Resource.Success(applianceSolveResult)
    }

    override suspend fun solveText(query: String): Resource<SolveResult> =
        if (shouldReturnError) {
            Resource.Error(PuriError.Unknown())
        } else {
            Resource.Success(applianceSolveResult)
        }
}