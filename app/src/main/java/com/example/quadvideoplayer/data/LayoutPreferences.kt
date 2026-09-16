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
 * Legacy SharedPreferences for layout. 1.0.9 no longer persists a choice;
 * every cold start shows the picker. [clear] wipes any leftover key.
 */
object LayoutPreferences {
    private const val PREFS_NAME = "tetraview_layout"
    private const val KEY_LAYOUT = "player_layout"

    // SMCPKG_SUPPORT>>>Cursor019
    // fun load(context: Context): PlayerLayout? {
    //     val raw = context.applicationContext
    //         .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    //         .getString(KEY_LAYOUT, null)
    //     return PlayerLayout.fromPref(raw)
    // }
    //
    // fun save(context: Context, layout: PlayerLayout) {
    //     context.applicationContext
    //         .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    //         .edit()
    //         .putString(KEY_LAYOUT, layout.prefValue)
    //         .apply()
    // }

    fun clear(context: Context) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_LAYOUT)
            .apply()
    }
    // SMCPKG_SUPPORT<<<Cursor019
}
