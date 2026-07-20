package com.example.cmp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import kotlin.math.roundToInt
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cmp.data.ActivityLevel
import com.example.cmp.data.BodyCompositionResult
import com.example.cmp.data.BodyTypeCategory
import com.example.cmp.data.Gender
import com.example.cmp.ui.theme.MagraColors
import com.example.cmp.ui.theme.MagraGradients

// ═══════════════════════════════════════════════════════════════
// GLASS CARD - Tarjeta con efecto glassmorphism
// ═══════════════════════════════════════════════════════════════

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    elevated: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val bgColor = if (elevated) MagraColors.GlassBackgroundElevated else MagraColors.GlassBackground
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .border(
                width = 1.dp,
                color = MagraColors.GlassBorder,
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(20.dp),
        content = content
    )
}

// ═══════════════════════════════════════════════════════════════
// GRADIENT BUTTON - Botón con gradiente premium
// ═══════════════════════════════════════════════════════════════

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = MagraGradients.Primary,
    enabled: Boolean = true
) {
    val alpha = if (enabled) 1f else 0.5f

    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(gradient, alpha = alpha)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// ANIMATED CIRCULAR PROGRESS - Indicador circular animado
// ═══════════════════════════════════════════════════════════════

@Composable
fun AnimatedCircularProgress(
    percentage: Float,
    label: String,
    valueText: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    strokeWidth: Dp = 12.dp
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 100f),
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    val trackColor = Color.White.copy(alpha = 0.08f)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepAngle = (animatedPercentage / 100f) * 360f
            val stroke = strokeWidth.toPx()
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(stroke / 2f, stroke / 2f)

            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = valueText,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = MagraColors.TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// BODY COMPOSITION BAR - Barra grasa vs magra
// ═══════════════════════════════════════════════════════════════

@Composable
fun BodyCompositionBar(
    fatPercentage: Float,
    fatKg: Float,
    leanKg: Float,
    modifier: Modifier = Modifier
) {
    val animatedFat by animateFloatAsState(
        targetValue = fatPercentage.coerceIn(0f, 100f),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Etiquetas superiores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Masa Grasa",
                    color = MagraColors.FatColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${fatKg} kg (${roundToOneDecimal(animatedFat)}%)",
                    color = MagraColors.FatColorLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Masa Magra",
                    color = MagraColors.LeanColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${leanKg} kg (${roundToOneDecimal(100f - animatedFat)}%)",
                    color = MagraColors.LeanColorLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Barra de composición
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
        ) {
            // Fondo (magra)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MagraGradients.LeanBar)
            )
            // Grasa (sobre magra)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedFat / 100f)
                    .background(MagraGradients.FatBar)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// METRIC CARD - Tarjeta individual de métrica
// ═══════════════════════════════════════════════════════════════

@Composable
fun MetricCard(
    emoji: String,
    title: String,
    value: String,
    subtitle: String = "",
    accentColor: Color = MagraColors.Primary,
    modifier: Modifier = Modifier,
    onInfoClick: (() -> Unit)? = null
) {
    GlassCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Emoji en círculo arriba
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = MagraColors.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                if (onInfoClick != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f))
                            .clickable(onClick = onInfoClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "i",
                            color = accentColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Valor principal
            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Subtítulo
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// TREND CHART - Gráfico de tendencia con Canvas
// ═══════════════════════════════════════════════════════════════

@Composable
fun TrendChart(
    values: List<Float>,
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    if (values.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MagraColors.TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.03f))
        ) {
            if (values.size < 2) {
                // Solo un punto: dibujar una línea horizontal
                val y = size.height / 2f
                drawLine(
                    color = color,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = color,
                    radius = 6f,
                    center = Offset(size.width / 2f, y)
                )
                return@Canvas
            }

            val minVal = values.min()
            val maxVal = values.max()
            val range = if (maxVal - minVal > 0.01f) maxVal - minVal else 1f
            val padding = 16f

            val points = values.mapIndexed { index, value ->
                val x = padding + (size.width - 2 * padding) * index / (values.size - 1).toFloat()
                val y = padding + (size.height - 2 * padding) * (1f - (value - minVal) / range)
                Offset(x, y)
            }

            // Área bajo la curva
            val path = Path().apply {
                moveTo(points.first().x, size.height)
                lineTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val cx = (prev.x + curr.x) / 2f
                    cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                }
                lineTo(points.last().x, size.height)
                close()
            }

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.3f),
                        color.copy(alpha = 0.0f)
                    )
                )
            )

            // Línea
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val cx = (prev.x + curr.x) / 2f
                    cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                }
            }

            drawPath(
                path = linePath,
                color = color,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )

            // Puntos
            points.forEach { point ->
                drawCircle(color = color, radius = 5f, center = point)
                drawCircle(color = Color.White, radius = 3f, center = point)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// MODE TOGGLE - Toggle estilizado rápido/avanzado
// ═══════════════════════════════════════════════════════════════

@Composable
fun ModeToggle(
    isAdvanced: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, MagraColors.GlassBorder, shape)
    ) {
        // Modo Rápido
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(shape)
                .background(
                    if (!isAdvanced) MagraColors.Primary.copy(alpha = 0.2f)
                    else Color.Transparent
                )
                .clickable { onToggle(false) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚡ Rápido",
                color = if (!isAdvanced) MagraColors.Primary else MagraColors.TextMuted,
                fontSize = 14.sp,
                fontWeight = if (!isAdvanced) FontWeight.Bold else FontWeight.Normal
            )
        }

        // Modo Avanzado
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(shape)
                .background(
                    if (isAdvanced) MagraColors.Primary.copy(alpha = 0.2f)
                    else Color.Transparent
                )
                .clickable { onToggle(true) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎯 Avanzado",
                color = if (isAdvanced) MagraColors.Primary else MagraColors.TextMuted,
                fontSize = 14.sp,
                fontWeight = if (isAdvanced) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// STYLED TEXT FIELD - Campo de texto estilizado
// ═══════════════════════════════════════════════════════════════

@Composable
fun MagraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String = "",
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // Solo permitir números y punto decimal
            val filtered = newValue.filter { it.isDigit() || it == '.' }
            onValueChange(filtered)
        },
        label = { Text(label) },
        suffix = if (suffix.isNotEmpty()) {
            { Text(suffix, color = MagraColors.TextMuted) }
        } else null,
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MagraColors.Primary,
            unfocusedBorderColor = MagraColors.GlassBorder,
            focusedLabelColor = MagraColors.Primary,
            unfocusedLabelColor = MagraColors.TextMuted,
            cursorColor = MagraColors.Primary,
            focusedTextColor = Color.White,
            unfocusedTextColor = MagraColors.TextPrimary,
            errorBorderColor = Color(0xFFFF5252),
            errorLabelColor = Color(0xFFFF5252)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    )
}

