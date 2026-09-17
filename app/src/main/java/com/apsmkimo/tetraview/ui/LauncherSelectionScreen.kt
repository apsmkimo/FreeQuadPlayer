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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
// SMCPKG_SUPPORT>>>Cursor021
// import com.example.quadvideoplayer.R
// import com.example.quadvideoplayer.data.PlayerLayout
import com.apsmkimo.tetraview.R
import com.apsmkimo.tetraview.data.PlayerLayout
// SMCPKG_SUPPORT<<<Cursor021

private val ScreenBlack = Color(0xFF000000)
private val LabelWhite = Color(0xFFFFFFFF)
// SMCPKG_SUPPORT>>>Cursor020
// private val GlassTop = Color(0x99313B4D)
// private val GlassBottom = Color(0xCC151A24)
// private val GlassStroke = Color(0x6698C4E8)
// private val GlassGlow = Color(0x554070A0)
// private val GlyphFill = Color(0xFFD5DCE8)
// SMCPKG_SUPPORT<<<Cursor020
// SMCPKG_SUPPORT>>>Cursor023
private val NeonBlue = Color(0xFF5BA8FF)
private val NeonYellow = Color(0xFFFFE14A)
private val NeonGreen = Color(0xFF3DDC84)
private val NeonPink = Color(0xFFFF6BCC)
// SMCPKG_SUPPORT<<<Cursor023

@Composable
fun LauncherSelectionScreen(
    onLayoutSelected: (PlayerLayout) -> Unit,
    onCancel: (() -> Unit)? = null,
    // SMCPKG_SUPPORT>>>Cursor021
    onAbout: () -> Unit = {},
    // SMCPKG_SUPPORT<<<Cursor021
    modifier: Modifier = Modifier,
) {
    if (onCancel != null) {
        BackHandler(onBack = onCancel)
    }

    // SMCPKG_SUPPORT>>>Cursor019
    // Column(...) { LayoutNameButton(...) x4 }
    // SMCPKG_SUPPORT<<<Cursor019
    // SMCPKG_SUPPORT>>>Cursor020
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBlack)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
        BoxWithConstraints {
            val gap = 18.dp
            val tile = min((maxWidth - gap) / 2, (maxHeight - gap) / 2)
                .coerceIn(128.dp, 220.dp)
            Column(
                verticalArrangement = Arrangement.spacedBy(gap),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    GlassLayoutTile(
                        tile = tile,
                        label = stringResource(R.string.layout_2x2_grid),
                        glyph = LayoutGlyph.GRID_2X2,
                        accent = NeonBlue,
                        onClick = { onLayoutSelected(PlayerLayout.LANDSCAPE_2X2) },
                    )
                    GlassLayoutTile(
                        tile = tile,
                        label = stringResource(R.string.layout_1x4_stack),
                        glyph = LayoutGlyph.STACK_1X4,
                        accent = NeonYellow,
                        onClick = { onLayoutSelected(PlayerLayout.VERTICAL_1X4) },
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    GlassLayoutTile(
                        tile = tile,
                        label = stringResource(R.string.layout_1x2_vertical),
                        glyph = LayoutGlyph.VERTICAL_1X2,
                        accent = NeonGreen,
                        onClick = { onLayoutSelected(PlayerLayout.VERTICAL_1X2) },
                    )
                    GlassLayoutTile(
                        tile = tile,
                        label = stringResource(R.string.layout_2x1_horizontal),
                        glyph = LayoutGlyph.HORIZONTAL_2X1,
                        accent = NeonPink,
                        onClick = { onLayoutSelected(PlayerLayout.LANDSCAPE_2X1) },
                    )
                }
            }
        }
        }
        // SMCPKG_SUPPORT>>>Cursor021
        IconButton(
            onClick = onAbout,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 4.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x33202A3A)),
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.about_title),
                tint = LabelWhite,
            )
        }
        // SMCPKG_SUPPORT<<<Cursor021
    }
    // SMCPKG_SUPPORT<<<Cursor020
}

