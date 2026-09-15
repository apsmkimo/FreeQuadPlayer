package com.example.quadvideoplayer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object VideoPermissions {
    fun requiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    fun hasReadAccess(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            requiredPermission(),
        ) == PackageManager.PERMISSION_GRANTED
    }
}
