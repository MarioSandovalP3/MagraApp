package com.example.cmp.domain

import com.example.cmp.data.*

/**
 * Datos de una recomendación personalizada.
 */
data class Recommendation(
    val emoji: String,
    val title: String,
    val message: String,
    val type: RecommendationType
)

enum class RecommendationType {
    SUCCESS,    // Verde — algo va bien
    INFO,       // Azul — información o consejo
    WARNING,    // Amarillo — atención
    ACTION      // Naranja — acción sugerida
}

/**
 * Genera recomendaciones personalizadas según el objetivo del usuario,
 * sus resultados actuales y su historial previo.
 */
object GoalRecommendations {

    /**
     * Genera la lista de recomendaciones para mostrar en ResultsScreen.
     */
    fun generate(
        result: BodyCompositionResult,
        goal: UserGoal,
        previousEntry: HistoryEntry? = null
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // Recomendaciones por categoría actual (aplica a todos)
        recommendations.add(getCategoryFeedback(result))

        // Recomendaciones específicas por objetivo
        when (goal) {
            UserGoal.LOSE_FAT -> recommendations.addAll(loseFatRecommendations(result, previousEntry))
            UserGoal.GAIN_MUSCLE -> recommendations.addAll(gainMuscleRecommendations(result, previousEntry))
            UserGoal.MAINTAIN -> recommendations.addAll(maintainRecommendations(result, previousEntry))
        }

        // Recomendación de ICA si aplica
        if (result.waistToHeightRatio > 0) {
            recommendations.add(getIcaFeedback(result))
        }

        return recommendations
    }

    // ─── PERDER GRASA ──────────────────────────────────────────

    private fun loseFatRecommendations(
        result: BodyCompositionResult,
        previous: HistoryEntry?
    ): List<Recommendation> {
        val recs = mutableListOf<Recommendation>()

        // Meta de % grasa según género
        val targetFat = if (result.gender == Gender.MALE) 14.0 else 21.0
        val isAboveTarget = result.bodyFatPercentage > targetFat

        if (isAboveTarget) {
            val excess = result.bodyFatPercentage - targetFat
            val excessKg = result.fatMassKg * (excess / result.bodyFatPercentage)
            recs.add(
                Recommendation(
                    emoji = "🎯",
                    title = "Meta de grasa",
                    message = "Para llegar al rango Fitness (${targetFat}%), necesitas perder aproximadamente ${formatDouble(excessKg)} kg de grasa.",
                    type = RecommendationType.ACTION
                )
            )
        } else {
            recs.add(
                Recommendation(
                    emoji = "🏆",
                    title = "¡Ya estás en rango Fitness!",
                    message = "Tu % de grasa (${result.bodyFatPercentage}%) está dentro del rango Fitness. Enfócate en mantenerlo.",
                    type = RecommendationType.SUCCESS
                )
            )
        }

        // Comparación con medición anterior
        if (previous != null) {
            val fatDiff = result.bodyFatPercentage - previous.result.bodyFatPercentage
            val leanDiff = result.leanMassKg - previous.result.leanMassKg

            if (fatDiff < -0.3) {
                recs.add(
                    Recommendation(
                        emoji = "📉",
                        title = "Grasa bajando",
                        message = "Tu grasa corporal bajó ${formatDouble(-fatDiff)}% desde la última medición. ¡Buen progreso!",
                        type = RecommendationType.SUCCESS
                    )
                )
            } else if (fatDiff > 0.3) {
                recs.add(
                    Recommendation(
                        emoji = "📈",
                        title = "Grasa subiendo",
                        message = "Tu grasa corporal subió ${formatDouble(fatDiff)}% desde la última medición. Revisa tu déficit calórico.",
                        type = RecommendationType.WARNING
                    )
                )
            }

            if (leanDiff < -0.5) {
                recs.add(
                    Recommendation(
                        emoji = "⚠️",
                        title = "¡Cuidado con tu músculo!",
                        message = "Perdiste ${formatDouble(-leanDiff)} kg de masa magra. Aumenta tu proteína y reduce el déficit calórico para proteger el músculo.",
                        type = RecommendationType.WARNING
                    )
                )
            }
        }

        // Consejo general
        recs.add(
            Recommendation(
                emoji = "💡",
                title = "Consejo",
                message = "Un déficit calórico moderado (300-500 kcal/día) con entrenamiento de fuerza es la mejor forma de perder grasa sin perder músculo.",
                type = RecommendationType.INFO
            )
        )

        return recs
    }

    // ─── GANAR MÚSCULO ─────────────────────────────────────────

