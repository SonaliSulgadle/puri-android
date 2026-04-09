# ── Kotlin ────────────────────────────────────────────
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**

# ── Coroutines ────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ── Hilt / Dagger ─────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# ── Room ──────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.**

# ── Gemini AI SDK ─────────────────────────────────────
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# ── Retrofit / OkHttp (used internally by Gemini SDK) ─
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class okhttp3.** { *; }

# ── Coil ──────────────────────────────────────────────
-dontwarn coil.**

# ── DataStore ─────────────────────────────────────────
-keep class androidx.datastore.** { *; }

# ── Compose ───────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── Domain models — never obfuscate these ─────────────
# They get serialized/deserialized via Room and DataStore
-keep class com.puri.app.domain.model.** { *; }
-keep class com.puri.app.data.local.db.** { *; }

# ── Puri Application ──────────────────────────────────
-keep class com.puri.app.PuriApplication { *; }

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all Gemini request/response models — serialization needs field names
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers @kotlinx.serialization.Serializable class * {
    *** Companion;
    *** serialVersionUID;
    static ** $serializer;
    private *** $$delegate_0;
}

# Puri data models
-keep class com.puri.app.data.remote.model.** { *; }