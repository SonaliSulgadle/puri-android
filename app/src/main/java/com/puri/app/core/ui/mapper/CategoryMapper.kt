package com.puri.app.core.ui.mapper

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.puri.app.R
import com.puri.app.domain.model.Category

@StringRes
fun Category.toLabelRes(): Int = when (this) {
    Category.TRASH -> R.string.category_eco_help
    Category.APPLIANCE -> R.string.category_utility
    Category.TRANSPORT -> R.string.category_transit
    Category.FOOD -> R.string.category_food
    Category.MEDICAL -> R.string.category_medical
    Category.GENERAL -> R.string.category_general
}

fun Category.toChipColor(): Color = when (this) {
    Category.TRASH -> Color(0xFF2E7D32)   // green
    Category.APPLIANCE -> Color(0xFF1565C0)   // blue
    Category.TRANSPORT -> Color(0xFF6A1B9A)   // purple
    Category.FOOD -> Color(0xFFE65100)   // orange
    Category.MEDICAL -> Color(0xFFC62828)   // red
    Category.GENERAL -> Color(0xFF37474F)   // grey
}