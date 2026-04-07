package com.puri.app.data.local.db

import androidx.room.TypeConverter
import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.model.SolveStep
import org.json.JSONArray
import org.json.JSONObject

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
    fun fromSteps(steps: List<SolveStep>): String {
        val array = JSONArray()
        steps.forEach { step ->
            array.put(JSONObject().apply {
                put("order", step.order)
                put("title", step.title)
                put("description", step.description)
            })
        }
        return array.toString()
    }

    @TypeConverter
    fun toSteps(json: String): List<SolveStep> {
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            SolveStep(
                order = obj.getInt("order"),
                title = obj.getString("title"),
                description = obj.getString("description")
            )
        }
    }
}