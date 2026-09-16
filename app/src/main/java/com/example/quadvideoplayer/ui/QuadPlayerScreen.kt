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

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import com.example.quadvideoplayer.R
import com.example.quadvideoplayer.data.PlayerLayout
import com.example.quadvideoplayer.player.QuadPlayerController
import com.example.quadvideoplayer.util.VideoPermissions
import kotlinx.coroutines.delay

private val Hairline = 1.dp
private val HairlineColor = Color(0xFF5A5A5A)

// SMCPKG_SUPPORT>>>Cursor012
// @Composable
// fun QuadPlayerScreen() {
@Composable
fun QuadPlayerScreen(
    layout: PlayerLayout,
    onChangeLayout: () -> Unit,
) {
// SMCPKG_SUPPORT<<<Cursor012
    val context = LocalContext.current
    val controller = remember { QuadPlayerController(context) }

    var videoUriStrings by rememberSaveable {
        mutableStateOf(List(QuadPlayerController.PLAYER_COUNT) { "" })
    }
    // SMCPKG_SUPPORT>>>Cursor004
    // var unmutedIndex by rememberSaveable {
    //     mutableIntStateOf(QuadPlayerController.DEFAULT_UNMUTED_INDEX)
    // }
    // var isGlobalPlaying by rememberSaveable { mutableStateOf(false) }
    // SMCPKG_SUPPORT<<<Cursor004
    var pickingIndex by rememberSaveable { mutableIntStateOf(0) }
    var hasPermission by remember { mutableStateOf(VideoPermissions.hasReadAccess(context)) }
    var showPicker by remember { mutableStateOf(false) }
    var pendingPicker by remember { mutableStateOf(false) }
    val playingSnapshot = remember { MutableList(QuadPlayerController.PLAYER_COUNT) { false } }
    // SMCPKG_SUPPORT>>>Cursor013
    // var pendingPick by remember { mutableStateOf<Pair<Int, String>?>(null) }
    // val pickerResumeFlags = remember { MutableList(QuadPlayerController.PLAYER_COUNT) { false } }
    // var pausedForPicker by remember { mutableStateOf(false) }
    // SMCPKG_SUPPORT<<<Cursor013
    // SMCPKG_SUPPORT>>>Cursor014
    // var surfacesReady by remember { mutableStateOf(true) }
    // SMCPKG_SUPPORT<<<Cursor014

    DisposableEffect(controller) {
        onDispose { controller.releaseAll() }
    }

    // SMCPKG_SUPPORT>>>Cursor015
    LaunchedEffect(layout) {
        delay(32)
        controller.reattachAllBoundViews()
        controller.restorePlayback(
            List(QuadPlayerController.PLAYER_COUNT) { index ->
                controller.players.getOrNull(index)?.mediaItemCount?.let { it > 0 } == true
            },
        )
    }
    // SMCPKG_SUPPORT<<<Cursor015

    LifecycleStartEffect(controller) {
        controller.reattachAllBoundViews()
        controller.resumePlaying(playingSnapshot.toList())
        onStopOrDispose {
            val snapshot = controller.snapshotPlaying()
            snapshot.forEachIndexed { index, playing ->
                if (index < playingSnapshot.size) {
                    playingSnapshot[index] = playing
                }
            }
            controller.pauseAll()
        }
    }

    LifecycleResumeEffect(Unit) {
        hasPermission = VideoPermissions.hasReadAccess(context)
        onPauseOrDispose { }
    }

    // SMCPKG_SUPPORT>>>Cursor004
    // LaunchedEffect(unmutedIndex) {
    //     controller.setUnmuted(unmutedIndex)
    // }
    //
    // LaunchedEffect(isGlobalPlaying) {
    //     controller.setPlaying(isGlobalPlaying)
    // }
    // SMCPKG_SUPPORT<<<Cursor004

    // SMCPKG_SUPPORT>>>Cursor016
    // videoUriStrings.forEachIndexed { index, uriString ->
    //     LaunchedEffect(index, uriString) {
    //         controller.setVideo(index, uriString.takeIf { it.isNotEmpty() }?.let(Uri::parse),
    //             playWhenReady = uriString.isNotEmpty())
    //     }
    // }
    // Per-index effect: only THIS slot's URI. Empty slots do not call setVideo(null).
    repeat(QuadPlayerController.PLAYER_COUNT) { index ->
        val uriString = videoUriStrings.getOrElse(index) { "" }
        key(index) {
            LaunchedEffect(uriString) {
                if (uriString.isEmpty()) return@LaunchedEffect
                controller.setVideo(
                    index = index,
                    uri = Uri.parse(uriString),
                    playWhenReady = true,
                )
            }
        }
    }

    // LaunchedEffect(showPicker) { pauseAll(); surfacesReady = false; restorePlayback() }
    // SMCPKG_SUPPORT<<<Cursor016

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted || VideoPermissions.hasReadAccess(context)
        // SMCPKG_SUPPORT>>>Cursor005
        if (hasPermission && pendingPicker) {
            showPicker = true
            pendingPicker = false
        }
        // SMCPKG_SUPPORT<<<Cursor005
    }

    // SMCPKG_SUPPORT>>>Cursor005
    // val documentLauncher = rememberLauncherForActivityResult(
    //     contract = ActivityResultContracts.OpenDocument(),
    // ) { uri ->
    //     if (uri == null) return@rememberLauncherForActivityResult
    //     try {
    //         context.contentResolver.takePersistableUriPermission(
    //             uri,
    //             Intent.FLAG_GRANT_READ_URI_PERMISSION,
    //         )
    //     } catch (_: SecurityException) {
    //     }
    //     videoUriStrings = videoUriStrings.toMutableList().also { list ->
    //         list[pickingIndex] = uri.toString()
    //     }
    // }
    //
    // fun pickVideo(index: Int) {
    //     pickingIndex = index
    //     documentLauncher.launch(arrayOf("video/*"))
    // }
    fun applyPickedUri(uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        } catch (_: SecurityException) {
            // MediaStore URIs rely on READ_MEDIA_VIDEO / READ_EXTERNAL_STORAGE.
        }
        // SMCPKG_SUPPORT>>>Cursor013
        // videoUriStrings = videoUriStrings.toMutableList().also { list ->
        //     list[pickingIndex] = uri.toString()
        // }
        // showPicker = false
        // pendingPicker = false
        val index = pickingIndex
        showPicker = false
        pendingPicker = false
        if (index !in 0 until QuadPlayerController.PLAYER_COUNT) return
        // SMCPKG_SUPPORT>>>Cursor016
        // pendingPick = index to uri.toString()
        videoUriStrings = videoUriStrings.toMutableList().also { list ->
            list[index] = uri.toString()
        }
        // SMCPKG_SUPPORT<<<Cursor016
        // SMCPKG_SUPPORT<<<Cursor013
    }

    fun pickVideo(index: Int) {
        if (index !in 0 until QuadPlayerController.PLAYER_COUNT) return
        pickingIndex = index
        if (VideoPermissions.hasReadAccess(context)) {
            hasPermission = true
            showPicker = true
        } else {
            pendingPicker = true
            permissionLauncher.launch(VideoPermissions.requiredPermission())
        }
    }
    // SMCPKG_SUPPORT<<<Cursor005

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    // SMCPKG_SUPPORT>>>Cursor004
    // Scaffold(topBar = { TopAppBar(...) }) { innerPadding ->
    //     Column(Modifier.padding(innerPadding).padding(horizontal = 8.dp, vertical = 4.dp)) { ... }
    // }
    // SMCPKG_SUPPORT<<<Cursor004
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HairlineColor),
    ) {
        // SMCPKG_SUPPORT>>>Cursor013
        // BoxWithConstraints + LazyVerticalGrid used computed cellHeight. In 1x4
        // the 4th row could land on a clipped/zero lazy slot; PlayerView's
        // SurfaceView then died the process when the 4th video attached.
        // BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        //     val columns = layout.columns
        //     val rows = layout.rows
        //     val cellHeight = (maxHeight - Hairline * (rows - 1).coerceAtLeast(0)) / rows
        //     LazyVerticalGrid(...) { items(PLAYER_COUNT) { ... height(cellHeight) } }
        // }
        PlayerPaneGrid(
            layout = layout,
            modifier = Modifier.fillMaxSize(),
        ) { index ->
            val uri = videoUriStrings.getOrNull(index)
                ?.takeIf { it.isNotEmpty() }
                ?.let(Uri::parse)
            val player = controller.players.getOrNull(index) ?: return@PlayerPaneGrid
            VideoCell(
                index = index,
                player = player,
                videoUri = uri,
                onPickVideo = { pickVideo(index) },
                attachSurface = !(showPicker && index == pickingIndex),
                onAttachPlayerView = { view -> controller.attachPlayerView(index, view) },
                onDetachPlayerView = { view -> controller.detachPlayerView(index, view) },
                modifier = Modifier.fillMaxSize(),
            )
        }
        // SMCPKG_SUPPORT<<<Cursor013

        if (!showPicker) {
            // SMCPKG_SUPPORT>>>Cursor012
            IconButton(
                onClick = onChangeLayout,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = 8.dp, top = 4.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000)),
            ) {
                Icon(
                    imageVector = Icons.Filled.DashboardCustomize,
                    contentDescription = stringResource(R.string.change_layout),
                    tint = Color.White,
                )
            }
            // SMCPKG_SUPPORT<<<Cursor012
            PermissionBanner(
                granted = hasPermission,
                onGrantClick = {
                    permissionLauncher.launch(VideoPermissions.requiredPermission())
                },
                onSettingsClick = { openAppSettings() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .fillMaxWidth(),
            )
        }

        if (showPicker) {
            VideoPickerScreen(
                onVideoSelected = { uri -> applyPickedUri(uri) },
                onDismiss = {
                    showPicker = false
                    pendingPicker = false
                },
            )
        }
    }
}

// SMCPKG_SUPPORT>>>Cursor013
@Composable
private fun PlayerPaneGrid(
    layout: PlayerLayout,
    modifier: Modifier = Modifier,
    cell: @Composable (index: Int) -> Unit,
) {
    when (layout) {
        PlayerLayout.VERTICAL_1X4 -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(Hairline),
            ) {
                repeat(QuadPlayerController.PLAYER_COUNT) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        key(index) { cell(index) }
                    }
                }
            }
        }

        PlayerLayout.LANDSCAPE_2X2 -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(Hairline),
            ) {
                repeat(2) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(Hairline),
                    ) {
                        repeat(2) { col ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                            ) {
                                key(row * 2 + col) { cell(row * 2 + col) }
                            }
                        }
                    }
                }
            }
        }
    }
}
// SMCPKG_SUPPORT<<<Cursor013
