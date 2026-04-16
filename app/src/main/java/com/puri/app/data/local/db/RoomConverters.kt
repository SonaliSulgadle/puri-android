package com.puri.app.data.local.db

import androidx.room.TypeConverter
import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.model.PreBundledGuideKey
import com.puri.app.domain.model.SolveStep
import com.puri.app.domain.model.VisibleTextItem

class RoomConverters {

    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.fromString(value)

    @TypeConverter
    fun fromConfidenceLevel(value: ConfidenceLevel): String = value.name

    @TypeConverter
    fun toConfidenceLevel(value: String): ConfidenceLevel =
        ConfidenceLevel.valueOf(value)

    @TypeConverter
    fun fromSteps(steps: List<SolveStep>): String = StepsSerializer.toJson(steps)

    @TypeConverter
    fun toSteps(json: String): List<SolveStep> = StepsSerializer.fromJson(json)

    @TypeConverter
    fun fromVisibleTexts(items: List<VisibleTextItem>): String =
        VisibleTextsSerializer.toJson(items)

    @TypeConverter
    fun toVisibleTexts(json: String): List<VisibleTextItem> =
        VisibleTextsSerializer.fromJson(json)

    @TypeConverter
    fun fromGuideKey(key: PreBundledGuideKey?): String? = key?.name

    @TypeConverter
    fun toGuideKey(value: String?): PreBundledGuideKey? =
        value?.let {
            try {
                PreBundledGuideKey.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
}