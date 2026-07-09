package com.example.cmp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cmp.data.UserGoal
import com.example.cmp.ui.components.GlassCard
import com.example.cmp.ui.components.GradientButton
import com.example.cmp.ui.theme.MagraColors
import com.example.cmp.ui.theme.MagraGradients

/**
 * Pantalla de bienvenida con selección de objetivo.
 */
@Composable
fun WelcomeScreen(
    onGoalSelected: (UserGoal) -> Unit
) {
    var selectedGoal by remember { mutableStateOf<UserGoal?>(null) }

    // Animaciones de entrada
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

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
                .padding(horizontal = 28.dp)
                .padding(top = 60.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800)) + slideInVertically(
                    tween(800, easing = FastOutSlowInEasing)
                ) { -60 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    // Logo / Ícono
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MagraColors.Primary.copy(alpha = glowAlpha * 0.3f))
                            .border(
                                2.dp,
                                MagraColors.Primary.copy(alpha = glowAlpha),
                                RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💪",
                            fontSize = 36.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "MAGRA",
                        color = MagraColors.Primary,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 6.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tu composición corporal\nmás allá del IMC",
                        color = MagraColors.TextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            // Goal Selection
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 300)) + slideInVertically(
                    tween(800, delayMillis = 300, easing = FastOutSlowInEasing)
                ) { 80 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿CUÁL ES TU OBJETIVO?",
                        color = MagraColors.TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UserGoal.entries.forEach { goal ->
                        GoalCard(
                            goal = goal,
                            isSelected = selectedGoal == goal,
                            onClick = { selectedGoal = goal }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // Continue Button
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 600)) + slideInVertically(
                    tween(800, delayMillis = 600, easing = FastOutSlowInEasing)
                ) { 40 }
            ) {
                GradientButton(
                    text = "CONTINUAR",
                    onClick = { selectedGoal?.let { onGoalSelected(it) } },
                    enabled = selectedGoal != null,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun GoalCard(
    goal: UserGoal,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MagraColors.Primary else MagraColors.GlassBorder,
        animationSpec = tween(300)
    )
    val bgAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.15f else 0.06f,
        animationSpec = tween(300)
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(MagraColors.Primary.copy(alpha = bgAlpha))
            .border(1.5.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = goal.emoji,
            fontSize = 28.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = goal.displayName,
                color = if (isSelected) Color.White else MagraColors.TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = goal.description,
                color = MagraColors.TextMuted,
                fontSize = 13.sp
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MagraColors.Primary),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
