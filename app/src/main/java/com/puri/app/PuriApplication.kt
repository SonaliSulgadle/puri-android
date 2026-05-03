package com.puri.app

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PuriApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (!BuildConfig.DEBUG) {
            Firebase.crashlytics.isCrashlyticsCollectionEnabled = true
        }

        Firebase.analytics.setAnalyticsCollectionEnabled(true)
    }
}