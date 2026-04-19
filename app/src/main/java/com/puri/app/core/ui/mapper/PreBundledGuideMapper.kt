package com.puri.app.core.ui.mapper

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.puri.app.R
import com.puri.app.domain.model.PreBundledGuideKey
import com.puri.app.domain.model.SavedGuide

@StringRes
fun PreBundledGuideKey.titleRes(): Int = when (this) {
    PreBundledGuideKey.TRASH_SORTING -> R.string.guide_trash_title
    PreBundledGuideKey.WASHING_MACHINE -> R.string.guide_washing_title
    PreBundledGuideKey.SUBWAY_TMONEY -> R.string.guide_subway_title
    PreBundledGuideKey.GAS_STOVE -> R.string.guide_gas_stove_title
    PreBundledGuideKey.MEDICAL_CLINICS -> R.string.guide_medical_title
    PreBundledGuideKey.BUS_SYSTEM -> R.string.guide_bus_title
    PreBundledGuideKey.APARTMENT_INTERCOM -> R.string.guide_intercom_title
    PreBundledGuideKey.ADDRESS_PEOPLE -> R.string.guide_address_title
    PreBundledGuideKey.DAILY_PHRASES -> R.string.guide_phrases_title
    PreBundledGuideKey.SIM_CARDS -> R.string.guide_sim_title
    PreBundledGuideKey.GETTING_AROUND -> R.string.guide_getting_around_title
    PreBundledGuideKey.WHERE_TO_STAY -> R.string.guide_stay_title
}

@StringRes
fun PreBundledGuideKey.descriptionRes(): Int = when (this) {
    PreBundledGuideKey.TRASH_SORTING -> R.string.guide_trash_desc
    PreBundledGuideKey.WASHING_MACHINE -> R.string.guide_washing_desc
    PreBundledGuideKey.SUBWAY_TMONEY -> R.string.guide_subway_desc
    PreBundledGuideKey.GAS_STOVE -> R.string.guide_gas_stove_desc
    PreBundledGuideKey.MEDICAL_CLINICS -> R.string.guide_medical_desc
    PreBundledGuideKey.BUS_SYSTEM -> R.string.guide_bus_desc
    PreBundledGuideKey.APARTMENT_INTERCOM -> R.string.guide_intercom_desc
    PreBundledGuideKey.ADDRESS_PEOPLE -> R.string.guide_address_desc
    PreBundledGuideKey.DAILY_PHRASES -> R.string.guide_phrases_desc
    PreBundledGuideKey.SIM_CARDS -> R.string.guide_sim_desc
    PreBundledGuideKey.GETTING_AROUND -> R.string.guide_getting_around_desc
    PreBundledGuideKey.WHERE_TO_STAY -> R.string.guide_stay_desc
}

@RawRes
fun PreBundledGuideKey.rawContentRes(): Int = when (this) {
    PreBundledGuideKey.TRASH_SORTING -> R.raw.guide_trash_sorting
    PreBundledGuideKey.WASHING_MACHINE -> R.raw.guide_washing_machine
    PreBundledGuideKey.SUBWAY_TMONEY -> R.raw.guide_subway_tmoney
    PreBundledGuideKey.GAS_STOVE -> R.raw.guide_gas_stove
    PreBundledGuideKey.MEDICAL_CLINICS -> R.raw.guide_medical_clinics
    PreBundledGuideKey.BUS_SYSTEM -> R.raw.guide_bus_system
    PreBundledGuideKey.APARTMENT_INTERCOM -> R.raw.guide_apartment_intercom
    PreBundledGuideKey.ADDRESS_PEOPLE -> R.raw.guide_address_people
    PreBundledGuideKey.DAILY_PHRASES -> R.raw.guide_daily_phrases
    PreBundledGuideKey.SIM_CARDS -> R.raw.guide_sim_cards
    PreBundledGuideKey.GETTING_AROUND -> R.raw.guide_getting_around
    PreBundledGuideKey.WHERE_TO_STAY -> R.raw.guide_where_to_stay
}

@Composable
fun SavedGuide.displayTitle(): String =
    if (guideKey != null) stringResource(guideKey.titleRes()) else title

@Composable
fun SavedGuide.displayDescription(): String =
    if (guideKey != null) stringResource(guideKey.descriptionRes()) else description