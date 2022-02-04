package com.zelgius.cropkeeper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val md_theme_light_primary = Color(0xFF346a1f)
val md_theme_light_onPrimary = Color(0xFFffffff)
val md_theme_light_primaryContainer = Color(0xFFb5f397)
val md_theme_light_onPrimaryContainer = Color(0xFF032100)
val md_theme_light_secondary = Color(0xFF00639c)
val md_theme_light_onSecondary = Color(0xFFffffff)
val md_theme_light_secondaryContainer = Color(0xFF4475a5)
val md_theme_light_onSecondaryContainer = Color(0xFF001d33)
val md_theme_light_tertiary = Color(0xFF006b5d)
val md_theme_light_onTertiary = Color(0xFFffffff)
val md_theme_light_tertiaryContainer = Color(0xFF75f8e2)
val md_theme_light_onTertiaryContainer = Color(0xFF00201b)
val md_theme_light_error = Color(0xFFba1b1b)
val md_theme_light_errorContainer = Color(0xFFffdad4)
val md_theme_light_onError = Color(0xFFffffff)
val md_theme_light_onErrorContainer = Color(0xFF410001)
val md_theme_light_background = Color(0xFFfdfdf6)
val md_theme_light_onBackground = Color(0xFF1a1c18)
val md_theme_light_surface = Color(0xFFfdfdf6)
val md_theme_light_onSurface = Color(0xFF1a1c18)
val md_theme_light_surfaceVariant = Color(0xFFdfe4d6)
val md_theme_light_onSurfaceVariant = Color(0xFF43483f)
val md_theme_light_outline = Color(0xFF73796d)
val md_theme_light_inverseOnSurface = Color(0xFFf1f1ea)
val md_theme_light_inverseSurface = Color(0xFF2f312c)

val md_theme_dark_primary = Color(0xFF9ad77e)
val md_theme_dark_onPrimary = Color(0xFF083900)
val md_theme_dark_primaryContainer = Color(0xFF1c5206)
val md_theme_dark_onPrimaryContainer = Color(0xFFb5f397)
val md_theme_dark_secondary = Color(0xFF94ccff)
val md_theme_dark_onSecondary = Color(0xFF003354)
val md_theme_dark_secondaryContainer = Color(0xFF004a76)
val md_theme_dark_onSecondaryContainer = Color(0xFF004a76)
val md_theme_dark_tertiary = Color(0xFF55dbc6)
val md_theme_dark_onTertiary = Color(0xFF003730)
val md_theme_dark_tertiaryContainer = Color(0xFF005045)
val md_theme_dark_onTertiaryContainer = Color(0xFF75f8e2)
val md_theme_dark_error = Color(0xFFffb4a9)
val md_theme_dark_errorContainer = Color(0xFF930006)
val md_theme_dark_onError = Color(0xFF680003)
val md_theme_dark_onErrorContainer = Color(0xFFffdad4)
val md_theme_dark_background = Color(0xFF1a1c18)
val md_theme_dark_onBackground = Color(0xFFe3e3dc)
val md_theme_dark_surface = Color(0xFF1a1c18)
val md_theme_dark_onSurface = Color(0xFFe3e3dc)
val md_theme_dark_surfaceVariant = Color(0xFF43483f)
val md_theme_dark_onSurfaceVariant = Color(0xFFc3c9bb)
val md_theme_dark_outline = Color(0xFF8d9287)
val md_theme_dark_inverseOnSurface = Color(0xFF1a1c18)
val md_theme_dark_inverseSurface = Color(0xFFe3e3dc)

val seed = Color(0xFF33691e)
val error = Color(0xFFba1b1b)


val phaseColorSetLight = listOf(
    Color(0xFFD50000),
    Color(0xFFC51162),
    Color(0xFFAA00FF),
    Color(0xFF6200EA),
    Color(0xFF304FFE),
    Color(0xFF2962FF),
    Color(0xFF0091EA),
    Color(0xFF00B8D4),
    Color(0xFF00BFA5),
    Color(0xFF00C853),
    Color(0xFF64DD17),
    Color(0xFFAEEA00),
    Color(0xFFFFD600),
    Color(0xFFFFAB00),
    Color(0xFFFF6D00),
    Color(0xFFDD2600),
)

val phaseColorSetDark = listOf(
    Color(0xFFB71C1C),
    Color(0xFF880E4F),
    Color(0xFF4A148C),
    Color(0xFF311B92),
    Color(0xFF1A237E),
    Color(0xFF0D47A1),
    Color(0xFF01579B),
    Color(0xFF006064),
    Color(0xFF004D40),
    Color(0xFF1B5E20),
    Color(0xFF33691E),
    Color(0xFF827717),
    Color(0xFFF57F17),
    Color(0xFFFF6F00),
    Color(0xFFE65100),
    Color(0xFFBF360C),
)

val ColorScheme.phaseColorSet
    @Composable
    get() = if (!isSystemInDarkTheme()) phaseColorSetLight else phaseColorSetDark


@Composable
fun String.toColor() = with(android.graphics.Color.parseColor(this)) {
    val color = Color(this)
    (if (isSystemInDarkTheme()) phaseColorSetLight else phaseColorSetDark).indexOfFirst { it.red == color.red && it.blue == color.blue && it.green == it.green }
        .let {
            if (it >= 0) MaterialTheme.colorScheme.phaseColorSet[it]
            else color
        }
}