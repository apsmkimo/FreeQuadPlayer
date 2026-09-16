package com.example.quadvideoplayer.data

import android.net.Uri

data class LocalVideo(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val durationMs: Long,
)
