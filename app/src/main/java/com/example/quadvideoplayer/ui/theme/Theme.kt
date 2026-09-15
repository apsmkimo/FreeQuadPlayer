package com.example.quadvideoplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CinemaColorScheme = darkColorScheme(
    primary = AccentGreen,
    onPrimary = CinemaBlack,
    secondary = AccentBlue,
    onSecondary = CinemaBlack,
    tertiary = UnmutedGold,
    background = CinemaBlack,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = CinemaSurface,
    onSurface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = CinemaCard,
    onSurfaceVariant = MutedGray,
)

@Composable
fun QuadVideoPlayerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CinemaColorScheme,
        typography = Typography,
        content = content,
    )
}
