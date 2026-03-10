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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.unfriendlyfitnessapp.data.WorkoutRecord
import com.example.unfriendlyfitnessapp.ui.AddExerciseScreen
import com.example.unfriendlyfitnessapp.ui.WorkoutListScreen
import com.example.unfriendlyfitnessapp.ui.theme.UnfriendlyFitnessAppTheme

sealed class Screen {
    data object Main : Screen()
    data class AddExercise(val workoutId: Int? = null) : Screen()
    data class EditExercise(val record: WorkoutRecord) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Disable Window Adjustment: prevents the window from resizing or panning when the keyboard opens.
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        setContent {
            UnfriendlyFitnessAppTheme(dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val backstack = remember { mutableStateListOf<Screen>(Screen.Main) }

                    NavDisplay(
                        backStack = backstack,
                        onBack = { if (backstack.size > 1) backstack.removeAt(backstack.size - 1) },
                        entryProvider = { screen ->
                            when (screen) {
                                is Screen.Main -> NavEntry(screen) {
                                    WorkoutListScreen(
                                        onAddExerciseToWorkout = { workoutId -> backstack.add(Screen.AddExercise(workoutId)) },
                                        onEditExercise = { record -> backstack.add(Screen.EditExercise(record)) }
                                    )
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
