package com.example.quadvideoplayer.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import com.example.quadvideoplayer.R
import com.example.quadvideoplayer.player.QuadPlayerController
import com.example.quadvideoplayer.util.VideoPermissions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuadPlayerScreen() {
    val context = LocalContext.current
    val controller = remember { QuadPlayerController(context) }

    var videoUriStrings by rememberSaveable {
        mutableStateOf(List(QuadPlayerController.PLAYER_COUNT) { "" })
    }
    var unmutedIndex by rememberSaveable {
        mutableIntStateOf(QuadPlayerController.DEFAULT_UNMUTED_INDEX)
    }
    var isGlobalPlaying by rememberSaveable { mutableStateOf(false) }
    var pickingIndex by remember { mutableIntStateOf(0) }
    var hasPermission by remember { mutableStateOf(VideoPermissions.hasReadAccess(context)) }

    DisposableEffect(controller) {
        onDispose { controller.releaseAll() }
    }

    LifecycleStartEffect(isGlobalPlaying, controller) {
        if (isGlobalPlaying) {
            controller.setPlaying(true)
        }
        onStopOrDispose {
            controller.pauseAll()
        }
    }

    LifecycleResumeEffect(Unit) {
        hasPermission = VideoPermissions.hasReadAccess(context)
        onPauseOrDispose { }
    }

    LaunchedEffect(unmutedIndex) {
        controller.setUnmuted(unmutedIndex)
    }

    LaunchedEffect(isGlobalPlaying) {
        controller.setPlaying(isGlobalPlaying)
    }

    videoUriStrings.forEachIndexed { index, uriString ->
        LaunchedEffect(uriString) {
            controller.setVideo(
                index = index,
                uri = uriString.takeIf { it.isNotEmpty() }?.let(Uri::parse),
                playWhenReady = isGlobalPlaying,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    FilledTonalButton(
                        onClick = { isGlobalPlaying = !isGlobalPlaying },
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Icon(
                            imageVector = if (isGlobalPlaying) {
                                Icons.Filled.Pause
                            } else {
                                Icons.Filled.PlayArrow
                            },
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(
                                if (isGlobalPlaying) {
                                    R.string.master_pause
                                } else {
                                    R.string.master_play
                                },
                            ),
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            PermissionBanner(
                granted = hasPermission,
                onGrantClick = {
                    permissionLauncher.launch(VideoPermissions.requiredPermission())
                },
                onSettingsClick = { openAppSettings() },
                modifier = Modifier.padding(bottom = 8.dp),
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                val cellHeight = maxHeight / QuadPlayerController.GRID_COLUMNS
                LazyVerticalGrid(
                    columns = GridCells.Fixed(QuadPlayerController.GRID_COLUMNS),
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false,
                ) {
                    items(QuadPlayerController.PLAYER_COUNT) { index ->
                        val uri = videoUriStrings[index]
                            .takeIf { it.isNotEmpty() }
                            ?.let(Uri::parse)
                        VideoCell(
                            index = index,
                            player = controller.players[index],
                            videoUri = uri,
                            isUnmuted = unmutedIndex == index,
                            onSelectUnmuted = { unmutedIndex = index },
                            onPickVideo = { pickVideo(index) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(cellHeight),
                        )
                    }
                }
            }
        }
    }
}
