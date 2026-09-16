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
// package com.example.quadvideoplayer.data
package com.apsmkimo.tetraview.data
// SMCPKG_SUPPORT<<<Cursor021

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore

object LocalVideoStore {
    fun queryAll(context: Context): List<LocalVideo> {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            // SMCPKG_SUPPORT>>>Cursor006
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            // SMCPKG_SUPPORT<<<Cursor006
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val videos = mutableListOf<LocalVideo>()
        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder,
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                videos += LocalVideo(
                    id = id,
                    uri = ContentUris.withAppendedId(collection, id),
                    displayName = cursor.getString(nameColumn).orEmpty().ifBlank { "video_$id" },
                    durationMs = cursor.getLong(durationColumn).coerceAtLeast(0L),
                    bucketId = cursor.getLong(bucketIdColumn),
                    bucketDisplayName = cursor.getString(bucketNameColumn).orEmpty().ifBlank { "Folder" },
                )
            }
        }
        return videos
    }

    fun groupByFolder(videos: List<LocalVideo>): List<LocalVideoFolder> {
        return videos
            .groupBy { it.bucketId }
            .map { (bucketId, items) ->
                LocalVideoFolder(
                    bucketId = bucketId,
                    displayName = items.first().bucketDisplayName,
                    videoCount = items.size,
                    coverUri = items.first().uri,
                )
            }
            .sortedBy { it.displayName.lowercase() }
    }

    fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000L
        val hours = totalSeconds / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        val seconds = totalSeconds % 60L
        // SMCPKG_SUPPORT>>>Cursor006
        // return if (hours > 0L) {
        //     "%d:%02d:%02d".format(hours, minutes, seconds)
        // } else {
        //     "%d:%02d".format(minutes, seconds)
        // }
        return if (hours > 0L) {
            "%d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
        // SMCPKG_SUPPORT<<<Cursor006
    }
}
