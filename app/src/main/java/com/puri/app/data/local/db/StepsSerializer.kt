package com.puri.app.data.local.db

import com.puri.app.domain.model.SolveStep
import org.json.JSONArray
import org.json.JSONObject

object StepsSerializer {

    fun toJson(steps: List<SolveStep>): String {
        if (steps.isEmpty()) return "[]"
        val array = JSONArray()
        steps.forEach { step ->
            JSONObject().apply {
                put("order", step.order)
                put("title", step.title)
                put("description", step.description)
            }.also { array.put(it) }
        }
        return array.toString()
    }

    fun fromJson(json: String): List<SolveStep> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                SolveStep(
                    order = obj.optInt("order", i + 1),
                    title = obj.optString("title", ""),
                    description = obj.optString("description", "")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}