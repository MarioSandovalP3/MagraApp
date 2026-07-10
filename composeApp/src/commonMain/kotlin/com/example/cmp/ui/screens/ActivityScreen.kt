package com.example.cmp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import com.example.cmp.data.ActivityLevel
import com.example.cmp.data.UserGoal
import com.example.cmp.ui.components.ActivityLevelSelector
import com.example.cmp.ui.components.GradientButton
import com.example.cmp.ui.theme.MagraColors
import com.example.cmp.ui.theme.MagraGradients

/**
 * Pantalla de selección de nivel de actividad física.
 * Paso intermedio entre elegir objetivo e ingresar medidas.
 */
@Composable
fun ActivityScreen(
    goal: UserGoal,
    onContinue: (ActivityLevel) -> Unit,
    onBack: () -> Unit
) {
    var selectedActivity by remember { mutableStateOf(ActivityLevel.SEDENTARY) }

    // Animación de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MagraGradients.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800)) + slideInVertically(
                    tween(800, easing = FastOutSlowInEasing)
                ) { -60 }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Botón atrás
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable(onClick = onBack),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("←", color = Color.White, fontSize = 20.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Icono
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(MagraColors.Primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏃", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "NIVEL DE ACTIVIDAD",
                        color = MagraColors.Primary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Selecciona tu nivel para\nrecomendaciones más precisas",
                        color = MagraColors.TextSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Objetivo: ${goal.emoji} ${goal.displayName}",
                        color = MagraColors.Primary.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Selector de actividad
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 300)) + slideInVertically(
                    tween(800, delayMillis = 300, easing = FastOutSlowInEasing)
                ) { 60 }
            ) {
                ActivityLevelSelector(
                    selected = selectedActivity,
                    onSelected = { selectedActivity = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón continuar
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 600)) + slideInVertically(
                    tween(800, delayMillis = 600, easing = FastOutSlowInEasing)
                ) { 40 }
            ) {
                GradientButton(
                    text = "CONTINUAR",
                    onClick = { onContinue(selectedActivity) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}