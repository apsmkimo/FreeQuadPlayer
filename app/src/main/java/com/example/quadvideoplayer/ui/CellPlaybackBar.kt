package com.example.quadvideoplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.quadvideoplayer.R
import com.example.quadvideoplayer.data.LocalVideoStore
import kotlin.math.abs
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
    var volume by remember(player) { mutableFloatStateOf(player.volume.coerceIn(0f, 1f)) }
    var lastAudibleVolume by remember(player) {
        mutableFloatStateOf(player.volume.takeIf { it > 0f } ?: 1f)
    }

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
                volume = player.volume.coerceIn(0f, 1f)
                if (volume > 0f) {
                    lastAudibleVolume = volume
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
    val elapsedMs = sliderValue.toLong().coerceAtLeast(0L)
    val isMuted = volume <= 0f

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

        // SMCPKG_SUPPORT>>>Cursor006
        VolumeGestureIcon(
            isMuted = isMuted,
            onToggleMute = {
                if (player.volume > 0f) {
                    lastAudibleVolume = player.volume
                    player.volume = 0f
                    volume = 0f
                } else {
                    val restored = lastAudibleVolume.takeIf { it > 0f } ?: 1f
                    player.volume = restored
                    volume = restored
                }
            },
            onVolumeDelta = { delta ->
                val next = (player.volume + delta).coerceIn(0f, 1f)
                player.volume = next
                volume = next
                if (next > 0f) {
                    lastAudibleVolume = next
                }
            },
        )

        Text(
            text = LocalVideoStore.formatDuration(durationMs),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier
                .widthIn(min = 40.dp)
                .padding(end = 4.dp),
        )
        // SMCPKG_SUPPORT<<<Cursor006

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

        // SMCPKG_SUPPORT>>>Cursor006
        Text(
            text = LocalVideoStore.formatDuration(elapsedMs),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .widthIn(min = 40.dp)
                .padding(start = 4.dp),
        )
        // SMCPKG_SUPPORT<<<Cursor006

        IconButton(onClick = onPickVideo) {
            Icon(
                imageVector = Icons.Outlined.FolderOpen,
                contentDescription = stringResource(R.string.pick_video),
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun VolumeGestureIcon(
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onVolumeDelta: (Float) -> Unit,
) {
    val touchSlopPx = 16f
    val dragSensitivity = 280f
    Box(
        modifier = Modifier
            .size(40.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var dragged = false
                    var lastY = down.position.y
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        if (!change.pressed) {
                            break
                        }
                        val dy = change.position.y - lastY
                        if (!dragged && abs(change.position.y - down.position.y) > touchSlopPx) {
                            dragged = true
                        }
                        if (dragged) {
                            // Finger up (negative dy) increases volume; down decreases it.
                            onVolumeDelta(-dy / dragSensitivity)
                            change.consume()
                        }
                        lastY = change.position.y
                    }
                    if (!dragged) {
                        onToggleMute()
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isMuted) {
                Icons.AutoMirrored.Filled.VolumeOff
            } else {
                Icons.AutoMirrored.Filled.VolumeUp
            },
            contentDescription = stringResource(
                if (isMuted) R.string.cell_unmute else R.string.cell_mute,
            ),
            tint = Color.White,
        )
    }
}

private fun resolvedDuration(player: Player): Long {
    val duration = player.duration
    return if (duration == C.TIME_UNSET || duration < 0L) 0L else duration
}