    private fun gainMuscleRecommendations(
        result: BodyCompositionResult,
        previous: HistoryEntry?
    ): List<Recommendation> {
        val recs = mutableListOf<Recommendation>()

        // Evaluar FFMI
        if (result.mode == CalculationMode.ADVANCED) {
            val ffmiMsg = when {
                result.ffmi < 18 -> Recommendation(
                    emoji = "📊",
                    title = "FFMI: Espacio para crecer",
                    message = "Tu FFMI (${result.ffmi}) indica que tienes buen potencial de ganancia muscular. Con entrenamiento progresivo puedes mejorar significativamente.",
                    type = RecommendationType.ACTION
                )
                result.ffmi < 22 -> Recommendation(
                    emoji = "💪",
                    title = "FFMI: Buen desarrollo",
                    message = "Tu FFMI (${result.ffmi}) muestra un desarrollo muscular por encima del promedio. Sigue con el entrenamiento progresivo.",
                    type = RecommendationType.SUCCESS
                )
                result.ffmi < 25 -> Recommendation(
                    emoji = "🏆",
                    title = "FFMI: Nivel atlético",
                    message = "Tu FFMI (${result.ffmi}) es excelente. Estás cerca del límite natural de desarrollo muscular.",
                    type = RecommendationType.SUCCESS
                )
                else -> Recommendation(
                    emoji = "⭐",
                    title = "FFMI: Excepcional",
                    message = "Tu FFMI (${result.ffmi}) está en niveles de élite. Mantén la consistencia en tu entrenamiento y nutrición.",
                    type = RecommendationType.SUCCESS
                )
            }
            recs.add(ffmiMsg)
        }

        // Comparación con medición anterior
        if (previous != null) {
            val leanDiff = result.leanMassKg - previous.result.leanMassKg
            val fatDiff = result.bodyFatPercentage - previous.result.bodyFatPercentage

            if (leanDiff > 0.3) {
                recs.add(
                    Recommendation(
                        emoji = "📈",
                        title = "Masa magra subiendo",
                        message = "Ganaste ${formatDouble(leanDiff)} kg de masa magra desde la última medición. ¡Tu entrenamiento está dando resultados!",
                        type = RecommendationType.SUCCESS
                    )
                )
            } else if (leanDiff < -0.3) {
                recs.add(
                    Recommendation(
                        emoji = "📉",
                        title = "Masa magra bajando",
                        message = "Perdiste ${formatDouble(-leanDiff)} kg de masa magra. Verifica tu ingesta de proteína y la intensidad del entrenamiento.",
                        type = RecommendationType.WARNING
                    )
                )
            }

            if (fatDiff > 3.0) {
                recs.add(
                    Recommendation(
                        emoji = "⚠️",
                        title = "Grasa subiendo rápido",
                        message = "Tu grasa subió ${formatDouble(fatDiff)}%. Un superávit calórico moderado (200-300 kcal/día) es suficiente para ganar músculo sin acumular grasa excesiva.",
                        type = RecommendationType.WARNING
                    )
                )
            }
        }

        // Consejo general
        recs.add(
            Recommendation(
                emoji = "💡",
                title = "Consejo",
                message = "Para ganar músculo: superávit calórico moderado, 1.6-2.2g de proteína por kg de peso, y entrenamiento de fuerza progresivo 3-5 días por semana.",
                type = RecommendationType.INFO
            )
        )

        return recs
    }

    // ─── MANTENERSE ────────────────────────────────────────────

