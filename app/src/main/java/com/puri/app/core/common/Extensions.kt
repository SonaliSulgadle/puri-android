package com.puri.app.core.common

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun Bitmap.compressForGemini(): Bitmap {
    val maxDimension = 1024
    val scale = minOf(
        maxDimension.toFloat() / width,
        maxDimension.toFloat() / height,
        1f  // never upscale
    )
    return if (scale < 1f) {
        this.scale((width * scale).toInt(), (height * scale).toInt())
    } else this
}

// Convert Uri to String safely
fun android.net.Uri.toStringOrNull(): String? = this.toString().ifBlank { null }

fun Bitmap.toBase64(): String {
    val outputStream = ByteArrayOutputStream()
    // JPEG at 85% quality — good balance of size vs clarity for Gemini
    compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    val bytes = outputStream.toByteArray()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

fun Bitmap.saveToTempFile(context: Context): Uri? = try {
    val file = File(context.cacheDir, "puri_capture_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
} catch (e: Exception) {
    null
}

/**
 * Scales bitmap down to safe size for Gemini API.
 * Gemini accepts up to 1568px on longest side.
 * Target: ~1024px longest side = ~4MB uncompressed = safe for all devices.
 *
 * Also handles the OOM case where the original bitmap is already huge.
 * Recycles original bitmap after scaling.
 */
fun Bitmap.scaleToSafe(maxLongestSide: Int = 1024): Bitmap {
    val longestSide = maxOf(width, height)
    if (longestSide <= maxLongestSide) return this  // already safe

    val scale = maxLongestSide.toFloat() / longestSide
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    val scaled = this.scale(newWidth, newHeight)

    // Recycle original immediately to free the ~170MB
    if (scaled !== this) recycle()

    return scaled
}