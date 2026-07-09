package com.example.cmp.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Almacenamiento persistente multiplataforma para el historial.
 * Usa expect/actual para implementaciones específicas de plataforma.
 */
object StorageManager {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private const val STORAGE_KEY = "magra_history"

    /**
     * Guarda la lista de entradas del historial de forma persistente.
     */
    fun saveHistory(entries: List<HistoryEntry>) {
        try {
            val data = json.encodeToString(entries)
            PlatformStorage.write(STORAGE_KEY, data)
        } catch (e: Exception) {
            println("StorageManager: Error saving history: ${e.message}")
        }
    }

    /**
     * Carga la lista de entradas del historial desde almacenamiento persistente.
     */
    fun loadHistory(): List<HistoryEntry> {
        return try {
            val data = PlatformStorage.read(STORAGE_KEY)
            if (data.isNullOrBlank()) {
                emptyList()
            } else {
                json.decodeFromString<List<HistoryEntry>>(data)
            }
        } catch (e: Exception) {
            println("StorageManager: Error loading history: ${e.message}")
            emptyList()
        }
    }

    /**
     * Limpia los datos persistentes del historial.
     */
    fun clearStorage() {
        PlatformStorage.write(STORAGE_KEY, "[]")
    }

    private const val AI_SETTINGS_KEY = "magra_ai_settings"

    /**
     * Guarda la configuración de IA.
     */
    fun saveAiSettings(settings: AiSettings) {
        try {
            val data = json.encodeToString(settings)
            PlatformStorage.write(AI_SETTINGS_KEY, data)
        } catch (e: Exception) {
            println("StorageManager: Error saving AI settings: ${e.message}")
        }
    }

    /**
     * Carga la configuración de IA.
     */
    fun loadAiSettings(): AiSettings {
        return try {
            val data = PlatformStorage.read(AI_SETTINGS_KEY)
            if (data.isNullOrBlank()) {
                AiSettings()
            } else {
                json.decodeFromString<AiSettings>(data)
            }
        } catch (e: Exception) {
            println("StorageManager: Error loading AI settings: ${e.message}")
            AiSettings()
        }
    }
}

/**
 * Declaración esperada de almacenamiento específico de plataforma.
 */
expect object PlatformStorage {
    fun write(key: String, data: String)
    fun read(key: String): String?
    fun onAppStart()
}

/**
 * Obtiene el timestamp actual en milisegundos (epoch).
 */
expect fun platformCurrentTimeMillis(): Long