    private fun maintainRecommendations(
        result: BodyCompositionResult,
        previous: HistoryEntry?
    ): List<Recommendation> {
        val recs = mutableListOf<Recommendation>()

        if (previous != null) {
            val fatDiff = result.bodyFatPercentage - previous.result.bodyFatPercentage
            val leanDiff = result.leanMassKg - previous.result.leanMassKg
            val weightDiff = result.fatMassKg + result.leanMassKg -
                (previous.result.fatMassKg + previous.result.leanMassKg)

            val isStable = kotlin.math.abs(fatDiff) < 1.0 &&
                kotlin.math.abs(leanDiff) < 0.5 &&
                kotlin.math.abs(weightDiff) < 1.0

            if (isStable) {
                recs.add(
                    Recommendation(
                        emoji = "✅",
                        title = "Composición estable",
                        message = "Tu composición corporal se mantiene estable. ¡Tu rutina y alimentación están funcionando bien!",
                        type = RecommendationType.SUCCESS
                    )
                )
            } else {
                if (kotlin.math.abs(fatDiff) >= 1.0) {
                    val direction = if (fatDiff > 0) "subió" else "bajó"
                    recs.add(
                        Recommendation(
                            emoji = "🔔",
                            title = "Cambio en grasa corporal",
                            message = "Tu % de grasa $direction ${formatDouble(kotlin.math.abs(fatDiff))}% desde la última medición. Revisa si algo cambió en tu alimentación o actividad.",
                            type = RecommendationType.WARNING
                        )
                    )
                }
                if (kotlin.math.abs(leanDiff) >= 0.5) {
                    val direction = if (leanDiff > 0) "ganaste" else "perdiste"
                    recs.add(
                        Recommendation(
                            emoji = "🔔",
                            title = "Cambio en masa magra",
                            message = "Tu masa magra cambió: $direction ${formatDouble(kotlin.math.abs(leanDiff))} kg. Evalúa tu nivel de actividad física.",
                            type = RecommendationType.WARNING
                        )
                    )
                }
            }
        } else {
            recs.add(
                Recommendation(
                    emoji = "📋",
                    title = "Primera medición registrada",
                    message = "Guarda esta medición y repite en 2-4 semanas para empezar a rastrear tu estabilidad.",
                    type = RecommendationType.INFO
                )
            )
        }

        // Consejo general
        recs.add(
            Recommendation(
                emoji = "💡",
                title = "Consejo",
                message = "Para mantenerte: come a tu nivel de calorías de mantenimiento, mantén la proteína a 1.4-1.6g/kg y conserva tu rutina de ejercicio.",
                type = RecommendationType.INFO
            )
        )

        return recs
    }

    // ─── FEEDBACK GENERAL ──────────────────────────────────────

    private fun getCategoryFeedback(result: BodyCompositionResult): Recommendation {
        val range = when (result.gender) {
            Gender.MALE -> result.category.maleRange
            Gender.FEMALE -> result.category.femaleRange
        }

        return when (result.category) {
            BodyCategory.ATHLETE -> Recommendation(
                emoji = "🏆",
                title = "Categoría: ${result.category.displayName}",
                message = "Tu % de grasa está en el rango de atleta/esencial ($range). Este nivel requiere disciplina extrema para mantenerlo.",
                type = RecommendationType.SUCCESS
            )
            BodyCategory.FITNESS -> Recommendation(
                emoji = "💪",
                title = "Categoría: ${result.category.displayName}",
                message = "Estás en el rango Fitness ($range). Excelente equilibrio entre salud y rendimiento.",
                type = RecommendationType.SUCCESS
            )
            BodyCategory.AVERAGE -> Recommendation(
                emoji = "👍",
                title = "Categoría: ${result.category.displayName}",
                message = "Estás en el rango aceptable ($range). Tu composición está dentro de lo saludable.",
                type = RecommendationType.INFO
            )
            BodyCategory.OVERWEIGHT -> Recommendation(
                emoji = "⚠️",
                title = "Categoría: ${result.category.displayName}",
                message = "Tu % de grasa está por encima del rango saludable ($range). Considera ajustar tu alimentación y aumentar la actividad física.",
                type = RecommendationType.WARNING
            )
        }
    }

    private fun getIcaFeedback(result: BodyCompositionResult): Recommendation {
        return when (result.cardiovascularRisk) {
            CardiovascularRisk.LOW -> Recommendation(
                emoji = "💚",
                title = "Riesgo cardiovascular bajo",
                message = "Tu relación cintura-altura (${result.waistToHeightRatio}) está en rango saludable. ¡Buen indicador de salud metabólica!",
                type = RecommendationType.SUCCESS
            )
            CardiovascularRisk.MODERATE -> Recommendation(
                emoji = "💛",
                title = "Atención al perímetro de cintura",
                message = "Tu ICA (${result.waistToHeightRatio}) está en el límite. Mantén la actividad física y una alimentación equilibrada.",
                type = RecommendationType.WARNING
            )
            CardiovascularRisk.HIGH -> Recommendation(
                emoji = "🧡",
                title = "Riesgo cardiovascular elevado",
                message = "Tu ICA (${result.waistToHeightRatio}) indica riesgo elevado. Prioriza reducir la grasa abdominal con ejercicio cardiovascular y control calórico.",
                type = RecommendationType.WARNING
            )
            CardiovascularRisk.VERY_HIGH -> Recommendation(
                emoji = "❤️",
                title = "Riesgo cardiovascular alto",
                message = "Tu ICA (${result.waistToHeightRatio}) indica riesgo alto. Se recomienda consultar con un profesional de salud.",
                type = RecommendationType.WARNING
            )
        }
    }

    private fun formatDouble(value: Double): String {
        val rounded = (value * 10).toInt() / 10.0
        return if (rounded == rounded.toInt().toDouble()) {
            "${rounded.toInt()}.0"
        } else {
            "$rounded"
        }
    }
}
