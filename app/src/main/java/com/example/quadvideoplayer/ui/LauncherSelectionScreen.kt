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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.quadvideoplayer.R
import com.example.quadvideoplayer.data.PlayerLayout

@Composable
fun LauncherSelectionScreen(
    onLayoutSelected: (PlayerLayout) -> Unit,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (onCancel != null) {
        BackHandler(onBack = onCancel)
    }

    // SMCPKG_SUPPORT>>>Cursor019
    // Column(...) {
    //     Text(app_name); Text(layout_selection_title)
    //     LayoutOptionCard(title, body, preview, ...)
    // }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LayoutNameButton(
            label = stringResource(R.string.layout_2x2_grid),
            onClick = { onLayoutSelected(PlayerLayout.LANDSCAPE_2X2) },
        )
        LayoutNameButton(
            label = stringResource(R.string.layout_1x4_stack),
            onClick = { onLayoutSelected(PlayerLayout.VERTICAL_1X4) },
        )
        LayoutNameButton(
            label = stringResource(R.string.layout_1x2_vertical),
            onClick = { onLayoutSelected(PlayerLayout.VERTICAL_1X2) },
        )
        LayoutNameButton(
            label = stringResource(R.string.layout_2x1_horizontal),
            onClick = { onLayoutSelected(PlayerLayout.LANDSCAPE_2X1) },
        )
    }
    // SMCPKG_SUPPORT<<<Cursor019
}

@Composable
private fun LayoutNameButton(
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = label)
    }
}

// SMCPKG_SUPPORT>>>Cursor019
// private fun LayoutOptionCard(...) { title + body + preview }
// private fun VerticalStackPreview() { ... }
// private fun LandscapeGridPreview() { ... }
// private fun MiniPane(...) { ... }
// SMCPKG_SUPPORT<<<Cursor019
