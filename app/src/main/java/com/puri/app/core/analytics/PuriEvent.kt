package com.puri.app.core.analytics

sealed class PuriEvent(val name: String) {

    // ── Screen views ──────────────────────────────────────────────────────
    data class ScreenViewed(val screen: String) : PuriEvent("screen_view")

    // ── Solve ─────────────────────────────────────────────────────────────
    data class SolveStarted(val type: String) : PuriEvent("solve_started")

    data class SolveCompleted(
        val type: String,
        val category: String,
        val confidence: String,
        val durationMs: Long
    ) : PuriEvent("solve_completed")

    data class SolveFailed(
        val type: String,
        val reason: String
    ) : PuriEvent("solve_failed")

    data object SolveResultSavedFromResult : PuriEvent("solve_result_saved")
    data object SolveRetried : PuriEvent("solve_retried")
    data object SolveDailyLimitReached : PuriEvent("daily_limit_reached")

    // ── Camera ────────────────────────────────────────────────────────────
    data object CameraOpened : PuriEvent("camera_opened")
    data object GalleryOpened : PuriEvent("gallery_opened")
    data object PhotoCaptured : PuriEvent("photo_captured")
    data object PhotoFromGallery : PuriEvent("photo_from_gallery")

    // ── Address ───────────────────────────────────────────────────────────
    data object AddressConverterOpened : PuriEvent("address_converter_opened")

    data class AddressConverted(
        val addressType: String,
        val confidence: String,
        val hasDetail: Boolean
    ) : PuriEvent("address_converted")

    data object AddressCopied : PuriEvent("address_copied")
    data object AddressOpenedNaver : PuriEvent("address_opened_naver")
    data object AddressOpenedKakao : PuriEvent("address_opened_kakao")
    data object WebToolOpened : PuriEvent("web_tool_opened")

    // ── Guides ────────────────────────────────────────────────────────────
    data class GuideOpened(
        val guideKey: String,
        val isPreBundled: Boolean
    ) : PuriEvent("guide_opened")

    data class GuideSection(
        val guideKey: String,
        val sectionType: String
    ) : PuriEvent("guide_section_viewed")

    // ── History ───────────────────────────────────────────────────────────
    data object HistoryItemOpened : PuriEvent("history_item_opened")
    data object HistorySavedFromDetail : PuriEvent("history_saved_from_detail")
    data object HistoryCleared : PuriEvent("history_cleared")

    // ── Onboarding ────────────────────────────────────────────────────────
    data class OnboardingPageViewed(val page: Int) : PuriEvent("onboarding_page_viewed")
    data class OnboardingCompleted(
        val skipped: Boolean,
        val pagesViewed: Int
    ) : PuriEvent("onboarding_completed")

    // ── Profile ───────────────────────────────────────────────────────────
    data object ProfileRateAppTapped : PuriEvent("profile_rate_app_tapped")
    data object ProfilePrivacyTapped : PuriEvent("profile_privacy_tapped")
    data object FeedbackOpened : PuriEvent("feedback_opened")

    // ── Save ──────────────────────────────────────────────────────────────
    data class ResultSaved(val category: String) : PuriEvent("result_saved")

    // ── Feature discovery ─────────────────────────────────────────────────
    data class FeatureDiscovered(val feature: String) : PuriEvent("feature_discovered")
}