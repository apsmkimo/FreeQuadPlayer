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
import com.example.quadvideoplayer.player.QuadPlayerController

/**
 * Persisted player grid. Vertical 1x4 is portrait; landscape 2x2 keeps the original grid.
 */
enum class PlayerLayout(
    val prefValue: String,
    val columns: Int,
    val orientation: Int,
) {
    VERTICAL_1X4(
        prefValue = "vertical_1x4",
        columns = 1,
        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
    ),
    LANDSCAPE_2X2(
        prefValue = "landscape_2x2",
        columns = 2,
        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
    );

    val rows: Int
        get() = QuadPlayerController.PLAYER_COUNT / columns

    companion object {
        fun fromPref(value: String?): PlayerLayout? {
            return entries.firstOrNull { it.prefValue == value }
        }
    }
}
