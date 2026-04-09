package com.puri.app.core.common

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

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