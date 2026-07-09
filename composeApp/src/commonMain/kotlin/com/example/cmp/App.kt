package com.example.cmp

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.cmp.data.*
import com.example.cmp.domain.BodyCompositionCalculator
import com.example.cmp.ui.screens.*
import com.example.cmp.ui.theme.MagraTheme

/**
 * Pantallas de la aplicación para la navegación manual.
 */
enum class Screen {
    WELCOME,
    INPUT,
    RESULTS,
    HISTORY
}

/**
 * Punto de entrada principal de la aplicación MagraApp.
 *
 * Gestiona la navegación entre pantallas usando estado Compose,
 * sin dependencias externas de navegación.
 */
@Composable
@Preview
fun App() {
    MagraTheme {
        // Inicializar repositorio con persistencia
        LaunchedEffect(Unit) {
            HistoryRepository.initialize()
        }

        // Estado de navegación
        var currentScreen by remember { mutableStateOf(Screen.WELCOME) }

        // Estado compartido entre pantallas
        var selectedGoal by remember { mutableStateOf(UserGoal.MAINTAIN) }
        var lastMeasurements by remember { mutableStateOf<UserMeasurements?>(null) }
        var lastResult by remember { mutableStateOf<BodyCompositionResult?>(null) }
        var historyEntries by remember { mutableStateOf(HistoryRepository.getHistory()) }

        // Contador para generar fechas simples
        var measurementCount by remember { mutableIntStateOf(0) }

        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                when {
                    // Ir hacia adelante
                    targetState.ordinal > initialState.ordinal -> {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 3 } + fadeOut())
                    }
                    // Ir hacia atrás
                    else -> {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it / 3 } + fadeOut())
                    }
                }
            }
        ) { screen ->
            when (screen) {
                Screen.WELCOME -> {
                    WelcomeScreen(
                        onGoalSelected = { goal ->
                            selectedGoal = goal
                            currentScreen = Screen.INPUT
                        }
                    )
                }

                Screen.INPUT -> {
                    InputScreen(
                        goal = selectedGoal,
                        onCalculate = { measurements ->
                            lastMeasurements = measurements
                            lastResult = BodyCompositionCalculator.calculate(measurements)
                            currentScreen = Screen.RESULTS
                        },
                        onBack = {
                            currentScreen = Screen.WELCOME
                        }
                    )
                }

                Screen.RESULTS -> {
                    val result = lastResult
                    val measurements = lastMeasurements

                    if (result != null && measurements != null) {
                        // Obtener la última entrada del historial para comparación
                        val previousEntry = historyEntries.firstOrNull()

                        ResultsScreen(
                            result = result,
                            measurements = measurements,
                            goal = selectedGoal,
                            previousEntry = previousEntry,
                            onSaveToHistory = {
                                measurementCount++
                                val dateString = "Medición #$measurementCount"
                                HistoryRepository.addEntry(
                                    measurements = measurements,
                                    result = result,
                                    goal = selectedGoal,
                                    dateString = dateString
                                )
                                historyEntries = HistoryRepository.getHistory()
                            },
                            onNewMeasurement = {
                                currentScreen = Screen.INPUT
                            },
                            onViewHistory = {
                                historyEntries = HistoryRepository.getHistory()
                                currentScreen = Screen.HISTORY
                            },
                            onBack = {
                                currentScreen = Screen.INPUT
                            }
                        )
                    }
                }

                Screen.HISTORY -> {
                    HistoryScreen(
                        entries = historyEntries,
                        onBack = {
                            currentScreen = if (lastResult != null) Screen.RESULTS else Screen.INPUT
                        },
                        onClearHistory = {
                            HistoryRepository.clearHistory()
                            historyEntries = HistoryRepository.getHistory()
                        },
                        onNewMeasurement = {
                            currentScreen = Screen.INPUT
                        }
                    )
                }
            }
        }
    }
}