// ═══════════════════════════════════════════════════════════════
// ACTIVITY LEVEL SELECTOR - Selector de nivel de actividad física
// ═══════════════════════════════════════════════════════════════

@Composable
fun ActivityLevelSelector(
    selected: ActivityLevel,
    onSelected: (ActivityLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ActivityLevel.entries.forEach { level ->
            val isSelected = selected == level

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) MagraColors.Primary.copy(alpha = 0.15f)
                        else MagraColors.Primary.copy(alpha = 0.06f)
                    )
                    .border(
                        1.5.dp,
                        if (isSelected) MagraColors.Primary else MagraColors.GlassBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelected(level) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = level.emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = level.displayName,
                        color = if (isSelected) Color.White else MagraColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = level.description,
                        color = MagraColors.TextMuted,
                        fontSize = 12.sp
                    )
                }
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(MagraColors.Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// SECTION HEADER
// ═══════════════════════════════════════════════════════════════

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        color = MagraColors.TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

// ═══════════════════════════════════════════════════════════════
// HELPERS
// ═══════════════════════════════════════════════════════════════

/**
 * Redondea un Float a 1 decimal para mostrar porcentajes consistentes.
 */
private fun roundToOneDecimal(value: Float): String {
    val rounded = (value * 10).roundToInt() / 10f
    return if (rounded == rounded.toInt().toFloat()) {
        "${rounded.toInt()}.0"
    } else {
        "$rounded"
    }
}

// ═══════════════════════════════════════════════════════════════
// BACK ICON - Ícono de regresar dibujado con Canvas
// ═══════════════════════════════════════════════════════════════

@Composable
fun BackIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val width = size.width
        val height = size.height
        val strokeWidthPx = 2.5.dp.toPx()

        // Línea horizontal
        drawLine(
            color = color,
            start = Offset(width * 0.25f, height * 0.5f),
            end = Offset(width * 0.75f, height * 0.5f),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )

        // Línea superior de la flecha
        drawLine(
            color = color,
            start = Offset(width * 0.25f, height * 0.5f),
            end = Offset(width * 0.5f, height * 0.25f),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )

        // Línea inferior de la flecha
        drawLine(
            color = color,
            start = Offset(width * 0.25f, height * 0.5f),
            end = Offset(width * 0.5f, height * 0.75f),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// BODY TYPE MATRIX CARD - Matriz 2D (IMC vs %GC)
// ═══════════════════════════════════════════════════════════════

@Composable
fun BodyTypeMatrixCard(
    result: BodyCompositionResult,
    modifier: Modifier = Modifier
) {
    val gender = result.gender
    val currentType = result.bodyTypeCategory
    val isMale = gender == Gender.MALE

    GlassCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Cabecera con título y badge de género
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "MATRIZ DE COMPOSICIÓN 2D",
                        color = MagraColors.Accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Clasificación Morfofuncional",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                // Badge de género
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isMale) Brush.linearGradient(listOf(Color(0xFF0088FF), Color(0xFF00C6FF)))
                            else Brush.linearGradient(listOf(Color(0xFFFF007A), Color(0xFFFF6584)))
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isMale) "MASCULINO" else "FEMENINO",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        softWrap = false,
                        maxLines = 1
                    )
                }
            }

            // Resultado actual del usuario
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MagraColors.GlassBackgroundElevated)
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(MagraColors.Accent, MagraColors.SecondaryLight)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = currentType.emoji,
                        fontSize = 32.sp
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "TU BIOTIPO: ",
                                color = MagraColors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentType.getDisplayName(gender).uppercase(),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentType.description,
                            color = MagraColors.TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Grilla 2x2 de la Matriz 2D
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Fila 1: Delgado vs Voluminoso
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MatrixItem(
                        category = BodyTypeCategory.SLIM,
                        gender = gender,
                        isSelected = currentType == BodyTypeCategory.SLIM,
                        modifier = Modifier.weight(1f)
                    )
                    MatrixItem(
                        category = BodyTypeCategory.BULKY,
                        gender = gender,
                        isSelected = currentType == BodyTypeCategory.BULKY,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Fila 2: Delgado con Grasa vs Grasa Corporal Alta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MatrixItem(
                        category = BodyTypeCategory.SKINNY_FAT,
                        gender = gender,
                        isSelected = currentType == BodyTypeCategory.SKINNY_FAT,
                        modifier = Modifier.weight(1f)
                    )
                    MatrixItem(
                        category = BodyTypeCategory.HIGH_FAT,
                        gender = gender,
                        isSelected = currentType == BodyTypeCategory.HIGH_FAT,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MatrixItem(
    category: BodyTypeCategory,
    gender: Gender,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    val bgColor = if (isSelected) {
        when (category) {
            BodyTypeCategory.SLIM -> Color(0xFF00E5FF).copy(alpha = 0.2f)
            BodyTypeCategory.SKINNY_FAT -> Color(0xFFFF9100).copy(alpha = 0.25f)
            BodyTypeCategory.BULKY -> Color(0xFF7C4DFF).copy(alpha = 0.25f)
            BodyTypeCategory.HIGH_FAT -> Color(0xFFFF5252).copy(alpha = 0.25f)
        }
    } else {
        Color.White.copy(alpha = 0.03f)
    }

    val borderColor = if (isSelected) {
        when (category) {
            BodyTypeCategory.SLIM -> Color(0xFF00E5FF)
            BodyTypeCategory.SKINNY_FAT -> Color(0xFFFF9100)
            BodyTypeCategory.BULKY -> Color(0xFFB388FF)
            BodyTypeCategory.HIGH_FAT -> Color(0xFFFF5252)
        }
    } else {
        Color.White.copy(alpha = 0.08f)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.emoji,
                    fontSize = 16.sp
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(borderColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AQUÍ",
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = category.getDisplayName(gender),
                color = if (isSelected) Color.White else MagraColors.TextSecondary,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "%GC: ${category.getFatRange(gender)}",
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else MagraColors.TextSecondary.copy(alpha = 0.7f),
                fontSize = 10.sp
            )

            Text(
                text = "IMC: ${category.bmiRange}",
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else MagraColors.TextSecondary.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}


