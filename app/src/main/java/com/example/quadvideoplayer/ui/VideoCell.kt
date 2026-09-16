package com.example.quadvideoplayer.ui

import android.net.Uri
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
// SMCPKG_SUPPORT>>>Cursor004
// import androidx.compose.foundation.border
// SMCPKG_SUPPORT<<<Cursor004
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.quadvideoplayer.R

@OptIn(UnstableApi::class)
@Composable
fun VideoCell(
    index: Int,
    player: ExoPlayer,
    videoUri: Uri?,
    onPickVideo: () -> Unit,
    modifier: Modifier = Modifier,
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
        } else {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
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
                        this.player = player
                    }
                },
                update = { view ->
                    view.useController = false
                    // SMCPKG_SUPPORT>>>Cursor005
                    // view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    view.setBackgroundColor(android.graphics.Color.BLACK)
                    // SMCPKG_SUPPORT<<<Cursor005
                    view.player = player
                },
                onRelease = { view ->
                    view.player = null
                },
                modifier = Modifier.fillMaxSize(),
            )

            // Transparent tap target above PlayerView so Compose receives show/hide taps.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        controlsVisible = !controlsVisible
                    },
            )

            if (controlsVisible) {
                CellPlaybackBar(
                    player = player,
                    onPickVideo = onPickVideo,
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
