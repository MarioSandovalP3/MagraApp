package com.example.cmp

import androidx.compose.runtime.Composable

/**
 * Manejador del botón de retroceso específico de plataforma.
 * En Android captura el botón físico de retroceso.
 * En otras plataformas no hace nada.
 */
@Composable
expect fun PlatformBackHandler(onBack: () -> Unit)