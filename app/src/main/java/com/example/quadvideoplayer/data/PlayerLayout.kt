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

package com.example.quadvideoplayer.data

import android.content.pm.ActivityInfo

/**
 * In-session player grid. Not persisted. Portrait for stacked modes;
 * landscape for 2x2 and 2x1.
 */
enum class PlayerLayout(
    val columns: Int,
    val rows: Int,
    val paneCount: Int,
    val orientation: Int,
) {
    // SMCPKG_SUPPORT>>>Cursor019
    // VERTICAL_1X4(prefValue = "vertical_1x4", columns = 1, portrait)
    // LANDSCAPE_2X2(prefValue = "landscape_2x2", columns = 2, landscape)
    // val rows get() = PLAYER_COUNT / columns
    LANDSCAPE_2X2(
        columns = 2,
        rows = 2,
        paneCount = 4,
        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
    ),
    VERTICAL_1X4(
        columns = 1,
        rows = 4,
        paneCount = 4,
        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
    ),
    VERTICAL_1X2(
        columns = 1,
        rows = 2,
        paneCount = 2,
        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
    ),
    LANDSCAPE_2X1(
        columns = 2,
        rows = 1,
        paneCount = 2,
        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
    );
    // SMCPKG_SUPPORT<<<Cursor019

    companion object {
        // SMCPKG_SUPPORT>>>Cursor019
        // fun fromPref(value: String?): PlayerLayout? {
        //     return entries.firstOrNull { it.prefValue == value }
        // }
        // SMCPKG_SUPPORT<<<Cursor019
    }
}
