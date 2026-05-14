package com.puri.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary: Celadon Green (The Ceramic Anchor) ───────
val CeladonPrimary = Color(0xFF5C8A6E)
val CeladonOnPrimary = Color(0xFFFFFFFF)
val CeladonPrimaryContainer = Color(0xFFDCE6DE)
val CeladonOnPrimaryContainer = Color(0xFF192B20)

// ── Secondary: Sage Muted (The Supporting Green) ─────
val SageSecondary = Color(0xFF4A6B56)
val SageOnSecondary = Color(0xFFFFFFFF)
val SageSecondaryContainer = Color(0xFFF0F5F0)
val SageOnSecondaryContainer = Color(0xFF0C1F14)

// ── Tertiary: Deep Moss (Snap & Solve Emphasis) ───────
val MossTertiary = Color(0xFF3E5C49)
val MossOnTertiary = Color(0xFFFFFFFF)

// ── Surface hierarchy (The "Editorial" Stack) ─────────
val Surface = Color(0xFFF6FAF6)                // Soft bone/ceramic base
val SurfaceContainerLow = Color(0xFFF0F5F0)    // Subtle sectioning
val SurfaceContainerLowest = Color(0xFFFFFFFF) // High-priority interactive cards
val SurfaceContainerHigh = Color(0xFFE8EDE8)   // Sunken inputs / grouping
val SurfaceContainer = Color(0xFFEDF2ED)       // Mid-level elevation

// ── Text (The "Scholar's Ink" System) ────────────────
val OnSurface = Color(0xFF1A1C1A)              // Near-black ink
val OnSurfaceVariant = Color(0xFF424942)       // Secondary editorial text
val OnSurfaceMuted = Color(0xFF727972)         // Placeholders and hints

// ── Semantic ─────────────────────────────────────────
val ErrorContainer = Color(0xFFF9EBEB)
val OnError = Color(0xFF702626)

// ── Dark mode surfaces (Ink & Charcoal) ──────────────
val SurfaceDark = Color(0xFF121412)            // Deep Ink Black
val SurfaceContainerLowDark = Color(0xFF1A1C1A)
val SurfaceContainerLowestDark = Color(0xFF0C0D0C)
val SurfaceContainerHighDark = Color(0xFF232623)
val OnSurfaceDark = Color(0xFFE2E3E2)
val OnSurfaceVariantDark = Color(0xFFC1C8C1)

// ── Gradient helpers (Polished & Professional) ───────
// Snap & Solve button: primary → moss (Subtle, smart depth)
val GradientSnapStart = Color(0xFF5C8A6E)
val GradientSnapEnd = Color(0xFF3E5C49)

// Hero card: primary → sage (The calm brand anchor)
val GradientHeroStart = Color(0xFF5C8A6E)
val GradientHeroEnd = Color(0xFF4A6B56)

val CeramicWhite = Color(0xFFF6FAF6)
val InkBlack = Color(0xFF121412)
