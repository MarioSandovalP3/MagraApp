package com.example.cmp.domain

import com.example.cmp.data.Gender

/**
 * Resultado de la validación de datos corporales.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)

/**
 * Validador de rangos biológicos para los datos ingresados por el usuario.
 * Evita cálculos con valores anatómicamente imposibles.
 */
object BodyValidation {

    // Rangos mínimos y máximos para adultos
    private const val MIN_AGE = 10
    private const val MAX_AGE = 120
    private const val MIN_WEIGHT_KG = 20.0
    private const val MAX_WEIGHT_KG = 350.0
    private const val MIN_HEIGHT_CM = 100.0
    private const val MAX_HEIGHT_CM = 260.0
    private const val MIN_NECK_CM = 20.0
    private const val MAX_NECK_CM = 60.0
    private const val MIN_WAIST_CM = 40.0
    private const val MAX_WAIST_CM = 200.0
    private const val MIN_HIP_CM = 50.0
    private const val MAX_HIP_CM = 200.0

    // Relación mínima cintura/cuello para que la fórmula funcione
    private const val MIN_WAIST_NECK_DIFF = 1.0

    /**
     * Valida todas las mediciones y retorna una lista de errores.
     * Si la lista está vacía, los datos son válidos.
     */
    fun validate(
        age: Int,
        weightKg: Double,
        heightCm: Double,
        neckCm: Double = 0.0,
        waistCm: Double = 0.0,
        hipCm: Double = 0.0,
        gender: Gender = Gender.MALE,
        isAdvanced: Boolean = true
    ): ValidationResult {
        val errors = mutableListOf<String>()

        // Validar edad
        if (age < MIN_AGE) {
            errors.add("La edad mínima es $MIN_AGE años")
        } else if (age > MAX_AGE) {
            errors.add("La edad máxima es $MAX_AGE años")
        }

        // Validar peso
        if (weightKg < MIN_WEIGHT_KG) {
            errors.add("El peso mínimo es $MIN_WEIGHT_KG kg (ingresaste ${weightKg}kg)")
        } else if (weightKg > MAX_WEIGHT_KG) {
            errors.add("El peso máximo es $MAX_WEIGHT_KG kg")
        }

        // Validar altura
        if (heightCm < MIN_HEIGHT_CM) {
            errors.add("La estatura mínima es $MIN_HEIGHT_CM cm (ingresaste ${heightCm}cm)")
        } else if (heightCm > MAX_HEIGHT_CM) {
            errors.add("La estatura máxima es $MAX_HEIGHT_CM cm")
        }

        // Validar IMC razonable (relación peso/altura)
        if (weightKg >= MIN_WEIGHT_KG && heightCm >= MIN_HEIGHT_CM) {
            val heightM = heightCm / 100.0
            val bmi = weightKg / (heightM * heightM)
            if (bmi < 10.0) {
                errors.add("El IMC calculado es muy bajo ($bmi). Revisa peso y estatura")
            } else if (bmi > 60.0) {
                errors.add("El IMC calculado es muy alto ($bmi). Revisa peso y estatura")
            }
        }

        // Validar modo avanzado
        if (isAdvanced) {
            // Validar cuello
            if (neckCm <= 0) {
                errors.add("Ingresa la medida del cuello")
            } else if (neckCm < MIN_NECK_CM) {
                errors.add("El cuello mínimo es $MIN_NECK_CM cm (ingresaste ${neckCm}cm)")
            } else if (neckCm > MAX_NECK_CM) {
                errors.add("El cuello máximo es $MAX_NECK_CM cm")
            }

            // Validar cintura
            if (waistCm <= 0) {
                errors.add("Ingresa la medida de la cintura")
            } else if (waistCm < MIN_WAIST_CM) {
                errors.add("La cintura mínima es $MIN_WAIST_CM cm (ingresaste ${waistCm}cm)")
            } else if (waistCm > MAX_WAIST_CM) {
                errors.add("La cintura máxima es $MAX_WAIST_CM cm")
            }

            // Validar que cintura > cuello (biológicamente necesario)
            if (neckCm > 0 && waistCm > 0 && waistCm <= neckCm + MIN_WAIST_NECK_DIFF) {
                errors.add("La cintura debe ser mayor que el cuello. Verifica las medidas")
            }

            // Validar cadera solo para mujeres
            if (gender == Gender.FEMALE) {
                if (hipCm <= 0) {
                    errors.add("Ingresa la medida de la cadera")
                } else if (hipCm < MIN_HIP_CM) {
                    errors.add("La cadera mínima es $MIN_HIP_CM cm (ingresaste ${hipCm}cm)")
                } else if (hipCm > MAX_HIP_CM) {
                    errors.add("La cadera máxima es $MAX_HIP_CM cm")
                }

                // Validar que cadera > cintura
                if (waistCm > 0 && hipCm > 0 && hipCm <= waistCm) {
                    errors.add("La cadera debe ser mayor que la cintura. Verifica las medidas")
                }
            }
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}