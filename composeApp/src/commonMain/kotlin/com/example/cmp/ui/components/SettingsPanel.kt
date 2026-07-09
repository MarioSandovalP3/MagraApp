package com.example.cmp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cmp.data.AiSettings
import com.example.cmp.data.StorageManager
import com.example.cmp.domain.AiRecommendationService
import kotlinx.coroutines.launch

@Composable
fun SettingsPanel(
    onClose: () -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚙️ Ajustes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onClose) {
                Text("Cerrar ✕", fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))

        // Sección IA
        Text(
            text = "🤖 Inteligencia Artificial",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Conecta con DeepSeek o ChatGPT (OpenAI) para obtener un plan personalizado. Ambas APIs usan el mismo formato.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // API Key
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Base URL
        OutlinedTextField(
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text("Base URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("https://api.openai.com/v1/") }
        )
        Text(
            text = "💡 DeepSeek: https://api.deepseek.com/v1/",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Modelo
        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Modelo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("gpt-3.5-turbo") }
        )
        Text(
            text = "💡 DeepSeek: deepseek-chat",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("💾 Guardar Ajustes", fontSize = 15.sp)
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
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTesting,
            shape = MaterialTheme.shapes.medium
        ) {
            if (isTesting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Probando...", fontSize = 15.sp)
            } else {
                Text("🔗 Probar Conexión", fontSize = 15.sp)
            }
        }

        if (testResult.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            val resultColor = if (testResult.startsWith("✅")) Color(0xFF00C853) else MaterialTheme.colorScheme.error
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = resultColor.copy(alpha = 0.08f),
                shape = MaterialTheme.shapes.small,
                tonalElevation = 0.dp
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
