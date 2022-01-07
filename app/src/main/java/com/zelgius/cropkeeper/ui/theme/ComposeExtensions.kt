package com.zelgius.cropkeeper.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx() = with(LocalDensity.current) { toPx() }

fun Int.toColor() = with(android.graphics.Color.valueOf(this)) {
    Color(red = red(), green = green(), blue = blue())
}