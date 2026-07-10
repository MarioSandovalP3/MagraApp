package com.example.cmp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cmp.ui.theme.MagraColors

/**
 * Panel del menú lateral con opciones de navegación.
 */
@Composable
fun SettingsPanel(
    onClose: () -> Unit,
    onOpenAiSettings: () -> Unit,
    onOpenAbout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        // Header con título y botón cerrar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MagraApp",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MagraColors.Primary
            )
            TextButton(onClick = onClose) {
                Text("✕", fontSize = 20.sp, color = MagraColors.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(color = MagraColors.GlassBorder)
        Spacer(modifier = Modifier.height(24.dp))

        // Sección Ajustes
        Text(
            text = "AJUSTES",
            color = MagraColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Inteligencia Artificial
        MenuItem(
            emoji = "🤖",
            title = "Inteligencia Artificial",
            subtitle = "Conecta con OpenAI o DeepSeek",
            onClick = {
                onClose()
                onOpenAiSettings()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Acerca de
        MenuItem(
            emoji = "ℹ️",
            title = "Acerca de",
            subtitle = "Información de la aplicación",
            onClick = {
                onClose()
                onOpenAbout()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        HorizontalDivider(color = MagraColors.GlassBorder)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "MagraApp v1.0.0",
            color = MagraColors.TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun MenuItem(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = MagraColors.TextMuted,
                fontSize = 12.sp
            )
        }
    }
}