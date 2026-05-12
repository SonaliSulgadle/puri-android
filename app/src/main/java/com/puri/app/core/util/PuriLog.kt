package com.puri.app.core.util

import android.util.Log
import com.puri.app.BuildConfig

object PuriLog {
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.d(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.e(tag, message, throwable)
    }

    fun w(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.w(tag, message)
    }
}