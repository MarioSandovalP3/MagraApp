package com.example.cmp.domain

import com.example.cmp.data.*
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Motor de cálculos para composición corporal.
 *
 * Implementa:
 * - Método de la Marina de EE.UU. (ecuaciones de Hodgdon & Beckett)
 * - IMC (Índice de Masa Corporal) para modo rápido
 * - Métricas derivadas: Masa Grasa, Masa Magra, FFMI, ICA
 * - Clasificación ACSM
 */
object BodyCompositionCalculator {

    /**
     * Calcula el resultado completo de composición corporal.
     */
    fun calculate(measurements: UserMeasurements, goal: UserGoal = UserGoal.MAINTAIN): BodyCompositionResult {
        val heightM = measurements.heightCm / 100.0
        val bmi = calculateBMI(measurements.weightKg, heightM)

        return when (measurements.mode) {
            CalculationMode.QUICK -> calculateQuickMode(measurements, bmi, heightM, goal)
            CalculationMode.ADVANCED -> calculateAdvancedMode(measurements, bmi, heightM, goal)
        }
    }

    /**
     * Modo rápido: usa una estimación de grasa basada en IMC.
     * Fórmula de Deurenberg et al. (1991):
     * %GC = 1.20 × IMC + 0.23 × edad − 10.8 × sexo − 5.4
     * donde sexo = 1 (hombres) o 0 (mujeres)
     */
    private fun calculateQuickMode(
        measurements: UserMeasurements,
        bmi: Double,
        heightM: Double,
        goal: UserGoal
    ): BodyCompositionResult {
        val sexFactor = if (measurements.gender == Gender.MALE) 1.0 else 0.0
        val bodyFatPercentage = (1.20 * bmi + 0.23 * measurements.age - 10.8 * sexFactor - 5.4)
            .coerceIn(2.0, 60.0)

        return buildResult(measurements, bodyFatPercentage, bmi, heightM, goal)
    }

    /**
     * Modo avanzado: Método de la Marina de EE.UU.
     * Ecuaciones de Hodgdon & Beckett (en centímetros, logaritmos base 10).
     */
    private fun calculateAdvancedMode(
        measurements: UserMeasurements,
        bmi: Double,
        heightM: Double,
        goal: UserGoal
    ): BodyCompositionResult {
        val bodyFatPercentage = calculateNavyBodyFat(measurements).coerceIn(2.0, 60.0)
        return buildResult(measurements, bodyFatPercentage, bmi, heightM, goal)
    }

    /**
     * Implementación directa de las ecuaciones de Hodgdon & Beckett.
     */
    private fun calculateNavyBodyFat(measurements: UserMeasurements): Double {
        return when (measurements.gender) {
            Gender.MALE -> {
                val circumferenceValue = measurements.waistCm - measurements.neckCm
                if (circumferenceValue <= 0) return 15.0 // Valor por defecto si datos inválidos

                val density = 1.0324 -
                    0.19077 * log10(circumferenceValue) +
                    0.15456 * log10(measurements.heightCm)

                if (density <= 0) return 15.0
                495.0 / density - 450.0
            }
            Gender.FEMALE -> {
                val circumferenceValue = measurements.waistCm + measurements.hipCm - measurements.neckCm
                if (circumferenceValue <= 0) return 22.0 // Valor por defecto si datos inválidos

                val density = 1.29579 -
                    0.35004 * log10(circumferenceValue) +
                    0.22100 * log10(measurements.heightCm)

                if (density <= 0) return 22.0
                495.0 / density - 450.0
            }
        }
    }

