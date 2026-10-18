package com.dhaliwal.notemindai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = RosePrimaryLight,
    onPrimary = RoseSurfaceLight,
    secondary = RoseSecondaryLight,
    background = RoseBackgroundLight,
    onBackground = RoseTextLight,
    surface = RoseSurfaceLight,
    onSurface = RoseTextLight,
    outline = Color(0xFFEBDDE0)
)

private val DarkColors = darkColorScheme(
    primary = RosePrimaryDark,
    onPrimary = Color(0xFF35151F),
    secondary = RoseSecondaryDark,
    background = RoseBackgroundDark,
    onBackground = RoseTextDark,
    surface = RoseSurfaceDark,
    onSurface = RoseTextDark,
    outline = Color(0xFF49343C)
)
@Composable
fun NoteMindAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}