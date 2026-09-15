package com.example.quadvideoplayer.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import com.example.quadvideoplayer.player.QuadPlayerController
import com.example.quadvideoplayer.util.VideoPermissions

private val Hairline = 1.dp
private val HairlineColor = Color(0xFF5A5A5A)

@Composable
fun QuadPlayerScreen() {
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
    var pickingIndex by remember { mutableIntStateOf(0) }
    var hasPermission by remember { mutableStateOf(VideoPermissions.hasReadAccess(context)) }
    val playingSnapshot = remember { MutableList(QuadPlayerController.PLAYER_COUNT) { false } }

    DisposableEffect(controller) {
        onDispose { controller.releaseAll() }
    }

    LifecycleStartEffect(controller) {
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

    videoUriStrings.forEachIndexed { index, uriString ->
        LaunchedEffect(uriString) {
            controller.setVideo(
                index = index,
                uri = uriString.takeIf { it.isNotEmpty() }?.let(Uri::parse),
                playWhenReady = uriString.isNotEmpty(),
            )
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted || VideoPermissions.hasReadAccess(context)
    }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        } catch (_: SecurityException) {
            // SAF may still grant session access even if persistable permission is unavailable.
        }
        videoUriStrings = videoUriStrings.toMutableList().also { list ->
            list[pickingIndex] = uri.toString()
        }
    }

    fun pickVideo(index: Int) {
        pickingIndex = index
        documentLauncher.launch(arrayOf("video/*"))
    }

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
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val cellHeight = (maxHeight - Hairline) / QuadPlayerController.GRID_COLUMNS
            LazyVerticalGrid(
                columns = GridCells.Fixed(QuadPlayerController.GRID_COLUMNS),
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false,
                horizontalArrangement = Arrangement.spacedBy(Hairline),
                verticalArrangement = Arrangement.spacedBy(Hairline),
            ) {
                items(QuadPlayerController.PLAYER_COUNT) { index ->
                    val uri = videoUriStrings[index]
                        .takeIf { it.isNotEmpty() }
                        ?.let(Uri::parse)
                    VideoCell(
                        index = index,
                        player = controller.players[index],
                        videoUri = uri,
                        onPickVideo = { pickVideo(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cellHeight),
                    )
                }
            }
        }

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
}
