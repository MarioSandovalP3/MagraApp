package com.example.cmp

import androidx.compose.runtime.Composable

/**
 * Implementación Android del manejador de retroceso.
 * Captura el botón físico de retroceso del dispositivo.
 */
@Composable
actual fun PlatformBackHandler(onBack: () -> Unit) {
    // BackHandler de AndroidX Activity
    @Suppress("UNRESOLVED_REFERENCE")
    androidx.activity.compose.BackHandler(onBack = onBack)
}
