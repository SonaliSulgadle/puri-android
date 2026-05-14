package com.puri.app.feature.onboarding

import androidx.compose.ui.graphics.Color
import com.puri.app.R

enum class PageType {
    IMAGE_TO_TEXT,
    TEXT_TO_TEXT,
    OFFLINE_GUIDES
}

data class PageResource(
    val backgroundGradient: List<Color>,
    val bigEmoji: String,
    val headlineRes: Int,
    val subtitleRes: Int,
    val accentColor: Color,
    val pageType: PageType
)

val pageResources = listOf(
    PageResource(
        backgroundGradient = listOf(
            Color(0xFF0C0D0C),
            Color(0xFF121C14),
            Color(0xFF1A2E20)
        ),
        bigEmoji = "📷",
        headlineRes = R.string.onboarding_page1_headline,
        subtitleRes = R.string.onboarding_page1_subtitle,
        accentColor = Color(0xFF7DB992),
        pageType = PageType.IMAGE_TO_TEXT
    ),
    PageResource(
        backgroundGradient = listOf(
            Color(0xFF0D0F0D),
            Color(0xFF141A14),
            Color(0xFF1C2A1E)
        ),
        bigEmoji = "💬",
        headlineRes = R.string.onboarding_page2_headline,
        subtitleRes = R.string.onboarding_page2_subtitle,
        accentColor = Color(0xFF90C4A0),
        pageType = PageType.TEXT_TO_TEXT
    ),
    PageResource(
        backgroundGradient = listOf(
            Color(0xFF0A0C0A),
            Color(0xFF111811),
            Color(0xFF192414)
        ),
        bigEmoji = "📚",
        headlineRes = R.string.onboarding_page3_headline,
        subtitleRes = R.string.onboarding_page3_subtitle,
        accentColor = Color(0xFF8FB89A),
        pageType = PageType.OFFLINE_GUIDES
    )
)