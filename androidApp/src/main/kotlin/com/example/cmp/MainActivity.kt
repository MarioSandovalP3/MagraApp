package com.example.cmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.cmp.data.PlatformStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Restaurar el tema por defecto para la actividad después del Splash Screen
        setTheme(android.R.style.Theme_Material_Light_NoActionBar)
        
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inicializar almacenamiento persistente con contexto Android
        PlatformStorage.initialize(this)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