    /**
     * Construye el resultado final con todas las métricas.
     */
    private fun buildResult(
        measurements: UserMeasurements,
        bodyFatPercentage: Double,
        bmi: Double,
        heightM: Double,
        goal: UserGoal
    ): BodyCompositionResult {
        val fatMassKg = measurements.weightKg * (bodyFatPercentage / 100.0)
        val leanMassKg = measurements.weightKg - fatMassKg

        // FFMI = masa magra (kg) / estatura (m)² + 6.1 × (1.8 - estatura (m))
        val ffmi = if (heightM > 0) {
            leanMassKg / (heightM * heightM) + 6.1 * (1.8 - heightM)
        } else 0.0

        // ICA = cintura (cm) / estatura (cm)
        val waistToHeightRatio = if (measurements.waistCm > 0 && measurements.heightCm > 0) {
            measurements.waistCm / measurements.heightCm
        } else 0.0

        val category = BodyCategory.fromBodyFat(bodyFatPercentage, measurements.gender)
        val cardiovascularRisk = classifyCardiovascularRisk(waistToHeightRatio)

        // BMR (Tasa Metabólica Basal)
        val bmr = if (measurements.mode == CalculationMode.ADVANCED && leanMassKg > 0.0) {
            // Katch-McArdle: 370 + 21.6 * Masa Magra (kg)
            370.0 + 21.6 * leanMassKg
        } else {
            // Mifflin-St Jeor
            if (measurements.gender == Gender.MALE) {
                10.0 * measurements.weightKg + 6.25 * measurements.heightCm - 5.0 * measurements.age + 5.0
            } else {
                10.0 * measurements.weightKg + 6.25 * measurements.heightCm - 5.0 * measurements.age - 161.0
            }
        }

        // TDEE (Gasto Energético Diario Total)
        val activityMultiplier = when (measurements.activityLevel) {
            ActivityLevel.SEDENTARY -> 1.2
            ActivityLevel.GENERAL_ACTIVE -> 1.375
            ActivityLevel.ENDURANCE -> 1.55
            ActivityLevel.FITNESS_STRENGTH -> 1.65
        }
        val tdee = bmr * activityMultiplier

        // Calorías Objetivo según meta
        val targetCalories = when (goal) {
            UserGoal.LOSE_FAT -> {
                val deficit = tdee - 500.0
                val minCalories = if (measurements.gender == Gender.MALE) 1500.0 else 1200.0
                deficit.coerceAtLeast(minCalories).coerceAtLeast(bmr)
            }
            UserGoal.GAIN_MUSCLE -> tdee + 300.0
            UserGoal.MAINTAIN -> tdee
        }

        return BodyCompositionResult(
            bodyFatPercentage = bodyFatPercentage.roundTo(1),
            fatMassKg = fatMassKg.roundTo(1),
            leanMassKg = leanMassKg.roundTo(1),
            ffmi = ffmi.roundTo(1),
            bmi = bmi.roundTo(1),
            waistToHeightRatio = waistToHeightRatio.roundTo(2),
            category = category,
            cardiovascularRisk = cardiovascularRisk,
            mode = measurements.mode,
            gender = measurements.gender,
            bmr = bmr.roundTo(0),
            tdee = tdee.roundTo(0),
            targetCalories = targetCalories.roundTo(0)
        )
    }

    /**
     * IMC = peso (kg) / estatura (m)²
     */
    private fun calculateBMI(weightKg: Double, heightM: Double): Double {
        if (heightM <= 0) return 0.0
        return weightKg / (heightM * heightM)
    }

    /**
     * Clasificación de riesgo cardiovascular por ICA.
     */
    private fun classifyCardiovascularRisk(ratio: Double): CardiovascularRisk {
        return when {
            ratio <= 0.0 -> CardiovascularRisk.LOW // No se proporcionó cintura
            ratio < 0.5 -> CardiovascularRisk.LOW        // < 0.50 = saludable
            ratio < 0.54 -> CardiovascularRisk.MODERATE   // 0.50 - 0.53 = atención
            ratio < 0.60 -> CardiovascularRisk.HIGH       // 0.54 - 0.59 = riesgo elevado
            else -> CardiovascularRisk.VERY_HIGH          // 0.60+ = riesgo muy alto
        }
    }

    /**
     * Extensión para redondear a N decimales.
     */
    private fun Double.roundTo(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).roundToInt() / factor
    }
}
