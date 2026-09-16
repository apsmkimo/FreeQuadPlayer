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

package com.example.quadvideoplayer

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.quadvideoplayer.ui.QuadPlayerScreen
import com.example.quadvideoplayer.ui.theme.QuadVideoPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // SMCPKG_SUPPORT>>>Cursor005
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // SMCPKG_SUPPORT<<<Cursor005
        // SMCPKG_SUPPORT>>>Cursor004
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        // SMCPKG_SUPPORT<<<Cursor004
        setContent {
            QuadVideoPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuadPlayerScreen()
                }
            }
        }
    }
}
