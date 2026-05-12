package com.puri.app.core.common

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

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
