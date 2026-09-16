package de.mindtrack.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF486A5B),
    secondary = Color(0xFF6B5E73),
    surface = Color(0xFFFFFBFE),
    surfaceVariant = Color(0xFFF0ECEF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAACDBB),
    secondary = Color(0xFFD2C0DA)
)

object TrackerColors {
    val Red = Color(0xFFC94B4B)
    val Orange = Color(0xFFD9794C)
    val Yellow = Color(0xFFD5AB36)
    val LightGreen = Color(0xFF82A967)
    val Green = Color(0xFF4E9161)
    val Breakdown = Color(0xFF171717)
    val BreakdownAccent = Color(0xFFF2CD46)

    fun energy(level: Int): Color = when (level) {
        1 -> Red
        2 -> Orange
        3 -> Yellow
        4 -> LightGreen
        else -> Green
    }

    fun tension(level: Int): Color = when (level) {
        1 -> Green
        2 -> LightGreen
        3 -> Yellow
        4 -> Orange
        else -> Red
    }
}

@Composable
fun MindTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
