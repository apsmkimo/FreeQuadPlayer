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

// SMCPKG_SUPPORT>>>Cursor021
// package com.example.quadvideoplayer.ui
package com.apsmkimo.tetraview.ui
// SMCPKG_SUPPORT<<<Cursor021

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
// SMCPKG_SUPPORT>>>Cursor004
// import androidx.compose.foundation.border
// SMCPKG_SUPPORT<<<Cursor004
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.abs
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
// SMCPKG_SUPPORT>>>Cursor021
// import com.example.quadvideoplayer.R
import com.apsmkimo.tetraview.R
// SMCPKG_SUPPORT<<<Cursor021

@OptIn(UnstableApi::class)
@Composable
fun VideoCell(
    index: Int,
    player: ExoPlayer,
    videoUri: Uri?,
    onPickVideo: () -> Unit,
    modifier: Modifier = Modifier,
    // SMCPKG_SUPPORT>>>Cursor013
    attachSurface: Boolean = true,
    // SMCPKG_SUPPORT<<<Cursor013
    // SMCPKG_SUPPORT>>>Cursor014
    // onBindPlayerView: (PlayerView?) -> Unit = {},
    // SMCPKG_SUPPORT<<<Cursor014
    // SMCPKG_SUPPORT>>>Cursor015
    onAttachPlayerView: (PlayerView) -> Unit = {},
    onDetachPlayerView: (PlayerView) -> Unit = {},
    // SMCPKG_SUPPORT<<<Cursor015
    // SMCPKG_SUPPORT>>>Cursor017
    onTogglePlay: () -> Unit = {
        if (player.isPlaying) player.pause() else {
            player.playWhenReady = true
            player.play()
        }
    },
    // SMCPKG_SUPPORT<<<Cursor017
) {
    // SMCPKG_SUPPORT>>>Cursor004
    // val shape = RoundedCornerShape(12.dp)
    // val borderColor = if (isUnmuted) {
    //     UnmutedGold
    // } else {
    //     MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    // }
    // SMCPKG_SUPPORT<<<Cursor004
    var controlsVisible by rememberSaveable(videoUri?.toString()) { mutableStateOf(false) }
    // SMCPKG_SUPPORT>>>Cursor023
    var volumeOverlayVisible by remember { mutableStateOf(false) }
    var overlayVolume by remember { mutableFloatStateOf(player.volume.coerceIn(0f, 1f)) }
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    val volumeBarHeight = screenHeightDp * 0.8f
    val density = LocalDensity.current
    // SMCPKG_SUPPORT<<<Cursor023

    Box(
        modifier = modifier
            // SMCPKG_SUPPORT>>>Cursor004
            // .padding(4.dp)
            // .clip(shape)
            // .border(2.dp, borderColor, shape)
            // SMCPKG_SUPPORT<<<Cursor004
            // SMCPKG_SUPPORT>>>Cursor005
            // .background(MaterialTheme.colorScheme.background)
            .background(androidx.compose.ui.graphics.Color.Black)
            // SMCPKG_SUPPORT<<<Cursor005
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {
                if (videoUri == null) {
                    onPickVideo()
                } else {
                    controlsVisible = !controlsVisible
                }
            },
    ) {
        if (videoUri == null) {
            EmptyVideoPlaceholder(index = index)
        // SMCPKG_SUPPORT>>>Cursor013
        // SMCPKG_SUPPORT>>>Cursor017
        // } else if (!attachSurface) {
        //     // Keep a black placeholder while the picker is open so SurfaceView
        //     // does not punch through the overlay or sit in a zero-size lazy slot.
        //     Box(Modifier.fillMaxSize().background(Color.Black))
        // TextureView: stay mounted so the live ExoPlayer keeps (or re-gains) a surface.
        } else if (!attachSurface) {
            EmptyVideoPlaceholder(index = index)
        // SMCPKG_SUPPORT<<<Cursor017
        // SMCPKG_SUPPORT<<<Cursor013
        } else {
            AndroidView(
                factory = { context ->
                    // SMCPKG_SUPPORT>>>Cursor014
                    // PlayerView(context).apply { this.player = player }
                    (LayoutInflater.from(context).inflate(
                        R.layout.player_view_texture,
                        null,
                        false,
                    ) as PlayerView).apply {
                        useController = false
                        controllerAutoShow = false
                        // SMCPKG_SUPPORT>>>Cursor005
                        // resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        setBackgroundColor(android.graphics.Color.BLACK)
                        // SMCPKG_SUPPORT<<<Cursor005
                        setShutterBackgroundColor(android.graphics.Color.BLACK)
                        isClickable = false
                        isFocusable = false
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        // SMCPKG_SUPPORT>>>Cursor018
                        this.player = player
                        // SMCPKG_SUPPORT<<<Cursor018
                    }
                    // SMCPKG_SUPPORT<<<Cursor014
                },
                update = { view ->
                    view.useController = false
                    // SMCPKG_SUPPORT>>>Cursor005
                    // view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    view.setBackgroundColor(android.graphics.Color.BLACK)
                    // SMCPKG_SUPPORT<<<Cursor005
                    // SMCPKG_SUPPORT>>>Cursor014
                    // view.player = player
                    // onBindPlayerView(view)
                    // SMCPKG_SUPPORT<<<Cursor014
                    // SMCPKG_SUPPORT>>>Cursor015
                    // onAttachPlayerView(view)
                    // SMCPKG_SUPPORT<<<Cursor015
                    // SMCPKG_SUPPORT>>>Cursor018
                    // Always assign the live ExoPlayer here so first-pick prepare
                    // never races a PlayerView that has no player/surface.
                    view.player = player
                    onAttachPlayerView(view)
                    // SMCPKG_SUPPORT<<<Cursor018
                },
                onRelease = { view ->
                    // SMCPKG_SUPPORT>>>Cursor014
                    // onBindPlayerView(null)
                    // SMCPKG_SUPPORT<<<Cursor014
                    // SMCPKG_SUPPORT>>>Cursor015
                    onDetachPlayerView(view)
                    // SMCPKG_SUPPORT<<<Cursor015
                    view.player = null
                },
                modifier = Modifier.fillMaxSize(),
            )

            // SMCPKG_SUPPORT>>>Cursor023
            // Transparent tap target above PlayerView so Compose receives show/hide taps.
            // Box(Modifier.fillMaxSize().clickable { controlsVisible = !controlsVisible })
            // Press + vertical drag on the video surface adjusts this cell's volume.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .pointerInput(player) {
                        val slopPx = 16f
                        val fullTravelPx = with(density) { volumeBarHeight.toPx() }
                            .coerceAtLeast(1f)
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            var dragged = false
                            var lastY = down.position.y
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                if (!change.pressed) {
                                    if (!dragged) {
                                        controlsVisible = !controlsVisible
                                    }
                                    volumeOverlayVisible = false
                                    break
                                }
                                val dy = change.position.y - lastY
                                if (!dragged && abs(change.position.y - down.position.y) > slopPx) {
                                    dragged = true
                                    overlayVolume = player.volume.coerceIn(0f, 1f)
                                    volumeOverlayVisible = true
                                }
                                if (dragged) {
                                    val next = (player.volume - dy / fullTravelPx)
                                        .coerceIn(0f, 1f)
                                    player.volume = next
                                    overlayVolume = next
                                    change.consume()
                                }
                                lastY = change.position.y
                            }
                            volumeOverlayVisible = false
                        }
                    },
            ) {
                if (volumeOverlayVisible) {
                    SurfaceVolumeBar(
                        volume = overlayVolume,
                        barHeight = volumeBarHeight,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp),
                    )
                }
            }
            // SMCPKG_SUPPORT<<<Cursor023

            if (controlsVisible) {
                CellPlaybackBar(
                    player = player,
                    onPickVideo = onPickVideo,
                    // SMCPKG_SUPPORT>>>Cursor017
                    onTogglePlay = onTogglePlay,
                    // SMCPKG_SUPPORT<<<Cursor017
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }

        // SMCPKG_SUPPORT>>>Cursor004
        // CellOverlay(
        //     index = index,
        //     hasVideo = videoUri != null,
        //     isUnmuted = isUnmuted,
        //     onPickVideo = onPickVideo,
        //     modifier = Modifier.align(Alignment.TopStart),
        // )
        // SMCPKG_SUPPORT<<<Cursor004
    }
}

@Composable
private fun EmptyVideoPlaceholder(index: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Movie,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.cell_index, index + 1),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.placeholder_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.placeholder_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

// SMCPKG_SUPPORT>>>Cursor023
@Composable
private fun SurfaceVolumeBar(
    volume: Float,
    barHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    val fraction = volume.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .width(28.dp)
            .height(barHeight)
            .clip(RoundedCornerShape(14.dp))
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.82f))
            .padding(horizontal = 8.dp, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.18f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(8.dp))
                    .background(androidx.compose.ui.graphics.Color.White),
            )
        }
    }
}
// SMCPKG_SUPPORT<<<Cursor023

// SMCPKG_SUPPORT>>>Cursor004
// @Composable
// private fun CellOverlay(
//     index: Int,
//     hasVideo: Boolean,
//     isUnmuted: Boolean,
//     onPickVideo: () -> Unit,
//     modifier: Modifier = Modifier,
// ) { ... }
// SMCPKG_SUPPORT<<<Cursor004
