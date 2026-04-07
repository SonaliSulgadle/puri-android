package com.puri.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel

@Entity(tableName = "history")
@TypeConverters(RoomConverters::class)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val whatThisIs: String,
    val description: String,
    val stepsJson: String,
    val warning: String?,
    val koreaTip: String?,
    val category: Category,
    val confidenceLevel: ConfidenceLevel,
    val imageUri: String?,
    val inputQuery: String?,
    val timestamp: Long,
    val isSaved: Boolean = false
)