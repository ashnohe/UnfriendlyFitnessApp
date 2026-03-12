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

package com.example.unfriendlyfitnessapp.ui.workoutlist

import androidx.activity.compose.BackHandler
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unfriendlyfitnessapp.data.WorkoutRecord
import com.example.unfriendlyfitnessapp.ui.WorkoutViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun WorkoutListScreen(
    onAddExerciseToWorkout: (Int) -> Unit,
    onEditExercise: (WorkoutRecord) -> Unit,
    viewModel: WorkoutViewModel = viewModel()
) {
    val workouts by viewModel.allWorkouts.collectAsState()
    val records by viewModel.allRecords.collectAsState()
    
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val scope = rememberCoroutineScope()

    BackHandler(enabled = navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                WorkoutListPane(
                    workouts = workouts,
                    records = records,
                    onWorkoutClick = { workoutId ->
                        scope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, workoutId)
                        }
                    },
                    onDeleteWorkout = { workoutId ->
                        viewModel.deleteWorkout(workoutId)
                        if (navigator.currentDestination?.contentKey == workoutId) {
                            scope.launch {
                                navigator.navigateBack()
                            }
                        }
                    },
                    onAddWorkout = {
                        scope.launch {
                            val id = viewModel.addWorkout()
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, id)
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val selectedWorkoutId = navigator.currentDestination?.contentKey
                WorkoutDetailPane(
                    selectedWorkoutId = selectedWorkoutId,
                    workout = workouts.find { it.id == selectedWorkoutId },
                    workoutRecords = records.filter { it.workoutId == selectedWorkoutId },
                    onEditExercise = onEditExercise,
                    onDeleteRecordsForExercise = { workoutId, exerciseName ->
                        viewModel.deleteRecordsForExercise(workoutId, exerciseName)
                    },
                    onAddExerciseToWorkout = onAddExerciseToWorkout
                )
            }
        }
    )
}
