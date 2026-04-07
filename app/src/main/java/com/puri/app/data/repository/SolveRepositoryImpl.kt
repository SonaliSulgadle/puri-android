package com.puri.app.data.repository

import android.graphics.Bitmap
import com.puri.app.core.common.Resource
import com.puri.app.data.local.datastore.PuriPreferences
import com.puri.app.data.remote.GeminiDataSource
import com.puri.app.domain.model.SolveResult
import com.puri.app.domain.repository.SolveRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolveRepositoryImpl @Inject constructor(
    private val geminiDataSource: GeminiDataSource,
    private val preferences: PuriPreferences
) : SolveRepository {

    override suspend fun solveImage(
        bitmap: Bitmap,
        additionalContext: String?,
        imageUri: String?
    ): Resource<SolveResult> {
        val language = preferences.appLanguage.first()
        return geminiDataSource.solveImage(bitmap, additionalContext, imageUri, language)
    }

    override suspend fun solveText(query: String): Resource<SolveResult> {
        val language = preferences.appLanguage.first()
        return geminiDataSource.solveText(query, language)
    }
}