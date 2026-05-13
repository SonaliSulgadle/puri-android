package com.puri.app.core.analytics

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.puri.app.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Crashlytics @Inject constructor() {

    /**
     * Record a non-fatal exception.
     * Shows in Firebase Crashlytics dashboard as a non-fatal issue.
     *
     * Use for: API failures, parse errors, unexpected null states
     * Do NOT use for: expected errors (network timeout, daily limit reached)
     */
    fun recordException(throwable: Throwable, context: String? = null) {
        if (BuildConfig.DEBUG) {
            Log.e("PuriCrashlytics", "Non-fatal [${context ?: "no context"}]", throwable)
            return
        }
        context?.let {
            Firebase.crashlytics.setCustomKey("error_context", it)
        }
        Firebase.crashlytics.recordException(throwable)
    }

    /**
     * Log a breadcrumb message.
     * Appears in crash logs to show what happened before a crash.
     *
     * Use for: marking key user actions before potential crash points
     */
    fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("PuriCrashlytics", "🍞 $message")
            return
        }
        Firebase.crashlytics.log(message)
    }

    /**
     * Set a key/value pair visible in all crash reports.
     * Use for: current screen, operation in progress, user state
     */
    fun setKey(key: String, value: String) {
        if (BuildConfig.DEBUG) return
        Firebase.crashlytics.setCustomKey(key, value)
    }

    /**
     * Record an API error as a non-fatal exception.
     * Groups API failures together in the dashboard.
     */
    fun recordApiError(
        endpoint: String,
        httpCode: Int,
        body: String? = null
    ) {
        val message = "HTTP $httpCode on $endpoint${body?.let { ": $it" } ?: ""}"
        recordException(ApiException(message), "api_error")
    }

    private class ApiException(message: String) : Exception(message)
}