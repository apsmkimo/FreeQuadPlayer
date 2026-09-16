/*
 * TetraView / FreeQuadPlayer
 * Copyright (C) 2026 apsmkimo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.quadvideoplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.quadvideoplayer.R
import com.example.quadvideoplayer.data.LocalVideoStore
import kotlin.math.abs
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun CellPlaybackBar(
    player: ExoPlayer,
    onPickVideo: () -> Unit,
    modifier: Modifier = Modifier,
    // SMCPKG_SUPPORT>>>Cursor017
    onTogglePlay: (() -> Unit)? = null,
    // SMCPKG_SUPPORT<<<Cursor017
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
                // SMCPKG_SUPPORT>>>Cursor017
                // if (player.isPlaying) {
                //     player.pause()
                // } else {
                //     player.play()
                // }
                // Always drive the live ExoPlayer: playWhenReady + play() after swap.
                if (onTogglePlay != null) {
                    onTogglePlay()
                } else if (player.isPlaying) {
                    player.pause()
                } else {
                    player.playWhenReady = true
                    player.play()
                }
                // SMCPKG_SUPPORT<<<Cursor017
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
            volume = volume,
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
    volume: Float,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onVolumeDelta: (Float) -> Unit,
) {
    // SMCPKG_SUPPORT>>>Cursor007
    // Immediate drag-to-adjust without an overlay. Replaced by press-and-hold bar.
    var overlayVisible by remember { mutableStateOf(false) }
    val touchSlopPx = 16f
    val dragSensitivity = 220f
    val holdTimeoutMs = 260L

    Box(
        modifier = Modifier
            .size(40.dp)
            .zIndex(2f)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var overlayShown = false
                    var lastY = down.position.y
                    while (true) {
                        val event = if (overlayShown) {
                            awaitPointerEvent()
                        } else {
                            withTimeoutOrNull(holdTimeoutMs) { awaitPointerEvent() }
                        }
                        if (event == null) {
                            overlayShown = true
                            overlayVisible = true
                            continue
                        }
                        val change = event.changes.firstOrNull() ?: break
                        if (!change.pressed) {
                            if (!overlayShown) {
                                onToggleMute()
                            }
                            overlayVisible = false
                            break
                        }
                        val dy = change.position.y - lastY
                        if (!overlayShown && abs(change.position.y - down.position.y) > touchSlopPx) {
                            overlayShown = true
                            overlayVisible = true
                        }
                        if (overlayShown) {
                            onVolumeDelta(-dy / dragSensitivity)
                            change.consume()
                        }
                        lastY = change.position.y
                    }
                    overlayVisible = false
                }
            },
        contentAlignment = Alignment.BottomCenter,
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
        if (overlayVisible) {
            VerticalVolumeOverlay(
                volume = volume,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-44).dp),
            )
        }
    }
    // SMCPKG_SUPPORT<<<Cursor007
}

@Composable
private fun VerticalVolumeOverlay(
    volume: Float,
    modifier: Modifier = Modifier,
) {
    val fraction = volume.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .width(28.dp)
            .height(112.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black.copy(alpha = 0.82f))
            .padding(horizontal = 9.dp, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.18f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
            )
        }
    }
}

private fun resolvedDuration(player: Player): Long {
    val duration = player.duration
    return if (duration == C.TIME_UNSET || duration < 0L) 0L else duration
}
