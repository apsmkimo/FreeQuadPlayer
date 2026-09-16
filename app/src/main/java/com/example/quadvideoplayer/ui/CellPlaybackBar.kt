package com.example.quadvideoplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.quadvideoplayer.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun CellPlaybackBar(
    player: ExoPlayer,
    onPickVideo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPlaying by remember(player) { mutableStateOf(player.isPlaying) }
    var durationMs by remember(player) { mutableLongStateOf(resolvedDuration(player)) }
    var sliderValue by remember(player) { mutableFloatStateOf(player.currentPosition.toFloat()) }
    var isSeeking by remember { mutableStateOf(false) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                isPlaying = player.isPlaying
                val duration = resolvedDuration(player)
                if (duration > 0L) {
                    durationMs = duration
                }
                if (!isSeeking) {
                    sliderValue = player.currentPosition.toFloat()
                }
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LaunchedEffect(player, isPlaying, isSeeking) {
        while (isActive && isPlaying && !isSeeking) {
            val duration = resolvedDuration(player)
            if (duration > 0L) {
                durationMs = duration
            }
            sliderValue = player.currentPosition.toFloat()
            delay(200)
        }
    }

    val maxValue = durationMs.toFloat().coerceAtLeast(1f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.72f))
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            },
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = stringResource(
                    if (isPlaying) R.string.cell_pause else R.string.cell_play,
                ),
                tint = Color.White,
            )
        }

        Slider(
            value = sliderValue.coerceIn(0f, maxValue),
            onValueChange = { value ->
                isSeeking = true
                sliderValue = value
            },
            onValueChangeFinished = {
                player.seekTo(sliderValue.toLong().coerceAtLeast(0L))
                isSeeking = false
            },
            valueRange = 0f..maxValue,
            enabled = durationMs > 0L,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.35f),
                disabledThumbColor = Color.White.copy(alpha = 0.5f),
                disabledActiveTrackColor = Color.White.copy(alpha = 0.35f),
            ),
        )

        // SMCPKG_SUPPORT>>>Cursor005
        // TextButton(onClick = onPickVideo) {
        //     Text(
        //         text = stringResource(R.string.change_video),
        //         style = MaterialTheme.typography.labelLarge,
        //         color = Color.White,
        //     )
        // }
        IconButton(onClick = onPickVideo) {
            Icon(
                imageVector = Icons.Outlined.FolderOpen,
                contentDescription = stringResource(R.string.pick_video),
                tint = Color.White,
            )
        }
        // SMCPKG_SUPPORT<<<Cursor005
    }
}

private fun resolvedDuration(player: Player): Long {
    val duration = player.duration
    return if (duration == C.TIME_UNSET || duration < 0L) 0L else duration
}
