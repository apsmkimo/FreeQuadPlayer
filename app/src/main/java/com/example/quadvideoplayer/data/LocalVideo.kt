package com.example.quadvideoplayer.data

import android.net.Uri

data class LocalVideo(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val durationMs: Long,
    // SMCPKG_SUPPORT>>>Cursor006
    val bucketId: Long,
    val bucketDisplayName: String,
    // SMCPKG_SUPPORT<<<Cursor006
)

data class LocalVideoFolder(
    val bucketId: Long,
    val displayName: String,
    val videoCount: Int,
    val coverUri: Uri?,
)
