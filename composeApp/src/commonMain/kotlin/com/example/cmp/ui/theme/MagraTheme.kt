package com.example.cmp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Paleta de colores premium para MagraApp.
 */
object MagraColors {
    // Primarios - Teal / Cyan
    val Primary = Color(0xFF00BFA6)
    val PrimaryLight = Color(0xFF5DF2D6)
    val PrimaryDark = Color(0xFF008E76)

    // Secundarios - Deep Blue
    val Secondary = Color(0xFF1A237E)
    val SecondaryLight = Color(0xFF534BAE)
    val SecondaryDark = Color(0xFF000051)

    // Acentos
    val Accent = Color(0xFF00E5FF)
    val AccentWarm = Color(0xFFFF6B35)

    // Fondos
    val BackgroundDark = Color(0xFF0A0E27)
    val BackgroundDarkSecondary = Color(0xFF121638)
    val SurfaceDark = Color(0xFF1A1F42)
    val SurfaceElevated = Color(0xFF242952)

    // Textos
    val TextPrimary = Color(0xFFE8EAED)
    val TextSecondary = Color(0xFFB0B3B8)
    val TextMuted = Color(0xFF6B7280)

    // Categorías ACSM
    val CategoryAthlete = Color(0xFF00E676)
    val CategoryFitness = Color(0xFF00BFA6)
    val CategoryAverage = Color(0xFFFFB300)
    val CategoryOverweight = Color(0xFFFF5252)

    // Composición corporal
    val FatColor = Color(0xFFFF6B35)
    val FatColorLight = Color(0xFFFF8A50)
    val LeanColor = Color(0xFF00BFA6)
    val LeanColorLight = Color(0xFF5DF2D6)

    // Riesgo cardiovascular
    val RiskLow = Color(0xFF00E676)
    val RiskModerate = Color(0xFFFFEB3B)
    val RiskHigh = Color(0xFFFF9800)
    val RiskVeryHigh = Color(0xFFFF5252)

    // Glassmorphism
    val GlassBorder = Color(0x30FFFFFF)
    val GlassBackground = Color(0x15FFFFFF)
    val GlassBackgroundElevated = Color(0x20FFFFFF)
}

/**
 * Gradientes premium reutilizables.
 */
object MagraGradients {
    val Background = Brush.verticalGradient(
        colors = listOf(
            MagraColors.BackgroundDark,
            Color(0xFF0D1B3E),
            MagraColors.BackgroundDarkSecondary
        )
    )

    val Primary = Brush.horizontalGradient(
        colors = listOf(
            MagraColors.Primary,
            MagraColors.Accent
        )
    )

    val PrimaryVertical = Brush.verticalGradient(
        colors = listOf(
            MagraColors.Primary,
            MagraColors.PrimaryDark
        )
    )

    val Warm = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF6B35),
            Color(0xFFFF8A50)
        )
    )

    val CardGlass = Brush.verticalGradient(
        colors = listOf(
            Color(0x18FFFFFF),
            Color(0x08FFFFFF)
        )
    )

    val FatBar = Brush.horizontalGradient(
        colors = listOf(
            MagraColors.FatColor,
            MagraColors.FatColorLight
        )
    )

    val LeanBar = Brush.horizontalGradient(
        colors = listOf(
            MagraColors.LeanColor,
            MagraColors.LeanColorLight
        )
    )
}

/**
 * Esquema de colores oscuro personalizado para Material3.
 */
private val MagraDarkColorScheme = darkColorScheme(
    primary = MagraColors.Primary,
    onPrimary = Color.White,
    primaryContainer = MagraColors.PrimaryDark,
    onPrimaryContainer = MagraColors.PrimaryLight,
    secondary = MagraColors.Secondary,
    onSecondary = Color.White,
    secondaryContainer = MagraColors.SecondaryLight,
    onSecondaryContainer = Color.White,
    tertiary = MagraColors.Accent,
    onTertiary = Color.Black,
    background = MagraColors.BackgroundDark,
    onBackground = MagraColors.TextPrimary,
    surface = MagraColors.SurfaceDark,
    onSurface = MagraColors.TextPrimary,
    surfaceVariant = MagraColors.SurfaceElevated,
    onSurfaceVariant = MagraColors.TextSecondary,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    outline = MagraColors.GlassBorder
)

/**
 * Formas redondeadas premium.
 */
val MagraShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Tema principal de MagraApp.
 */
@Composable
fun MagraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MagraDarkColorScheme,
        shapes = MagraShapes,
        content = content
    )
}
