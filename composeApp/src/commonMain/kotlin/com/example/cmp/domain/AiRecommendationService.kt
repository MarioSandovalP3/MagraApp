package com.example.cmp.domain

import com.example.cmp.data.BodyCompositionResult
import com.example.cmp.data.StorageManager
import com.example.cmp.data.UserGoal
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChatMessage(val role: String, val content: String)

@Serializable
data class ChatRequest(val model: String, val messages: List<ChatMessage>)

@Serializable
data class ChatResponse(val choices: List<Choice>)

@Serializable
data class Choice(val message: ChatMessage)

object AiRecommendationService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    /**
     * Prueba la conexión con la API enviando un mensaje simple.
     * Retorna un mensaje de éxito o el error ocurrido.
     */
    suspend fun testConnection(apiKey: String, baseUrl: String, model: String): String {
        if (apiKey.isBlank()) return "❌ Ingresa una API Key primero."
        val cleanUrl = if (baseUrl.endsWith("/")) "${baseUrl}chat/completions" else "${baseUrl}/chat/completions"
        val requestBody = ChatRequest(
            model = model.ifBlank { "gpt-3.5-turbo" },
            messages = listOf(ChatMessage(role = "user", content = "Di solo: OK"))
        )
        return try {
            val response: ChatResponse = client.post(cleanUrl) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                setBody(requestBody)
            }.body()
            val reply = response.choices.firstOrNull()?.message?.content ?: "Respuesta vacía"
            "✅ Conexión exitosa: $reply"
        } catch (e: Exception) {
            "❌ Error: ${e.message?.take(100)}"
        }
    }

    suspend fun getRecommendations(result: BodyCompositionResult, goal: UserGoal): String? {
        val settings = StorageManager.loadAiSettings()
        if (settings.apiKey.isBlank()) {
            return null
        }

        val url = if (settings.baseUrl.endsWith("/")) "${settings.baseUrl}chat/completions" else "${settings.baseUrl}/chat/completions"

        val prompt = buildPrompt(result, goal)

        val requestBody = ChatRequest(
            model = settings.model.ifBlank { "gpt-3.5-turbo" },
            messages = listOf(
                ChatMessage(role = "system", content = "Eres un nutricionista y entrenador personal experto. Da recomendaciones claras, amables y concisas basadas en la composición corporal (máx 3 párrafos cortos). Usa emojis para hacer el texto amigable."),
                ChatMessage(role = "user", content = prompt)
            )
        )

        return try {
            val response: ChatResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${settings.apiKey}")
                setBody(requestBody)
            }.body()
            response.choices.firstOrNull()?.message?.content
        } catch (e: Exception) {
            println("AiRecommendationService Error: ${e.message}")
            null
        }
    }

    private fun buildPrompt(result: BodyCompositionResult, goal: UserGoal): String {
        return """
            Tengo el siguiente perfil corporal:
            - Género: ${result.gender.displayName}
            - Grasa corporal: ${result.bodyFatPercentage}%
            - Masa grasa: ${result.fatMassKg} kg
            - Masa magra: ${result.leanMassKg} kg
            - Índice de masa libre de grasa (FFMI): ${result.ffmi}
            - Categoría de composición: ${result.category.displayName}
            - Riesgo cardiovascular (ICA): ${result.cardiovascularRisk.displayName}
            
            Mi objetivo actual es: ${goal.displayName} (${goal.description}).
            
            Basado estrictamente en estos datos, dame recomendaciones personalizadas de nutrición y entrenamiento para lograr mi objetivo de forma saludable.
        """.trimIndent()
    }
}
