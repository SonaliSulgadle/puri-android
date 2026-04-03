package com.puri.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary: Deep Indigo ──────────────────────────────
val IndigoPrimary        = Color(0xFF4D51B1)
val IndigoOnPrimary      = Color(0xFFFFFFFF)
val IndigoPrimaryContainer = Color(0xFF9297FC)
val IndigoOnPrimaryContainer = Color(0xFF1A1D6E)

// ── Secondary: Vibrant Violet ─────────────────────────
val VioletSecondary      = Color(0xFF6A37D4)
val VioletOnSecondary    = Color(0xFFFFFFFF)
val VioletSecondaryContainer = Color(0xFFE8DEFF)
val VioletOnSecondaryContainer = Color(0xFF230070)

// ── Tertiary: Magenta (gradient end, Snap & Solve) ────
val MagentaTertiary      = Color(0xFFA02D70)
val MagentaOnTertiary    = Color(0xFFFFFFFF)

// ── Surface hierarchy (the "stacked paper" system) ────
val Surface              = Color(0xFFF5F6FC)   // base bg
val SurfaceContainerLow  = Color(0xFFEFF0F7)   // sectioning
val SurfaceContainerLowest = Color(0xFFFFFFFF) // interactive cards
val SurfaceContainerHigh = Color(0xFFE0E2EA)   // sunken inputs
val SurfaceContainer     = Color(0xFFE8EAF2)   // mid-level

// ── Text ──────────────────────────────────────────────
val OnSurface            = Color(0xFF2C2F33)   // no pure black
val OnSurfaceVariant     = Color(0xFF5A5F6E)   // secondary text
val OnSurfaceMuted       = Color(0xFF8B90A0)   // hints, placeholders

// ── Semantic ──────────────────────────────────────────
val WarningContainer     = Color(0xFFFFF3CD)
val OnWarning            = Color(0xFF7A4F00)
val ErrorContainer       = Color(0xFFFFEDED)
val OnError              = Color(0xFF8B1A1A)
val SuccessContainer     = Color(0xFFE6F4EA)
val OnSuccess            = Color(0xFF1A5C2A)

// ── Dark mode surfaces ────────────────────────────────
val SurfaceDark          = Color(0xFF12131A)
val SurfaceContainerLowDark = Color(0xFF1C1E2A)
val SurfaceContainerLowestDark = Color(0xFF0E0F14)
val SurfaceContainerHighDark = Color(0xFF252838)
val OnSurfaceDark        = Color(0xFFE4E5F0)
val OnSurfaceVariantDark = Color(0xFFA0A4B8)

// ── Gradient helpers (used directly in Brush calls) ───
// Snap & Solve button: secondary → tertiary
val GradientSnapStart    = Color(0xFF6A37D4)
val GradientSnapEnd      = Color(0xFFA02D70)
// Hero card: primary → primaryContainer
val GradientHeroStart    = Color(0xFF4D51B1)
val GradientHeroEnd      = Color(0xFF6B6FD4)