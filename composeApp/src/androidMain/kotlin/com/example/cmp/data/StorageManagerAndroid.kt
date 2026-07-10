package com.example.cmp.data

import android.content.Context

/**
 * Implementación Android del almacenamiento.
 * Guarda el historial como archivo JSON en el directorio de datos interno de la app.
 */
actual object PlatformStorage {

    private var appContext: Context? = null

    /**
     * Inicializa el backend con el contexto de la aplicación Android.
     * Debe llamarse desde el Application.onCreate() o desde la Activity principal.
     */
    actual fun onAppStart() {
        // En Android, el contexto se pasa explícitamente desde MainActivity
    }

    /**
     * Inicializa con contexto de Android. Debe llamarse desde MainActivity.
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Expone el contexto para otras funciones (como compartir).
     */
    fun getContext(): Context? = appContext

    actual fun write(key: String, data: String) {
        val context = appContext ?: return
        context.openFileOutput("$key.json", Context.MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    }

    actual fun read(key: String): String? {
        val context = appContext ?: return null
        val file = context.getFileStreamPath("$key.json")
        if (!file.exists()) return null
        return context.openFileInput("$key.json").use {
            it.readBytes().decodeToString()
        }
    }
}

/**
 * Implementación Android del timestamp.
 */
actual fun platformCurrentTimeMillis(): Long = System.currentTimeMillis()


/**
 * Implementación Android de copiar al portapapeles.
 * Copia el texto sin abrir ninguna interfaz.
 */
actual fun platformCopyToClipboard(text: String) {
    val context = PlatformStorage.getContext() ?: return
    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("MagraApp", text)
    clipboard.setPrimaryClip(clip)
}
