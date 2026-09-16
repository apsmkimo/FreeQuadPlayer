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

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.example.quadvideoplayer.R
import com.example.quadvideoplayer.data.LocalVideo
import com.example.quadvideoplayer.data.LocalVideoFolder
import com.example.quadvideoplayer.data.LocalVideoStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// SMCPKG_SUPPORT>>>Cursor013
private const val THUMB_WIDTH_PX = 512
private const val THUMB_HEIGHT_PX = 288
// SMCPKG_SUPPORT<<<Cursor013

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPickerScreen(
    onVideoSelected: (Uri) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var videos by remember { mutableStateOf<List<LocalVideo>?>(null) }
    // SMCPKG_SUPPORT>>>Cursor006
    // Flat all-videos grid replaced by folder-first navigation.
    var selectedFolder by remember { mutableStateOf<LocalVideoFolder?>(null) }
    // SMCPKG_SUPPORT<<<Cursor006

    LaunchedEffect(Unit) {
        videos = withContext(Dispatchers.IO) {
            LocalVideoStore.queryAll(context)
        }
    }

    // SMCPKG_SUPPORT>>>Cursor013
    DisposableEffect(Unit) {
        val imageLoader = context.imageLoader
        onDispose {
            imageLoader.memoryCache?.clear()
        }
    }
    // SMCPKG_SUPPORT<<<Cursor013

    val loaded = videos
    val folders = remember(loaded) {
        loaded?.let { LocalVideoStore.groupByFolder(it) }.orEmpty()
    }
    val folderVideos = remember(loaded, selectedFolder) {
        val folder = selectedFolder ?: return@remember emptyList()
        loaded.orEmpty().filter { it.bucketId == folder.bucketId }
    }

    BackHandler(enabled = selectedFolder != null) {
        selectedFolder = null
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = selectedFolder?.displayName
                                ?: stringResource(R.string.picker_folders_title),
                        )
                    },
                    navigationIcon = {
                        if (selectedFolder == null) {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.picker_close),
                                )
                            }
                        } else {
                            IconButton(onClick = { selectedFolder = null }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.picker_back),
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
            },
        ) { innerPadding ->
            when {
                loaded == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                loaded.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.picker_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                selectedFolder == null -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(folders, key = { it.bucketId }) { folder ->
                            FolderPickerItem(
                                folder = folder,
                                onClick = { selectedFolder = folder },
                            )
                        }
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(folderVideos, key = { it.id }) { video ->
                            VideoPickerItem(
                                video = video,
                                onClick = { onVideoSelected(video.uri) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderPickerItem(
    folder: LocalVideoFolder,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        VideoThumb(
            uri = folder.coverUri,
            contentDescription = folder.displayName,
        )
        Text(
            text = folder.displayName,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            text = stringResource(R.string.picker_folder_count, folder.videoCount),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun VideoPickerItem(
    video: LocalVideo,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Box {
            VideoThumb(
                uri = video.uri,
                contentDescription = video.displayName,
            )
            // SMCPKG_SUPPORT>>>Cursor007
            // Surface(
            //     color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f),
            //     ...
            //     color = MaterialTheme.colorScheme.onPrimary,
            // )
            Text(
                text = LocalVideoStore.formatDuration(video.durationMs),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
            // SMCPKG_SUPPORT<<<Cursor007
        }
        Text(
            text = video.displayName,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun VideoThumb(
    uri: Uri?,
    contentDescription: String,
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(uri)
            .decoderFactory(VideoFrameDecoder.Factory())
            .videoFrameMillis(0)
            // SMCPKG_SUPPORT>>>Cursor013
            // Cap decode size so folder grids do not keep full-resolution frames.
            .size(THUMB_WIDTH_PX, THUMB_HEIGHT_PX)
            // SMCPKG_SUPPORT<<<Cursor013
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        placeholder = ColorPainter(Color(0xFF2A2A2A)),
        error = ColorPainter(Color(0xFF2A2A2A)),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
    )
}
