package com.example.cmp

import androidx.compose.runtime.Composable

/**
 * Implementación JVM (Desktop) del manejador de retroceso.
 * No hace nada ya que Desktop no tiene botón de retroceso físico.
 */
@Composable
actual fun PlatformBackHandler(onBack: () -> Unit) {
    // No-op: no hay botón de retroceso en Desktop
}