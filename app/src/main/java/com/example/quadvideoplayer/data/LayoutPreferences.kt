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

import android.content.Context

/**
 * Stores the last chosen [PlayerLayout] in SharedPreferences.
 * A missing key means the user has not picked a mode yet (show LauncherSelectionScreen).
 */
object LayoutPreferences {
    private const val PREFS_NAME = "tetraview_layout"
    private const val KEY_LAYOUT = "player_layout"

    fun load(context: Context): PlayerLayout? {
        val raw = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAYOUT, null)
        return PlayerLayout.fromPref(raw)
    }

    fun save(context: Context, layout: PlayerLayout) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAYOUT, layout.prefValue)
            .apply()
    }
}
