package com.puri.app.core.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.puri.app.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Analytics @Inject constructor() {

    private val firebase: FirebaseAnalytics by lazy { Firebase.analytics }

    fun log(event: PuriEvent) {
        val params = buildBundle(event)

        if (BuildConfig.DEBUG) {
            Log.d("PuriAnalytics", "📊 ${event.name} | ${bundleToString(params)}")
            firebase.logEvent(event.name, params)
            return
        }

        firebase.logEvent(event.name, params)
    }

    fun setScreen(screenName: String) {
        if (BuildConfig.DEBUG) {
            Log.d("PuriAnalytics", "📱 Screen: $screenName")
        }
        firebase.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        })
    }

    private fun buildBundle(event: PuriEvent): Bundle? = when (event) {

        is PuriEvent.ScreenViewed -> Bundle().apply {
            putString("screen_name", event.screen)
        }

        is PuriEvent.SolveStarted -> Bundle().apply {
            putString("type", event.type)
        }

        is PuriEvent.SolveCompleted -> Bundle().apply {
            putString("type", event.type)
            putString("category", event.category)
            putString("confidence", event.confidence)
            putLong("duration_ms", event.durationMs)
        }

        is PuriEvent.SolveFailed -> Bundle().apply {
            putString("type", event.type)
            putString("reason", event.reason)
        }

        is PuriEvent.GuideOpened -> Bundle().apply {
            putString("guide_key", event.guideKey)
            putBoolean("is_pre_bundled", event.isPreBundled)
        }

        is PuriEvent.GuideSection -> Bundle().apply {
            putString("guide_key", event.guideKey)
            putString("section_type", event.sectionType)
        }

        is PuriEvent.AddressConverted -> Bundle().apply {
            putString("address_type", event.addressType)
            putString("confidence", event.confidence)
            putBoolean("has_detail", event.hasDetail)
        }

        is PuriEvent.OnboardingPageViewed -> Bundle().apply {
            putInt("page", event.page)
        }

        is PuriEvent.OnboardingCompleted -> Bundle().apply {
            putBoolean("skipped", event.skipped)
            putInt("pages_viewed", event.pagesViewed)
        }

        is PuriEvent.ResultSaved -> Bundle().apply {
            putString("category", event.category)
        }

        is PuriEvent.FeatureDiscovered -> Bundle().apply {
            putString("feature", event.feature)
        }

        // Events with no params
        else -> null
    }

    private fun bundleToString(bundle: Bundle?): String {
        if (bundle == null) return "(no params)"
        return bundle.keySet().joinToString(", ") { key ->
            "$key=${bundle.get(key)}"
        }
    }
}