package com.example.cmp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.cmp.domain.AiRecommendationService
import com.example.cmp.domain.GoalRecommendations
import com.example.cmp.domain.Recommendation
import com.example.cmp.domain.RecommendationType
import com.example.cmp.domain.generateShareText
import com.example.cmp.ui.components.*
import com.example.cmp.ui.theme.MagraColors
import com.example.cmp.ui.theme.MagraGradients

/**
 * Pantalla de resultados con dashboard visual de composición corporal.
 */
@Composable
fun ResultsScreen(
    result: BodyCompositionResult,
    measurements: UserMeasurements,
    goal: UserGoal,
    previousEntry: HistoryEntry? = null,
    onSaveToHistory: () -> Unit,
    onNewMeasurement: () -> Unit,
    onViewHistory: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var saved by remember { mutableStateOf(false) }

    // Generar recomendaciones personalizadas
    val recommendations = remember(result, goal, previousEntry) {
        GoalRecommendations.generate(result, goal, previousEntry)
    }

    // Animación de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Estado para feedback de copiado
    var copiedMessage by remember { mutableStateOf(false) }
    if (copiedMessage) {
        LaunchedEffect(copiedMessage) {
            kotlinx.coroutines.delay(2000)
            copiedMessage = false
        }
    }

    // IA
    var aiRecommendation by remember { mutableStateOf<String?>(null) }
    var isAiLoading by remember { mutableStateOf(false) }

    LaunchedEffect(result, goal) {
        val settings = StorageManager.loadAiSettings()
        if (settings.apiKey.isNotBlank()) {
            isAiLoading = true
            aiRecommendation = AiRecommendationService.getRecommendations(
                result = result,
                goal = goal,
                activityLevel = measurements.activityLevel
            )
            isAiLoading = false
        }
    }

    // Estado para diálogo de información
    var infoDialogType by remember { mutableStateOf<String?>(null) }

    // Información de métricas para el diálogo
    val metricInfo = mapOf(
        "FFMI" to Pair("📊 FFMI - Índice de Masa Libre de Grasa",
            "El FFMI (Fat-Free Mass Index) es como el IMC pero solo para masa muscular. " +
            "Elimina la grasa corporal del cálculo, dando una medida real de tu desarrollo muscular. " +
            "\n\n• < 18: Por debajo del promedio\n• 18-20: Promedio\n• 20-22: Sobre el promedio\n• 22-25: Nivel atlético\n• > 25: Nivel élite (posible uso de sustancias)"),
        "ICA" to Pair("${result.cardiovascularRisk.emoji} ICA - Índice Cintura-Altura",
            "El ICA (Waist-to-Height Ratio) mide la grasa abdominal dividiendo la cintura entre la estatura. " +
            "Es uno de los mejores predictores de riesgo cardiovascular y metabólico. " +
            "\n\n• < 0.50: Bajo riesgo 💚\n• 0.50-0.53: Moderado 💛\n• 0.54-0.59: Alto 🧡\n• ≥ 0.60: Muy alto ❤️"),
        "IMC" to Pair("📏 IMC - Índice de Masa Corporal",
            "El IMC (Body Mass Index) es una medida básica que relaciona peso y estatura. " +
            "Su principal limitación es que NO distingue entre grasa y músculo. " +
            "\n\n• < 18.5: Bajo peso\n• 18.5-24.9: Normal\n• 25-29.9: Sobrepeso\n• ≥ 30: Obesidad" +
            "\n\n⚠️ MagraApp usa el Método de la Marina para superar esta limitación.")
    )

    // Color según categoría
    val categoryColor = when (result.category) {
        BodyCategory.ATHLETE -> MagraColors.CategoryAthlete
        BodyCategory.FITNESS -> MagraColors.CategoryFitness
        BodyCategory.AVERAGE -> MagraColors.CategoryAverage
        BodyCategory.OVERWEIGHT -> MagraColors.CategoryOverweight
    }

    val riskColor = when (result.cardiovascularRisk) {
        CardiovascularRisk.LOW -> MagraColors.RiskLow
        CardiovascularRisk.MODERATE -> MagraColors.RiskModerate
        CardiovascularRisk.HIGH -> MagraColors.RiskHigh
        CardiovascularRisk.VERY_HIGH -> MagraColors.RiskVeryHigh
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MagraGradients.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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
                        text = "Tus Resultados",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (result.mode == CalculationMode.ADVANCED) "🎯 Método de la Marina" else "⚡ IMC Rápido",
                        color = MagraColors.Primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Text("☰", fontSize = 24.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Indicador circular principal
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + scaleIn(
                    tween(800, easing = FastOutSlowInEasing),
                    initialScale = 0.6f
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedCircularProgress(
                        percentage = result.bodyFatPercentage.toFloat(),
                        label = "Grasa Corporal",
                        valueText = "${result.bodyFatPercentage}%",
                        color = categoryColor,
                        size = 200.dp,
                        strokeWidth = 14.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Badge de categoría
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${result.category.emoji} ${result.category.displayName}",
                            color = categoryColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Barra de composición corporal
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically(
                    tween(600, delayMillis = 300)
                ) { 40 }
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "COMPOSICIÓN CORPORAL",
                        color = MagraColors.TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BodyCompositionBar(
                        fatPercentage = result.bodyFatPercentage.toFloat(),
                        fatKg = result.fatMassKg.toFloat(),
                        leanKg = result.leanMassKg.toFloat()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Métricas detalladas
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 500)) + slideInVertically(
                    tween(600, delayMillis = 500)
                ) { 40 }
            ) {
                Column {
                    SectionHeader("MÉTRICAS DETALLADAS")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            emoji = "🔥",
                            title = "Masa Grasa",
                            value = "${result.fatMassKg} kg",
                            subtitle = "${result.bodyFatPercentage}% del peso",
                            accentColor = MagraColors.FatColor,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            emoji = "💪",
                            title = "Masa Magra",
                            value = "${result.leanMassKg} kg",
                            subtitle = "${100.0 - result.bodyFatPercentage}% del peso",
                            accentColor = MagraColors.LeanColor,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // FFMI solo en modo avanzado
                    if (result.mode == CalculationMode.ADVANCED) {
                        MetricCard(
                            emoji = "📊",
                            title = "FFMI (Índice de Masa Libre de Grasa)",
                            value = "${result.ffmi}",
                            subtitle = when {
                                result.ffmi < 18 -> "Por debajo del promedio"
                                result.ffmi < 20 -> "Promedio"
                                result.ffmi < 22 -> "Por encima del promedio"
                                result.ffmi < 25 -> "Excelente — nivel atlético"
                                else -> "Excepcional — nivel élite"
                            },
                            accentColor = MagraColors.Accent,
                            onInfoClick = { infoDialogType = "FFMI" }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // ICA - Relación cintura-altura
                    if (result.waistToHeightRatio > 0) {
                        MetricCard(
                            emoji = result.cardiovascularRisk.emoji,
                            title = "ICA (Cintura / Altura)",
                            value = "${result.waistToHeightRatio}",
                            subtitle = "Riesgo cardiovascular: ${result.cardiovascularRisk.displayName}",
                            accentColor = riskColor,
                            onInfoClick = { infoDialogType = "ICA" }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    MetricCard(
                        emoji = "📏",
                        title = "IMC (Índice de Masa Corporal)",
                        value = "${result.bmi}",
                        subtitle = when {
                            result.bmi < 18.5 -> "Bajo peso"
                            result.bmi < 25 -> "Peso normal"
                            result.bmi < 30 -> "Sobrepeso"
                            else -> "Obesidad"
                        } + " · Solo usa peso y altura",
                        accentColor = MagraColors.TextSecondary,
                        onInfoClick = { infoDialogType = "IMC" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nota sobre modo rápido
            if (result.mode == CalculationMode.QUICK) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 700))
                ) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text("⚠️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Estos resultados son estimaciones basadas en el IMC (fórmula de Deurenberg). Para mayor precisión, usa el Modo Avanzado con cinta métrica.",
                                color = MagraColors.AccentWarm,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Recomendaciones de IA
            if (isAiLoading || aiRecommendation != null) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 720))
                ) {
                    Column {
                        SectionHeader("✨ RECOMENDACIONES DE IA")
                        
                        if (isAiLoading) {
                            GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        color = MagraColors.Primary,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Analizando tu composición y generando plan...",
                                        color = MagraColors.TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        } else if (aiRecommendation != null) {
                            GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                                Text(
                                    text = aiRecommendation!!,
                                    color = MagraColors.TextPrimary,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Recomendaciones personalizadas
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 750)) + slideInVertically(
                    tween(600, delayMillis = 750)
                ) { 40 }
            ) {
                Column {
                    SectionHeader("${goal.emoji} RECOMENDACIONES PARA ${goal.displayName.uppercase()}")

                    recommendations.forEach { rec ->
                        RecommendationCard(rec)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de acción
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 800)) + slideInVertically(
                    tween(600, delayMillis = 800)
                ) { 40 }
            ) {
                Column {
                    GradientButton(
                        text = if (saved) "✓ GUARDADO" else "💾 GUARDAR EN HISTORIAL",
                        onClick = {
                            if (!saved) {
                                onSaveToHistory()
                                saved = true
                            }
                        },
                        enabled = !saved,
                        gradient = if (saved) Brush_success() else MagraGradients.Primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNewMeasurement,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MagraColors.Primary
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MagraColors.Primary.copy(alpha = 0.5f))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                "🔄 Nueva",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = onViewHistory,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MagraColors.Accent
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MagraColors.Accent.copy(alpha = 0.5f))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                "📈 Historial",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Botones de compartir / copiar
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 850)) + slideInVertically(
                    tween(600, delayMillis = 850)
                ) { 40 }
            ) {
                Column {
                    HorizontalDivider(
                        color = MagraColors.GlassBorder,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            val text = generateShareText(
                                result = result,
                                measurements = measurements,
                                goal = goal,
                                aiRecommendation = aiRecommendation,
                                recommendations = recommendations
                            )
                            platformCopyToClipboard(text)
                            copiedMessage = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (copiedMessage) Color(0xFF00C853) else MagraColors.Accent
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (copiedMessage) Color(0xFF00C853).copy(alpha = 0.5f)
                                else MagraColors.Accent.copy(alpha = 0.5f)
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (copiedMessage) "✅ ¡Copiado al portapapeles!" else "📋 Copiar y Compartir",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Diálogo de información de métricas
    val currentInfo = infoDialogType?.let { metricInfo[it] }
    if (currentInfo != null) {
        AlertDialog(
            onDismissRequest = { infoDialogType = null },
            containerColor = MagraColors.SurfaceElevated,
            titleContentColor = Color.White,
            textContentColor = MagraColors.TextSecondary,
            title = {
                Text(
                    text = currentInfo.first,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = currentInfo.second,
                    color = MagraColors.TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { infoDialogType = null }) {
                    Text("Entendido", color = MagraColors.Primary)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

/**
 * Brush de éxito para botón guardado.
 */
@Composable
private fun Brush_success() = androidx.compose.ui.graphics.Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF00C853),
        Color(0xFF00E676)
    )
)

/**
 * Tarjeta de recomendación con borde lateral de color según tipo.
 */
@Composable
private fun RecommendationCard(recommendation: Recommendation) {
    val accentColor = when (recommendation.type) {
        RecommendationType.SUCCESS -> MagraColors.CategoryAthlete
        RecommendationType.INFO -> MagraColors.Accent
        RecommendationType.WARNING -> MagraColors.CategoryAverage
        RecommendationType.ACTION -> MagraColors.AccentWarm
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.06f))
    ) {
        // Borde lateral de color
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(accentColor)
        )

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = recommendation.emoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recommendation.title,
                    color = accentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recommendation.message,
                    color = MagraColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}
