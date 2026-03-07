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

package com.example.unfriendlyfitnessapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unfriendlyfitnessapp.data.WorkoutRecord
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                "Workouts",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        items(workouts) { workout ->
                            val workoutRecords = records.filter { it.workoutId == workout.id }
                            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(workout.timestamp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, workout.id)
                                        }
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(dateStr, style = MaterialTheme.typography.titleLarge)
                                    val uniqueExercises = workoutRecords.map { it.exerciseName }.distinct().size
                                    Text(
                                        "$uniqueExercises exercises",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = {
                                    viewModel.deleteWorkout(workout.id)
                                    if (navigator.currentDestination?.contentKey == workout.id) {
                                        scope.launch {
                                            navigator.navigateBack()
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Workout")
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                val id = viewModel.addWorkout()
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, id)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Workout")
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                val selectedWorkoutId = navigator.currentDestination?.contentKey
                if (selectedWorkoutId != null) {
                    val workout = workouts.find { it.id == selectedWorkoutId }
                    val workoutRecords = records.filter { it.workoutId == selectedWorkoutId }
                    val dateStr = workout?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it.timestamp)) } ?: ""
                    
                    val groupedWorkoutRecords = workoutRecords.groupBy { it.exerciseName }
                    
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    "Exercises - $dateStr",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            groupedWorkoutRecords.forEach { (exerciseName, sets) ->
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onEditExercise(sets.first()) }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(exerciseName, style = MaterialTheme.typography.titleMedium)
                                            Text(
                                                "${sets.size} sets",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(onClick = {
                                            viewModel.deleteRecordsForExercise(selectedWorkoutId, exerciseName)
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Remove Exercise")
                                        }
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                        FloatingActionButton(
                            onClick = { onAddExerciseToWorkout(selectedWorkoutId) },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Exercise to Workout")
                        }
                    }
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Select a workout from the list")
                    }
                }
            }
        }
    )
}
