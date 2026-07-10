package com.example.cmp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cmp.data.*
import com.example.cmp.ui.components.*
import com.example.cmp.ui.theme.MagraColors
import com.example.cmp.ui.theme.MagraGradients

/**
 * Pantalla de historial con gráficos de tendencia y listado de entradas.
 */
@Composable
fun HistoryScreen(
    entries: List<HistoryEntry>,
    onBack: () -> Unit,
    onClearHistory: () -> Unit,
    onNewMeasurement: () -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    // Animación de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MagraGradients.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header fijo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp, bottom = 16.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    BackIcon(color = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Historial",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${entries.size} mediciones",
                        color = MagraColors.TextMuted,
                        fontSize = 13.sp
                    )
                }
                if (entries.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFF5252).copy(alpha = 0.15f))
                            .clickable(onClick = onClearHistory)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "🗑️ Borrar",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Text("☰", fontSize = 20.sp, color = Color.White)
                }
            }

            if (entries.isEmpty()) {
                // Estado vacío
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600)) + scaleIn(tween(600), initialScale = 0.8f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📊",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Sin mediciones aún",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Realiza tu primera medición para empezar a rastrear tu progreso.",
                            color = MagraColors.TextMuted,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        GradientButton(
                            text = "NUEVA MEDICIÓN",
                            onClick = onNewMeasurement,
                            modifier = Modifier.width(240.dp)
                        )
                    }
                }
            } else {
                // Contenido con historial
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Gráficos de tendencia
                    if (entries.size >= 2) {
                        item {
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { 40 }
                            ) {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "TENDENCIAS",
                                        color = MagraColors.TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Gráfico de % grasa
                                    TrendChart(
                                        values = entries.reversed().map { it.result.bodyFatPercentage.toFloat() },
                                        color = MagraColors.FatColor,
                                        label = "% Grasa Corporal"
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Gráfico de masa magra
                                    TrendChart(
                                        values = entries.reversed().map { it.result.leanMassKg.toFloat() },
                                        color = MagraColors.LeanColor,
                                        label = "Masa Magra (kg)"
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Gráfico de peso
                                    TrendChart(
                                        values = entries.reversed().map { it.measurements.weightKg.toFloat() },
                                        color = MagraColors.Accent,
                                        label = "Peso Total (kg)"
                                    )
                                }
                            }
                        }
                    }

                    // Header de lista
                    item {
                        SectionHeader("MEDICIONES ANTERIORES")
                    }

                    // Entradas del historial
                    itemsIndexed(entries) { index, entry ->
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(400, delayMillis = 100 * index.coerceAtMost(5))) +
                                slideInVertically(tween(400, delayMillis = 100 * index.coerceAtMost(5))) { 30 }
                        ) {
                            HistoryEntryCard(entry)
                        }
                    }

                    // Espaciado final
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryEntryCard(entry: HistoryEntry) {
    val categoryColor = when (entry.result.category) {
        BodyCategory.ATHLETE -> MagraColors.CategoryAthlete
        BodyCategory.FITNESS -> MagraColors.CategoryFitness
        BodyCategory.AVERAGE -> MagraColors.CategoryAverage
        BodyCategory.OVERWEIGHT -> MagraColors.CategoryOverweight
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        // Header de la entrada
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = entry.dateString,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${entry.goal.emoji} ${entry.goal.displayName}",
                    color = MagraColors.TextMuted,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(categoryColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = entry.result.category.displayName,
                    color = categoryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Métricas resumen
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MiniMetric(
                label = "% Grasa",
                value = "${entry.result.bodyFatPercentage}%",
                color = MagraColors.FatColor
            )
            MiniMetric(
                label = "Magra",
                value = "${entry.result.leanMassKg} kg",
                color = MagraColors.LeanColor
            )
            MiniMetric(
                label = "Peso",
                value = "${entry.measurements.weightKg} kg",
                color = MagraColors.Accent
            )
            MiniMetric(
                label = "IMC",
                value = "${entry.result.bmi}",
                color = MagraColors.TextMuted
            )
        }
    }
}

@Composable
private fun MiniMetric(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = MagraColors.TextMuted,
            fontSize = 11.sp
        )
    }
}
