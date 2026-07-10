package com.example.cmp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.cmp.domain.BodyValidation
import com.example.cmp.ui.components.*
import com.example.cmp.ui.theme.MagraColors
import com.example.cmp.ui.theme.MagraGradients

/**
 * Pantalla de entrada de datos con modo rápido y avanzado.
 */
@Composable
fun InputScreen(
    goal: UserGoal,
    activityLevel: ActivityLevel,
    onCalculate: (UserMeasurements) -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    var isAdvancedMode by remember { mutableStateOf(true) }
    var selectedGender by remember { mutableStateOf(Gender.MALE) }

    // Campos de texto
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var neck by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var hip by remember { mutableStateOf("") }

    // Validación
    val isBasicValid = age.isNotEmpty() && weight.isNotEmpty() && height.isNotEmpty()
    val isAdvancedValid = isBasicValid && neck.isNotEmpty() && waist.isNotEmpty() &&
        (selectedGender == Gender.MALE || hip.isNotEmpty())
    val isFormValid = if (isAdvancedMode) isAdvancedValid else isBasicValid

    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }
    var showValidation by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MagraGradients.Background)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            // Header con botón atrás y menú
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
                        text = "Tus Medidas",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Objetivo: ${goal.emoji} ${goal.displayName}",
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

            // Mode Toggle
            ModeToggle(
                isAdvanced = isAdvancedMode,
                onToggle = { isAdvancedMode = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Aviso de modo
            AnimatedContent(
                targetState = isAdvancedMode,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                }
            ) { advanced ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = if (advanced) "🎯" else "⚡",
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (advanced)
                                "Método de la Marina de EE.UU. — Necesitas una cinta métrica. Resultados más precisos y confiables."
                            else
                                "Solo calcula el IMC. Es una métrica limitada que no distingue entre grasa y músculo.",
                            color = if (advanced) MagraColors.TextSecondary else MagraColors.AccentWarm,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gender Selection
            SectionHeader("GÉNERO")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GenderChip(
                    label = "Hombre",
                    emoji = "🙋‍♂️",
                    isSelected = selectedGender == Gender.MALE,
                    onClick = { selectedGender = Gender.MALE },
                    modifier = Modifier.weight(1f)
                )
                GenderChip(
                    label = "Mujer",
                    emoji = "🙋‍♀️",
                    isSelected = selectedGender == Gender.FEMALE,
                    onClick = { selectedGender = Gender.FEMALE },
                    modifier = Modifier.weight(1f)
                )
            }

            // Mostrar nivel de actividad seleccionado
            Spacer(modifier = Modifier.height(12.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = activityLevel.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Nivel de actividad",
                            color = MagraColors.TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = activityLevel.displayName,
                            color = MagraColors.Primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Datos básicos
            SectionHeader("DATOS BÁSICOS")
            MagraTextField(
                value = age,
                onValueChange = { age = it },
                label = "Edad",
                suffix = "años"
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MagraTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = "Peso",
                    suffix = "kg",
                    modifier = Modifier.weight(1f)
                )
                MagraTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = "Estatura",
                    suffix = "cm",
                    modifier = Modifier.weight(1f)
                )
            }

            // Campos avanzados
            AnimatedVisibility(
                visible = isAdvancedMode,
                enter = fadeIn(tween(300)) + expandVertically(tween(400)),
                exit = fadeOut(tween(300)) + shrinkVertically(tween(400))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    SectionHeader("CIRCUNFERENCIAS")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MagraTextField(
                            value = neck,
                            onValueChange = { neck = it },
                            label = "Cuello",
                            suffix = "cm",
                            modifier = Modifier.weight(1f)
                        )
                        MagraTextField(
                            value = waist,
                            onValueChange = { waist = it },
                            label = "Cintura",
                            suffix = "cm",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Campo de cadera solo para mujeres
                    AnimatedVisibility(
                        visible = selectedGender == Gender.FEMALE,
                        enter = fadeIn(tween(300)) + expandVertically(tween(400)),
                        exit = fadeOut(tween(300)) + shrinkVertically(tween(400))
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            MagraTextField(
                                value = hip,
                                onValueChange = { hip = it },
                                label = "Cadera",
                                suffix = "cm"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Guía de medición
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "📏 Guía de Medición",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MeasurementGuideItem("Cuello", "Justo por debajo de la nuez de Adán")
                        MeasurementGuideItem(
                            "Cintura",
                            if (selectedGender == Gender.MALE) "A la altura del ombligo"
                            else "Parte más estrecha del torso"
                        )
                        if (selectedGender == Gender.FEMALE) {
                            MeasurementGuideItem("Cadera", "Parte más ancha de la cadera")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Mensajes de error de validación
            AnimatedVisibility(visible = showValidation && validationErrors.isNotEmpty()) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "⚠️ Verifica los datos:",
                        color = Color(0xFFFF5252),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    validationErrors.forEach { error ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("• ", color = Color(0xFFFF5252), fontSize = 13.sp)
                            Text(
                                text = error,
                                color = MagraColors.TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Calculate Button
            GradientButton(
                text = "CALCULAR",
                onClick = {
                    showValidation = true
                    val ageVal = age.toIntOrNull() ?: 0
                    val weightVal = weight.toDoubleOrNull() ?: 0.0
                    val heightVal = height.toDoubleOrNull() ?: 0.0
                    val neckVal = neck.toDoubleOrNull() ?: 0.0
                    val waistVal = waist.toDoubleOrNull() ?: 0.0
                    val hipVal = hip.toDoubleOrNull() ?: 0.0

                    val result = BodyValidation.validate(
                        age = ageVal,
                        weightKg = weightVal,
                        heightCm = heightVal,
                        neckCm = neckVal,
                        waistCm = waistVal,
                        hipCm = hipVal,
                        gender = selectedGender,
                        isAdvanced = isAdvancedMode
                    )

                    if (result.isValid) {
                        validationErrors = emptyList()
                        val measurements = UserMeasurements(
                            gender = selectedGender,
                            age = ageVal,
                            weightKg = weightVal,
                            heightCm = heightVal,
                            neckCm = neckVal,
                            waistCm = waistVal,
                            hipCm = hipVal,
                            mode = if (isAdvancedMode) CalculationMode.ADVANCED else CalculationMode.QUICK,
                            activityLevel = activityLevel
                        )
                        onCalculate(measurements)
                    } else {
                        validationErrors = result.errors
                    }
                },
                enabled = isFormValid
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GenderChip(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        if (isSelected) MagraColors.Primary else MagraColors.GlassBorder,
        tween(300)
    )
    val bgAlpha by animateFloatAsState(
        if (isSelected) 0.15f else 0.06f,
        tween(300)
    )

    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MagraColors.Primary.copy(alpha = bgAlpha))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = if (isSelected) Color.White else MagraColors.TextMuted,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun MeasurementGuideItem(label: String, description: String) {
    Row(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(
            text = "•",
            color = MagraColors.Primary,
            fontSize = 13.sp,
            modifier = Modifier.width(14.dp)
        )
        Text(
            text = "$label: ",
            color = MagraColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = description,
            color = MagraColors.TextMuted,
            fontSize = 13.sp
        )
    }
}
