package com.puri.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.puri.app.domain.model.Category

@Entity(tableName = "saved_guides")
@TypeConverters(RoomConverters::class)
data class SavedGuideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String,
    val category: Category,
    val stepsJson: String,
    val warning: String?,
    val koreaTip: String?,
    val imageUri: String?,
    val isPreBundled: Boolean,
    val isFeatured: Boolean,
    val savedAt: Long
)