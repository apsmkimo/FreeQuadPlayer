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
// package com.example.quadvideoplayer.ui
package com.apsmkimo.tetraview.ui
// SMCPKG_SUPPORT<<<Cursor021

import android.app.Activity
import android.content.pm.ActivityInfo
// SMCPKG_SUPPORT>>>Cursor024
// import androidx.compose.foundation.layout.Box
// import androidx.compose.foundation.layout.fillMaxSize
// SMCPKG_SUPPORT<<<Cursor024
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
// SMCPKG_SUPPORT>>>Cursor024
// import androidx.compose.ui.Modifier
// SMCPKG_SUPPORT<<<Cursor024
import androidx.compose.ui.platform.LocalContext
// SMCPKG_SUPPORT>>>Cursor021
// import com.example.quadvideoplayer.data.LayoutPreferences
// import com.example.quadvideoplayer.data.PlayerLayout
import com.apsmkimo.tetraview.data.LayoutPreferences
import com.apsmkimo.tetraview.data.PlayerLayout
// SMCPKG_SUPPORT<<<Cursor021

/**
 * Every cold start shows [LauncherSelectionScreen]. The chosen layout is
 * in-session only (not written to prefs). Back-to-selection disposes the
 * player so media is released and the next layout choice starts empty.
 */
@Composable
fun TetraViewApp() {
    val context = LocalContext.current
    val activity = context as Activity
    // SMCPKG_SUPPORT>>>Cursor019
    // var persisted by remember { mutableStateOf(LayoutPreferences.load(context)) }
    // var layout by remember { mutableStateOf(persisted ?: PlayerLayout.LANDSCAPE_2X2) }
    // var showSelection by remember { mutableStateOf(persisted == null) }
    var layout by remember { mutableStateOf<PlayerLayout?>(null) }
    var showSelection by remember { mutableStateOf(true) }
    // SMCPKG_SUPPORT<<<Cursor019
    // SMCPKG_SUPPORT>>>Cursor021
    var showAbout by remember { mutableStateOf(false) }
    var showLicense by remember { mutableStateOf(false) }
    // SMCPKG_SUPPORT<<<Cursor021

    LaunchedEffect(Unit) {
        LayoutPreferences.clear(context)
    }

    fun applyLayout(newLayout: PlayerLayout) {
        // SMCPKG_SUPPORT>>>Cursor019
        // LayoutPreferences.save(context, newLayout)
        // persisted = newLayout
        // SMCPKG_SUPPORT<<<Cursor019
        layout = newLayout
        showSelection = false
        activity.requestedOrientation = newLayout.orientation
    }

    LaunchedEffect(showSelection, layout) {
        activity.requestedOrientation = if (showSelection && layout == null) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            layout?.orientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // SMCPKG_SUPPORT>>>Cursor019
    // if (persisted == null && showSelection) {
    //     LauncherSelectionScreen(onLayoutSelected = ::applyLayout)
    // } else { ... overlay picker ... }
    val selected = layout
    if (selected == null) {
        LauncherSelectionScreen(
            onLayoutSelected = ::applyLayout,
            onAbout = { showAbout = true },
        )
    } else {
        // SMCPKG_SUPPORT>>>Cursor024
        // Box(modifier = Modifier.fillMaxSize()) {
        //     QuadPlayerScreen(
        //         layout = selected,
        //         onChangeLayout = { showSelection = true },
        //     )
        //     if (showSelection) {
        //         LauncherSelectionScreen(
        //             onLayoutSelected = ::applyLayout,
        //             onCancel = { showSelection = false },
        //             onAbout = { showAbout = true },
        //         )
        //     }
        // }
        // Overlaying the picker left players running and remembered URIs.
        // Dispose QuadPlayerScreen (layout = null) so releaseAll runs.
        QuadPlayerScreen(
            layout = selected,
            onChangeLayout = {
                layout = null
                showSelection = true
            },
            // SMCPKG_SUPPORT>>>Cursor022
            // onAbout = { showAbout = true },
            // SMCPKG_SUPPORT<<<Cursor022
        )
        // SMCPKG_SUPPORT<<<Cursor024
    }
    // SMCPKG_SUPPORT<<<Cursor019
    // SMCPKG_SUPPORT>>>Cursor021
    if (showLicense) {
        LicenseTextScreen(onClose = { showLicense = false })
    } else if (showAbout) {
        AboutScreen(
            onClose = { showAbout = false },
            onViewLicense = { showLicense = true },
        )
    }
    // SMCPKG_SUPPORT<<<Cursor021
}
