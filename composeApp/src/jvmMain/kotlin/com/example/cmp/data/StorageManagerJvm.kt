package com.example.cmp.data

/**
 * Implementación JVM (Desktop) del almacenamiento.
 * Guarda el historial como archivo JSON en ~/.magraapp/
 */
actual object PlatformStorage {

    private val storageDir: java.io.File = run {
        val userHome = System.getProperty("user.home")
        val appDir = java.nio.file.Paths.get(userHome, ".magraapp").toFile()
        appDir.mkdirs()
        appDir
    }

    actual fun onAppStart() {
        storageDir.mkdirs()
    }

    actual fun write(key: String, data: String) {
        val file = java.io.File(storageDir, "$key.json")
        file.writeText(data)
    }

    actual fun read(key: String): String? {
        val file = java.io.File(storageDir, "$key.json")
        return if (file.exists()) file.readText() else null
    }
}

/**
 * Implementación JVM del timestamp.
 */
actual fun platformCurrentTimeMillis(): Long = System.currentTimeMillis()