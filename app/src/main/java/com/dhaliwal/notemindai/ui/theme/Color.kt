package com.dhaliwal.notemindai.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// -- Core Brand Colors (Based on NoteMind Design) --
val BurgundyPrimary = Color(0xFF7F1D35)
val BurgundyPrimaryDark = Color(0xFF4C001C)
val BurgundyPrimaryLight = Color(0xFFFFB0C5)
val BurgundyContainerLight = Color(0xFFFFD9E1)
val BurgundyContainerDark = Color(0xFF650024)

val SecondaryTextLight = Color(0xFF74565F)
val SecondaryTextDark = Color(0xFFE2BDC6)

// -- Background & Surface Colors --
val BackgroundLight = Color(0xFFFBFDF9) // Very light off-white from design
val SurfaceLight = Color(0xFFFFFFFF)
val BackgroundDark = Color(0xFF101010) // True dark from your dark mode mockups
val SurfaceDark = Color(0xFF1E1E1E) // Slightly elevated for cards

// -- Text Colors --
val TextDarkPrimary = Color(0xFF1A1C19)
val TextLightPrimary = Color(0xFFE3E3E3)

// --- Light Color Scheme ---
val LightColorScheme = lightColorScheme(
    primary = BurgundyPrimary,
    onPrimary = Color.White,
    primaryContainer = BurgundyContainerLight,
    onPrimaryContainer = BurgundyPrimaryDark,

    secondary = SecondaryTextLight,
    onSecondary = Color.White,

    background = BackgroundLight,
    onBackground = TextDarkPrimary,
    surface = SurfaceLight,
    onSurface = TextDarkPrimary,

    surfaceVariant = Color(0xFFF0F0F0), // For subtle borders and unselected chips
    onSurfaceVariant = Color(0xFF534346)
)

// --- Dark Color Scheme ---
val DarkColorScheme = darkColorScheme(
    primary = BurgundyPrimaryLight,
    onPrimary = BurgundyPrimaryDark,
    primaryContainer = BurgundyContainerDark,
    onPrimaryContainer = BurgundyContainerLight,

    secondary = SecondaryTextDark,
    onSecondary = Color(0xFF422931),

    background = BackgroundDark,
    onBackground = TextLightPrimary,
    surface = SurfaceDark,
    onSurface = TextLightPrimary,

    surfaceVariant = Color(0xFF2C2C2C), // For dark mode chips/borders
    onSurfaceVariant = Color(0xFFD3C1C4)
)