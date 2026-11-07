package com.example.dontforget.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val onboardingBrandPrimary: Color,
    val onboardingBrandContainer: Color,
    val onboardingBrandAccent: Color,
    val onboardingSurfaceSubtle: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        onboardingBrandPrimary = Color(0xFF1B3B2B),
        onboardingBrandContainer = Color(0xFFE8F5E9),
        onboardingBrandAccent = Color(0xFF2E7D32),
        onboardingSurfaceSubtle = Color(0xFFFAF9F6),
    )
}

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
