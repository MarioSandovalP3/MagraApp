package com.example.cmp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cmp.data.AiSettings
import com.example.cmp.data.StorageManager
import com.example.cmp.domain.AiRecommendationService
import com.example.cmp.ui.components.GlassCard
import com.example.cmp.ui.theme.MagraColors
import com.example.cmp.ui.theme.MagraGradients
import kotlinx.coroutines.launch

/**
 * Pantalla completa de configuración de Inteligencia Artificial.
 */
@Composable
fun AiSettingsScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var aiSettings by remember { mutableStateOf(StorageManager.loadAiSettings()) }
    var apiKey by remember { mutableStateOf(aiSettings.apiKey) }
    var baseUrl by remember { mutableStateOf(aiSettings.baseUrl) }
    var model by remember { mutableStateOf(aiSettings.model) }

    var savedMessage by remember { mutableStateOf("") }
    var testResult by remember { mutableStateOf("") }
    var isTesting by remember { mutableStateOf(false) }

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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "🤖 Inteligencia Artificial",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Descripción
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Conecta con DeepSeek o ChatGPT (OpenAI) para obtener un plan personalizado de nutrición y entrenamiento según tus resultados.",
                    color = MagraColors.TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // API Key
            Text(
                text = "API KEY",
                color = MagraColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MagraColors.Primary,
                    unfocusedBorderColor = MagraColors.GlassBorder,
                    focusedLabelColor = MagraColors.Primary,
                    unfocusedLabelColor = MagraColors.TextMuted,
                    cursorColor = MagraColors.Primary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = MagraColors.TextPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Base URL
            Text(
                text = "BASE URL",
                color = MagraColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text("Base URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("https://api.openai.com/v1/") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MagraColors.Primary,
                    unfocusedBorderColor = MagraColors.GlassBorder,
                    focusedLabelColor = MagraColors.Primary,
                    unfocusedLabelColor = MagraColors.TextMuted,
                    cursorColor = MagraColors.Primary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = MagraColors.TextPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            )
            Text(
                text = "💡 DeepSeek: https://api.deepseek.com/v1/",
                color = MagraColors.TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Modelo
            Text(
                text = "MODELO",
                color = MagraColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Modelo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("gpt-3.5-turbo") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MagraColors.Primary,
                    unfocusedBorderColor = MagraColors.GlassBorder,
                    focusedLabelColor = MagraColors.Primary,
                    unfocusedLabelColor = MagraColors.TextMuted,
                    cursorColor = MagraColors.Primary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = MagraColors.TextPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            )
            Text(
                text = "💡 DeepSeek: deepseek-chat",
                color = MagraColors.TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Guardar
            Button(
                onClick = {
                    val newSettings = AiSettings(
                        apiKey = apiKey.trim(),
                        baseUrl = baseUrl.trim(),
                        model = model.trim()
                    )
                    StorageManager.saveAiSettings(newSettings)
                    aiSettings = newSettings
                    savedMessage = "✅ ¡Ajustes guardados!"
                    scope.launch {
                        kotlinx.coroutines.delay(2500)
                        savedMessage = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MagraColors.Primary)
            ) {
                Text("💾 Guardar Ajustes", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            if (savedMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = savedMessage,
                    color = Color(0xFF00C853),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Probar Conexión
            OutlinedButton(
                onClick = {
                    scope.launch {
                        isTesting = true
                        testResult = ""
                        testResult = AiRecommendationService.testConnection(
                            apiKey = apiKey.trim(),
                            baseUrl = baseUrl.trim(),
                            model = model.trim()
                        )
                        isTesting = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isTesting,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MagraColors.Primary),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MagraColors.Primary.copy(alpha = 0.5f))
                )
            ) {
                if (isTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MagraColors.Primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Probando...", fontSize = 15.sp)
                } else {
                    Text("🔗 Probar Conexión", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (testResult.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                val resultColor = if (testResult.startsWith("✅")) Color(0xFF00C853) else Color(0xFFFF5252)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = resultColor.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = testResult,
                        color = resultColor,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}