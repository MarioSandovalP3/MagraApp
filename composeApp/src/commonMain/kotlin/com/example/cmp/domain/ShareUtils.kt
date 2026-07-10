package com.example.cmp.domain

import com.example.cmp.data.BodyCompositionResult
import com.example.cmp.data.UserGoal
import com.example.cmp.data.UserMeasurements

/**
 * Genera un texto plano con todos los resultados para copiar/compartir.
 */
fun generateShareText(
    result: BodyCompositionResult,
    measurements: UserMeasurements,
    goal: UserGoal,
    aiRecommendation: String? = null,
    recommendations: List<Recommendation> = emptyList()
): String {
    val sb = StringBuilder()
    sb.appendLine("📊 MagraApp — Resultados de Composición Corporal")
    sb.appendLine("═".repeat(40))
    sb.appendLine()
    sb.appendLine("🎯 Objetivo: ${goal.emoji} ${goal.displayName}")
    sb.appendLine("📋 Modo: ${result.mode.displayName}")
    if (measurements.mode == com.example.cmp.data.CalculationMode.ADVANCED) {
        sb.appendLine("🏃 Actividad: ${measurements.activityLevel.emoji} ${measurements.activityLevel.displayName}")
    }
    sb.appendLine()
    sb.appendLine("📏 Tus Medidas:")
    sb.appendLine("   • Edad: ${measurements.age} años")
    sb.appendLine("   • Peso: ${measurements.weightKg} kg")
    sb.appendLine("   • Estatura: ${measurements.heightCm} cm")
    sb.appendLine()
    sb.appendLine("🎯 Resultados:")
    sb.appendLine("   • ${result.category.emoji} Categoría: ${result.category.displayName} (${result.bodyFatPercentage}% grasa)")
    sb.appendLine("   🔥 Masa Grasa: ${result.fatMassKg} kg")
    sb.appendLine("   💪 Masa Magra: ${result.leanMassKg} kg")
    sb.appendLine("   📊 FFMI: ${result.ffmi}")
    sb.appendLine("   📏 IMC: ${result.bmi}")
    if (result.waistToHeightRatio > 0) {
        sb.appendLine("   ${result.cardiovascularRisk.emoji} ICA: ${result.waistToHeightRatio} (${result.cardiovascularRisk.displayName})")
    }
    sb.appendLine("   🔥 TMB (Metabolismo Basal): ${result.bmr.toInt()} kcal")
    sb.appendLine("   ⚡ TDEE (Gasto Diario): ${result.tdee.toInt()} kcal")
    sb.appendLine("   🎯 Calorías Objetivo: ${result.targetCalories.toInt()} kcal/día")
    sb.appendLine()
    sb.appendLine("📋 Clasificación ACSM: ${result.category.emoji} ${result.category.displayName}")

    // Recomendaciones personalizadas (locales)
    if (recommendations.isNotEmpty()) {
        sb.appendLine()
        sb.appendLine("${goal.emoji} RECOMENDACIONES PARA ${goal.displayName.uppercase()}")
        recommendations.forEach { rec ->
            sb.appendLine("  ${rec.emoji} ${rec.title}: ${rec.message}")
        }
    }

    // Recomendaciones de IA (si hay API Key configurada)
    if (aiRecommendation != null) {
        sb.appendLine()
        sb.appendLine("🤖 RECOMENDACIONES DE INTELIGENCIA ARTIFICIAL")
        sb.appendLine(aiRecommendation)
    }

    sb.appendLine()
    sb.appendLine("═".repeat(40))

    return sb.toString()
}
