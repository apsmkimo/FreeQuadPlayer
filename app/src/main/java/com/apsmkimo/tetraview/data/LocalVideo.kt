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
