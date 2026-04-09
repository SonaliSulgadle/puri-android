package com.puri.app.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    fun formatRelativeTimestamp(timestamp: Long): String {
        val diffMs = System.currentTimeMillis() - timestamp
        val hours = diffMs / (1000 * 60 * 60)
        return when {
            hours < 1 -> "Just now"
            hours < 24 -> "${hours}h ago"
            hours < 48 -> "Yesterday"
            else -> SimpleDateFormat("MMM d", Locale.getDefault())
                .format(Date(timestamp))
        }
    }

    fun formatFullDate(timestamp: Long): String =
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            .format(Date(timestamp))

    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return fmt.format(Date(timestamp1)) == fmt.format(Date(timestamp2))
    }
}