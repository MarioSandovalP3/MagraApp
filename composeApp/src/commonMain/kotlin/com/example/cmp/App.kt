package com.example.cmp

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.cmp.data.*
import com.example.cmp.domain.BodyCompositionCalculator
import com.example.cmp.ui.screens.*
import com.example.cmp.ui.theme.MagraTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.unit.dp
import com.example.cmp.ui.components.SettingsPanel
import kotlinx.coroutines.launch

/**
 * Pantallas de la aplicación para la navegación manual.
 */
enum class Screen {
    WELCOME,
    ACTIVITY,
    INPUT,
    RESULTS,
    HISTORY,
    AI_SETTINGS,
    ABOUT
}

/**
 * Punto de entrada principal de la aplicación MagraApp.
 */
@Composable
@Preview
fun App() {
    MagraTheme {
        LaunchedEffect(Unit) {
            HistoryRepository.initialize()
        }

        var currentScreen by remember { mutableStateOf(Screen.WELCOME) }
        var previousScreen by remember { mutableStateOf(Screen.WELCOME) }

        var selectedGoal by remember { mutableStateOf(UserGoal.MAINTAIN) }
        var selectedActivity by remember { mutableStateOf(ActivityLevel.SEDENTARY) }
        var lastMeasurements by remember { mutableStateOf<UserMeasurements?>(null) }
        var lastResult by remember { mutableStateOf<BodyCompositionResult?>(null) }
        var historyEntries by remember { mutableStateOf(HistoryRepository.getHistory()) }
        var measurementCount by remember { mutableIntStateOf(0) }

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = androidx.compose.ui.Modifier
                        .widthIn(max = 340.dp)
                        .fillMaxHeight()
                ) {
                    SettingsPanel(
                        onClose = {
                            coroutineScope.launch { drawerState.close() }
                        },
                        onOpenAiSettings = {
                            previousScreen = currentScreen
                            currentScreen = Screen.AI_SETTINGS
                        },
                        onOpenAbout = {
                            previousScreen = currentScreen
                            currentScreen = Screen.ABOUT
                        }
                    )
                }
            }
        ) {
            AnimatedContent(
                targetState = currentScreen,
            transitionSpec = {
                when {
                    targetState.ordinal > initialState.ordinal -> {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 3 } + fadeOut())
                    }
                    else -> {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it / 3 } + fadeOut())
                    }
                }
            }
        ) { screen ->
            PlatformBackHandler {
                when (currentScreen) {
                    Screen.WELCOME -> { }
                    Screen.ACTIVITY -> currentScreen = Screen.WELCOME
                    Screen.INPUT -> currentScreen = Screen.ACTIVITY
                    Screen.RESULTS -> currentScreen = Screen.INPUT
                    Screen.HISTORY -> currentScreen = if (lastResult != null) Screen.RESULTS else Screen.INPUT
                    Screen.AI_SETTINGS -> currentScreen = Screen.WELCOME
                    Screen.ABOUT -> currentScreen = Screen.WELCOME
                }
            }

            when (screen) {
                Screen.WELCOME -> {
                    WelcomeScreen(
                        onGoalSelected = { goal ->
                            selectedGoal = goal
                            currentScreen = Screen.ACTIVITY
                        },
                        onOpenSettings = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }

                Screen.ACTIVITY -> {
                    ActivityScreen(
                        goal = selectedGoal,
                        onContinue = { activity ->
                            selectedActivity = activity
                            currentScreen = Screen.INPUT
                        },
                        onBack = { currentScreen = Screen.WELCOME },
                        onOpenSettings = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }

                Screen.INPUT -> {
                    InputScreen(
                        goal = selectedGoal,
                        activityLevel = selectedActivity,
                        onCalculate = { measurements ->
                            lastMeasurements = measurements
                            lastResult = BodyCompositionCalculator.calculate(measurements)
                            currentScreen = Screen.RESULTS
                        },
                        onBack = { currentScreen = Screen.ACTIVITY },
                        onOpenSettings = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }

                Screen.RESULTS -> {
                    val result = lastResult
                    val measurements = lastMeasurements
                    if (result != null && measurements != null) {
                        val previousEntry = historyEntries.firstOrNull()
                        ResultsScreen(
                            result = result,
                            measurements = measurements,
                            goal = selectedGoal,
                            previousEntry = previousEntry,
                            onSaveToHistory = {
                                measurementCount++
                                HistoryRepository.addEntry(
                                    measurements = measurements,
                                    result = result,
                                    goal = selectedGoal,
                                    dateString = "Medición #$measurementCount"
                                )
                                historyEntries = HistoryRepository.getHistory()
                            },
                            onNewMeasurement = { currentScreen = Screen.ACTIVITY },
                            onViewHistory = {
                                historyEntries = HistoryRepository.getHistory()
                                currentScreen = Screen.HISTORY
                            },
                            onBack = { currentScreen = Screen.INPUT },
                            onOpenSettings = {
                                coroutineScope.launch { drawerState.open() }
                            }
                        )
                    }
                }

                Screen.HISTORY -> {
                    HistoryScreen(
                        entries = historyEntries,
                        onBack = { currentScreen = if (lastResult != null) Screen.RESULTS else Screen.INPUT },
                        onClearHistory = {
                            HistoryRepository.clearHistory()
                            historyEntries = HistoryRepository.getHistory()
                        },
                        onNewMeasurement = { currentScreen = Screen.ACTIVITY },
                        onOpenSettings = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }

                Screen.AI_SETTINGS -> {
                    AiSettingsScreen(
                        onBack = { currentScreen = previousScreen }
                    )
                }

                Screen.ABOUT -> {
                    AboutScreen(
                        onBack = { currentScreen = previousScreen }
                    )
                }
            }
            }
        }
    }
}