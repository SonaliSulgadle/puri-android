package com.puri.app.data.local.db

import androidx.room.TypeConverter
import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.model.SolveStep

class RoomConverters {

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.fromString(value)

    @TypeConverter
    fun fromConfidenceLevel(level: ConfidenceLevel): String = level.name

    @TypeConverter
    fun toConfidenceLevel(value: String): ConfidenceLevel =
        ConfidenceLevel.valueOf(value)

    @TypeConverter
    fun fromSteps(steps: List<SolveStep>): String = StepsSerializer.toJson(steps)

    @TypeConverter
    fun toSteps(json: String): List<SolveStep> = StepsSerializer.fromJson(json)
}