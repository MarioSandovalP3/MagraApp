package com.example.cmp.data

import kotlinx.serialization.Serializable

/**
 * Género del usuario para los cálculos de composición corporal.
 */
@Serializable
enum class Gender(val displayName: String) {
    MALE("Hombre"),
    FEMALE("Mujer")
}

/**
 * Objetivo del usuario.
 */
@Serializable
enum class UserGoal(val displayName: String, val emoji: String, val description: String) {
    LOSE_FAT("Perder Grasa", "🔥", "Reduce tu porcentaje de grasa corporal"),
    GAIN_MUSCLE("Ganar Músculo", "💪", "Aumenta tu masa muscular magra"),
    MAINTAIN("Mantenerse", "⚖️", "Mantén tu composición actual")
}

/**
 * Modo de cálculo.
 */
@Serializable
enum class CalculationMode(val displayName: String, val description: String) {
    QUICK("Modo Rápido", "Solo IMC — menos preciso"),
    ADVANCED("Modo Avanzado", "Método de la Marina — recomendado")
}

/**
 * Mediciones ingresadas por el usuario.
 */
@Serializable
data class UserMeasurements(
    val gender: Gender,
    val age: Int,
    val weightKg: Double,
    val heightCm: Double,
    val neckCm: Double = 0.0,     // Solo modo avanzado
    val waistCm: Double = 0.0,    // Solo modo avanzado
    val hipCm: Double = 0.0,      // Solo modo avanzado + mujeres
    val mode: CalculationMode = CalculationMode.ADVANCED
)

/**
 * Categoría de composición corporal según ACSM.
 */
@Serializable
enum class BodyCategory(
    val displayName: String,
    val emoji: String,
    val maleRange: String,
    val femaleRange: String
) {
    ATHLETE("Atleta / Esencial", "🏆", "2% - 5%", "10% - 13%"),
    FITNESS("Fitness", "💪", "6% - 13%", "14% - 20%"),
    AVERAGE("Aceptable", "👍", "14% - 24%", "21% - 31%"),
    OVERWEIGHT("Sobrepeso", "⚠️", "25%+", "32%+");

    companion object {
        fun fromBodyFat(percentage: Double, gender: Gender): BodyCategory {
            return when (gender) {
                Gender.MALE -> when {
                    percentage <= 5.0 -> ATHLETE
                    percentage <= 13.0 -> FITNESS
                    percentage <= 24.0 -> AVERAGE
                    else -> OVERWEIGHT
                }
                Gender.FEMALE -> when {
                    percentage <= 13.0 -> ATHLETE
                    percentage <= 20.0 -> FITNESS
                    percentage <= 31.0 -> AVERAGE
                    else -> OVERWEIGHT
                }
            }
        }
    }
}

/**
 * Nivel de riesgo cardiovascular según ICA (Relación Cintura-Altura).
 */
@Serializable
enum class CardiovascularRisk(val displayName: String, val emoji: String) {
    LOW("Bajo", "💚"),
    MODERATE("Moderado", "💛"),
    HIGH("Alto", "🧡"),
    VERY_HIGH("Muy Alto", "❤️")
}

/**
 * Resultado completo de la evaluación de composición corporal.
 */
@Serializable
data class BodyCompositionResult(
    val bodyFatPercentage: Double,
    val fatMassKg: Double,
    val leanMassKg: Double,
    val ffmi: Double,
    val bmi: Double,
    val waistToHeightRatio: Double,
    val category: BodyCategory,
    val cardiovascularRisk: CardiovascularRisk,
    val mode: CalculationMode,
    val gender: Gender
)

/**
 * Entrada del historial.
 */
@Serializable
data class HistoryEntry(
    val id: Long,
    val timestamp: Long, // epoch millis
    val dateString: String,
    val measurements: UserMeasurements,
    val result: BodyCompositionResult,
    val goal: UserGoal
)
