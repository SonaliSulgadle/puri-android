package com.puri.app.core.common

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Base64
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

fun Bitmap.scaleToSafe(maxLongestSide: Int = 1024): Bitmap {
    val longestSide = maxOf(width, height)
    if (longestSide <= maxLongestSide) return this
    val scale = maxLongestSide.toFloat() / longestSide
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()
    val scaled = this.scale(newWidth, newHeight)
    if (scaled !== this && !isRecycled) recycle()
    return scaled
}

// Keep return type Bitmap — existing callers expect this
fun Bitmap.compressForGemini(): Bitmap {
    // Reduce max from 1024 to 800 — smaller upload, less timeout risk
    val maxDimension = 800
    val scale = minOf(
        maxDimension.toFloat() / width,
        maxDimension.toFloat() / height,
        1f
    )
    return if (scale < 1f) {
        this.scale((width * scale).toInt(), (height * scale).toInt())
    } else this
}

// Keep return type String — existing callers expect this
fun Bitmap.toBase64(): String {
    val outputStream = ByteArrayOutputStream()
    // Reduce from 85% to 75% — meaningful size reduction, Gemini reads text fine at 75%
    compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
    val bytes = outputStream.toByteArray()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

// Extension on Bitmap to fix sensor rotation
// CameraX captures at sensor orientation — which is landscape for most phones
// When user holds phone portrait, the image comes out rotated 90°
// sensorRotation: degrees the sensor is rotated relative to natural orientation
// 0 = sensor matches display, 90 = sensor is 90° clockwise from display
fun Bitmap.fixRotationFromDisplay(sensorRotation: Int): Bitmap {
    if (sensorRotation == 0) return this
    val matrix = Matrix().apply { postRotate(sensorRotation.toFloat()) }
    val rotated = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    if (rotated !== this && !isRecycled) recycle()
    return rotated
}