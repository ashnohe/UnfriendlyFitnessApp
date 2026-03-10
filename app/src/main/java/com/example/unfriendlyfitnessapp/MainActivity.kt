/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.unfriendlyfitnessapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.unfriendlyfitnessapp.data.WorkoutRecord
import com.example.unfriendlyfitnessapp.ui.AddExerciseScreen
import com.example.unfriendlyfitnessapp.ui.StatsScreen
import com.example.unfriendlyfitnessapp.ui.WorkoutListScreen
import com.example.unfriendlyfitnessapp.ui.theme.UnfriendlyFitnessAppTheme

/**
 * Navigation destinations for the app.
 */
sealed class Screen {
    data object Workouts : Screen()
    data object Stats : Screen()
    data class AddExercise(val workoutId: Int? = null) : Screen()
    data class EditExercise(val record: WorkoutRecord) : Screen()
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Disable Window Adjustment: prevents the window from resizing or panning when the keyboard opens.
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        setContent {
            UnfriendlyFitnessAppTheme(dynamicColor = true) {
                // Navigation 3 state management: the backstack is a simple mutable state list.
                // We initialize it with the "Workouts" screen as the root.
                val backstack = remember { mutableStateListOf<Screen>(Screen.Workouts) }
                
                // Adaptive UI: Determine the window adaptive info (window size class, etc.)
                val adaptiveInfo = currentWindowAdaptiveInfo()

                // NavigationSuiteScaffold handles switching between NavigationBar (compact)
                // and NavigationRail (wide/expanded) automatically.
                NavigationSuiteScaffold(
                    layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo),
                    navigationSuiteItems = {
                        item(
                            selected = backstack.firstOrNull() is Screen.Workouts,
                            onClick = {
                                if (backstack.firstOrNull() !is Screen.Workouts) {
                                    // Switch to Workouts branch
                                    backstack.clear()
                                    backstack.add(Screen.Workouts)
                                } else {
                                    // If already in Workouts, pop to root
                                    while (backstack.size > 1) {
                                        backstack.removeAt(backstack.size - 1)
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Workouts") },
                            label = { Text("Workouts") }
                        )
                        item(
                            selected = backstack.firstOrNull() is Screen.Stats,
                            onClick = {
                                if (backstack.firstOrNull() !is Screen.Stats) {
                                    // Switch to Stats branch
                                    backstack.clear()
                                    backstack.add(Screen.Stats)
                                }
                            },
                            icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
                            label = { Text("Stats") }
                        )
                    }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Navigation 3 NavDisplay renders the current screen from the backstack.
                        NavDisplay(
                            backStack = backstack,
                            onBack = { 
                                if (backstack.size > 1) {
                                    backstack.removeAt(backstack.size - 1)
                                } 
                            },
                            entryProvider = { screen ->
                                when (screen) {
                                    is Screen.Workouts -> NavEntry(screen) {
                                        WorkoutListScreen(
                                            onAddExerciseToWorkout = { workoutId -> 
                                                backstack.add(Screen.AddExercise(workoutId)) 
                                            },
                                            onEditExercise = { record -> 
                                                backstack.add(Screen.EditExercise(record)) 
                                            }
                                        )
                                    }
                                    is Screen.Stats -> NavEntry(screen) {
                                        StatsScreen()
                                    }
                                    is Screen.AddExercise -> NavEntry(screen) {
                                        AddExerciseScreen(
                                            workoutId = screen.workoutId,
                                            onBack = { backstack.removeAt(backstack.size - 1) }
                                        )
                                    }
                                    is Screen.EditExercise -> NavEntry(screen) {
                                        AddExerciseScreen(
                                            existingRecord = screen.record,
                                            onBack = { backstack.removeAt(backstack.size - 1) }
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
