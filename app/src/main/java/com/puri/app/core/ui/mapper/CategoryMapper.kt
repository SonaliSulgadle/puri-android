package com.puri.app.core.ui.mapper

import androidx.annotation.StringRes
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