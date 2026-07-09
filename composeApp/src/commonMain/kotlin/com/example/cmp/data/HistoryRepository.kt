package com.example.cmp.data

/**
 * Repositorio para el historial de mediciones con persistencia.
 *
 * Almacena las entradas de composición corporal del usuario
 * con persistencia mediante StorageManager.
 */
object HistoryRepository {

    private val entries = mutableListOf<HistoryEntry>()
    private var nextId = 1L
    private var initialized = false

    /**
     * Inicializa el repositorio cargando datos persistentes.
     * Debe llamarse antes de usar otras funciones.
     */
    fun initialize() {
        if (initialized) return
        val savedEntries = StorageManager.loadHistory()
        entries.clear()
        entries.addAll(savedEntries)
        nextId = (savedEntries.maxOfOrNull { it.id } ?: 0) + 1
        initialized = true
    }

    /**
     * Agrega una nueva entrada al historial y persiste los datos.
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
        persistHistory()
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
     * Limpia todo el historial y elimina datos persistentes.
     */
    fun clearHistory() {
        entries.clear()
        StorageManager.clearStorage()
    }

    /**
     * Persiste el historial actual en almacenamiento.
     */
    private fun persistHistory() {
        StorageManager.saveHistory(entries.toList())
    }

    /**
     * Obtiene timestamp actual en millis.
     */
    private fun currentTimeMillis(): Long {
        return platformCurrentTimeMillis()
    }
}
