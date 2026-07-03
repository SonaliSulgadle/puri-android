# ════════════════════════════════════════════════════════
# PURI — ProGuard Rules
# Last updated: V1 release
# ════════════════════════════════════════════════════════

# ── GENERAL ATTRIBUTES ───────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable
# Rename to SourceFile so stack traces show file names not "Unknown Source"
-renamesourcefileattribute SourceFile

# ── KOTLIN ───────────────────────────────────────────────────────────────────
# Kotlin metadata for reflection-based libraries (Hilt, serialization)
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.**
# Kotlin coroutines internals — for dispatcher resolution at runtime
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# ── KOTLINX SERIALIZATION ────────────────────────────────────────────────────
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
# Keep serializer() method on all @Serializable classes
-keepclasseswithmembers class ** {
    @kotlinx.serialization.Serializable <methods>;
}
-keepclassmembers class ** implements kotlinx.serialization.KSerializer {
    public static final ** INSTANCE;
}

# Keep the generated serializer infrastructure for every @Serializable class
-keep @kotlinx.serialization.Serializable class * {
    *** Companion;
    static ** $serializer;
    static ** INSTANCE;
}
-keepclassmembers @kotlinx.serialization.Serializable class * {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
    private *** $$delegate_0;
}

# ── RETROFIT ─────────────────────────────────────────────────────────────────
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**
# Keep all @retrofit2.http.* annotated methods (GET, POST, etc.)
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Retrofit's Response/Call generics need signature info
-keepattributes Exceptions

# ── OKHTTP ───────────────────────────────────────────────────────────────────
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
# OkHttp's internal platform detection uses reflection
-keep class okhttp3.internal.platform.** { *; }

# ── HILT / DAGGER ────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel { *; }
-keep class **_HiltModules { *; }
-keep class **_HiltModules$* { *; }
-keep class *_ComponentTreeDeps { *; }
-keep class *_HiltComponents { *; }

# ── ROOM ─────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Database class * { *; }
# TypeConverters are called via reflection
-keep class * extends androidx.room.TypeConverter { *; }
-dontwarn androidx.room.**

# ── FIREBASE ─────────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
# Crashlytics needs class names to be preserved for stack trace symbolication
-keep public class * extends java.lang.Exception

# ── PURI ERROR TYPES ─────────────────────────────────────────────────────────
# Keep readable class names so error types show correctly in Crashlytics /
# Firebase Analytics (logged via javaClass.simpleName in release builds).
-keep class com.puri.app.core.common.PuriError { *; }
-keep class com.puri.app.core.common.PuriError$* { *; }

# ── PURI DATA MODELS ─────────────────────────────────────────────────────────
-keep class com.puri.app.domain.model.** { *; }
-keep class com.puri.app.data.local.db.** { *; }
-keep class com.puri.app.data.remote.model.** { *; }
-keep class com.puri.app.data.guides.** { *; }

# ── COIL ─────────────────────────────────────────────────────────────────────
-dontwarn coil.**
-keep class coil.** { *; }

# ── COMPOSE ──────────────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class androidx.compose.ui.tooling.** { *; }

# ── DATASTORE ────────────────────────────────────────────────────────────────
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# ── CAMERAX ──────────────────────────────────────────────────────────────────
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ── APPLICATION CLASS ─────────────────────────────────────────────────────────
-keep class com.puri.app.PuriApplication { *; }
-keep class com.puri.app.BuildConfig { *; }
