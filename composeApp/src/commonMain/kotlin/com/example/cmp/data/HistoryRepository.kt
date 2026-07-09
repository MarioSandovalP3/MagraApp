package com.example.cmp.data

/**
 * Repositorio en memoria para el historial de mediciones.
 *
 * Almacena las entradas de composición corporal del usuario
 * durante la sesión actual de la aplicación.
 */
object HistoryRepository {

    private val entries = mutableListOf<HistoryEntry>()
    private var nextId = 1L

    /**
     * Agrega una nueva entrada al historial.
     */
    fun addEntry(
        measurements: UserMeasurements,
        result: BodyCompositionResult,
        goal: UserGoal,
        dateString: String
    ): HistoryEntry {
        val entry = HistoryEntry(
            id = nextId++,
            timestamp = currentTimeMillis(),
            dateString = dateString,
            measurements = measurements,
            result = result,
            goal = goal
        )
        entries.add(0, entry) // Insertar al inicio (más reciente primero)
        return entry
    }

    /**
     * Obtiene el historial completo ordenado por fecha (más reciente primero).
     */
    fun getHistory(): List<HistoryEntry> = entries.toList()

    /**
     * Obtiene el número de entradas.
     */
    fun getEntryCount(): Int = entries.size

    /**
     * Limpia todo el historial.
     */
    fun clearHistory() {
        entries.clear()
    }

    /**
     * Obtiene timestamp actual en millis.
     * Usa una implementación simple multiplataforma.
     */
    private fun currentTimeMillis(): Long {
        // Kotlin Multiplatform no tiene System.currentTimeMillis directamente
        // Usamos una aproximación basada en el contador de entries
        return nextId * 1000L
    }
}
