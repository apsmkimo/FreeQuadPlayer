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

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.quadvideoplayer.data.LayoutPreferences
import com.example.quadvideoplayer.data.PlayerLayout

/**
 * First launch shows [LauncherSelectionScreen]. After a choice is persisted,
 * the matching player grid opens. Returning to selection overlays the picker
 * so the four players are not disposed.
 */
@Composable
fun TetraViewApp() {
    val context = LocalContext.current
    val activity = context as Activity
    var persisted by remember { mutableStateOf(LayoutPreferences.load(context)) }
    var layout by remember { mutableStateOf(persisted ?: PlayerLayout.LANDSCAPE_2X2) }
    var showSelection by remember { mutableStateOf(persisted == null) }

    fun applyLayout(newLayout: PlayerLayout) {
        LayoutPreferences.save(context, newLayout)
        persisted = newLayout
        layout = newLayout
        showSelection = false
        activity.requestedOrientation = newLayout.orientation
    }

    LaunchedEffect(showSelection, layout) {
        activity.requestedOrientation = if (showSelection) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            layout.orientation
        }
    }

    if (persisted == null && showSelection) {
        LauncherSelectionScreen(onLayoutSelected = ::applyLayout)
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            QuadPlayerScreen(
                layout = layout,
                onChangeLayout = { showSelection = true },
            )
            if (showSelection) {
                LauncherSelectionScreen(
                    onLayoutSelected = ::applyLayout,
                    onCancel = { showSelection = false },
                )
            }
        }
    }
}
