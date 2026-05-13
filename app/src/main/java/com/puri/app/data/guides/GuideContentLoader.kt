package com.puri.app.data.guides

import android.content.Context
import com.puri.app.core.analytics.Crashlytics
import com.puri.app.core.ui.mapper.rawContentRes
import com.puri.app.domain.model.GuideContent
import com.puri.app.domain.model.PreBundledGuideKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuideContentLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val crashlytics: Crashlytics
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    // Cache loaded content — to avoid re-reading files every time
    private val cache = mutableMapOf<PreBundledGuideKey, GuideContent?>()

    fun loadContent(key: PreBundledGuideKey): GuideContent? {
        return cache.getOrPut(key) {
            try {
                val resId = key.rawContentRes()
                val jsonString = context.resources
                    .openRawResource(resId)
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }
                json.decodeFromString<GuideContentDto>(jsonString).toDomain()
            } catch (e: Exception) {
                crashlytics.recordException(e, "guide_load_${key.name}")
                null
            }
        }
    }
}
