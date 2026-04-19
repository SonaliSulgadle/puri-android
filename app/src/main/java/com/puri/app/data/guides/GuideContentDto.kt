package com.puri.app.data.guides

import com.puri.app.domain.model.GuideContent
import com.puri.app.domain.model.GuideItem
import com.puri.app.domain.model.GuideSection
import com.puri.app.domain.model.GuideSectionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuideContentDto(
    val sections: List<GuideSectionDto> = emptyList()
)

@Serializable
data class GuideSectionDto(
    val title: String = "",
    @SerialName("sectionType")
    val sectionType: String = "LIST",
    val items: List<GuideItemDto> = emptyList()
)

@Serializable
data class GuideItemDto(
    val korean: String = "",
    val translation: String = "",
    val detail: String = ""
)

fun GuideContentDto.toDomain() = GuideContent(
    sections = sections.map { it.toDomain() }
)

fun GuideSectionDto.toDomain() = GuideSection(
    title = title,
    sectionType = try {
        GuideSectionType.valueOf(sectionType)
    } catch (e: IllegalArgumentException) {
        GuideSectionType.LIST
    },
    items = items.map { GuideItem(it.korean, it.translation, it.detail) }
)