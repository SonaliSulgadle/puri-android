package com.puri.app.domain.model

data class GuideContent(
    val sections: List<GuideSection>
)

data class GuideSection(
    val title: String,
    val items: List<GuideItem>,
    val sectionType: GuideSectionType = GuideSectionType.LIST
)

enum class GuideSectionType {
    LIST,      // bullet list of items
    STEPS,     // numbered steps
    TABLE,     // two-column key→value
    WARNING,   // red highlighted block
    TIP        // green highlighted block
}

data class GuideItem(
    val korean: String = "",
    val translation: String = "",
    val detail: String = "",
    val isHighlighted: Boolean = false
)