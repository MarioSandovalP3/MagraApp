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
import com.example.cmp.ui.theme.MagraColors
import com.example.cmp.ui.theme.MagraGradients

@Composable
fun AboutScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier.fillMaxSize().background(MagraGradients.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp).padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f)).clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) { Text("←", color = Color.White, fontSize = 20.sp) }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(28.dp)).background(MagraColors.Primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) { Text("💪", fontSize = 44.sp) }
            Spacer(modifier = Modifier.height(24.dp))
            Text("MagraApp", color = MagraColors.Primary, fontSize = 32.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("v1.0.0", color = MagraColors.TextMuted, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Análisis de Composición Corporal\nmás allá del IMC", color = MagraColors.TextSecondary, fontSize = 15.sp, textAlign = TextAlign.Center, lineHeight = 22.sp)
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = MagraColors.GlassBorder)
            Spacer(modifier = Modifier.height(24.dp))

            AboutFeature("📊", "Método de la Marina de EE.UU.", "Cálculo preciso del % de grasa corporal usando ecuaciones de Hodgdon & Beckett")
            Spacer(modifier = Modifier.height(16.dp))
            AboutFeature("📏", "FFMI, ICA e IMC", "Múltiples métricas para una evaluación completa")
            Spacer(modifier = Modifier.height(16.dp))
            AboutFeature("🤖", "Recomendaciones con IA", "Planes vía OpenAI/DeepSeek")
            Spacer(modifier = Modifier.height(16.dp))
            AboutFeature("📈", "Historial y Tendencias", "Gráficos para rastrear tu progreso")
            Spacer(modifier = Modifier.height(16.dp))
            AboutFeature("📤", "Compartir Resultados", "Copia o comparte tus resultados")
            Spacer(modifier = Modifier.height(16.dp))
            AboutFeature("🛋️", "Perfil de Actividad", "Recomendaciones adaptadas a tu disciplina")

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = MagraColors.GlassBorder)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Desarrollado con Kotlin Multiplatform", color = MagraColors.TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("© 2026 MagraApp", color = MagraColors.TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AboutFeature(emoji: String, title: String, description: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Text(text = emoji, fontSize = 24.sp, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, color = MagraColors.TextMuted, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}