// SMCPKG_SUPPORT>>>Cursor019
// private fun LayoutNameButton(label: String, onClick: () -> Unit) { Button(...) }
// SMCPKG_SUPPORT<<<Cursor019

// SMCPKG_SUPPORT>>>Cursor020
private enum class LayoutGlyph {
    GRID_2X2,
    STACK_1X4,
    VERTICAL_1X2,
    HORIZONTAL_2X1,
}

@Composable
private fun GlassLayoutTile(
    tile: Dp,
    label: String,
    glyph: LayoutGlyph,
    // SMCPKG_SUPPORT>>>Cursor023
    accent: Color,
    // SMCPKG_SUPPORT<<<Cursor023
    onClick: () -> Unit,
) {
    val corner = tile * 0.28f
    val shape = RoundedCornerShape(corner)
    val iconSize = tile * 0.42f
    Box(
        modifier = Modifier
            .size(tile)
            .shadow(
                elevation = 22.dp,
                shape = shape,
                ambientColor = accent.copy(alpha = 0.55f),
                spotColor = accent.copy(alpha = 0.40f),
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = 0.28f),
                        Color(0xCC10141C),
                    ),
                ),
            )
            .border(1.5.dp, accent.copy(alpha = 0.62f), shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = tile * 0.14f)
                .size(iconSize),
        ) {
            // SMCPKG_SUPPORT>>>Cursor023
            // drawLayoutGlyph(glyph)
            drawLayoutGlyph(glyph, accent)
            // SMCPKG_SUPPORT<<<Cursor023
        }
        Text(
            text = label,
            color = LabelWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = tile * 0.12f),
        )
    }
}

private fun DrawScope.drawLayoutGlyph(glyph: LayoutGlyph, accent: Color) {
    val w = size.width
    val h = size.height
    when (glyph) {
        LayoutGlyph.GRID_2X2 -> {
            val gap = w * 0.18f
            val cell = (w - gap) / 2f
            val radius = CornerRadius(cell * 0.38f)
            val xs = floatArrayOf(0f, cell + gap)
            val ys = floatArrayOf(0f, cell + gap)
            for (x in xs) {
                for (y in ys) {
                    drawNeonPane(accent, Offset(x, y), Size(cell, cell), radius)
                }
            }
        }
        LayoutGlyph.STACK_1X4 -> {
            val gap = h * 0.14f
            val barH = (h - 3f * gap) / 4f
            val radius = CornerRadius(barH * 0.5f)
            repeat(4) { index ->
                drawNeonPane(
                    accent,
                    Offset(0f, index * (barH + gap)),
                    Size(w, barH),
                    radius,
                )
            }
        }
        LayoutGlyph.VERTICAL_1X2 -> {
            val gap = h * 0.18f
            val barH = (h - gap) / 2f
            val radius = CornerRadius(barH * 0.48f)
            repeat(2) { index ->
                drawNeonPane(
                    accent,
                    Offset(0f, index * (barH + gap)),
                    Size(w, barH),
                    radius,
                )
            }
        }
        LayoutGlyph.HORIZONTAL_2X1 -> {
            val gap = w * 0.18f
            val barW = (w - gap) / 2f
            val radius = CornerRadius(barW * 0.48f)
            repeat(2) { index ->
                drawNeonPane(
                    accent,
                    Offset(index * (barW + gap), 0f),
                    Size(barW, h),
                    radius,
                )
            }
        }
    }
}

private fun DrawScope.drawNeonPane(
    accent: Color,
    topLeft: Offset,
    size: Size,
    corner: CornerRadius,
) {
    val glowPad = size.minDimension * 0.12f
    drawRoundRect(
        color = accent.copy(alpha = 0.28f),
        topLeft = Offset(topLeft.x - glowPad, topLeft.y - glowPad),
        size = Size(size.width + glowPad * 2f, size.height + glowPad * 2f),
        cornerRadius = CornerRadius(corner.x + glowPad),
    )
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.92f), accent),
            startY = topLeft.y,
            endY = topLeft.y + size.height,
        ),
        topLeft = topLeft,
        size = size,
        cornerRadius = corner,
    )
}
// SMCPKG_SUPPORT<<<Cursor020
