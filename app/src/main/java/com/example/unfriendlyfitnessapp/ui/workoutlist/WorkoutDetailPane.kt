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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unfriendlyfitnessapp.data.Workout
import com.example.unfriendlyfitnessapp.data.WorkoutRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutDetailPane(
    selectedWorkoutId: Int?,
    workout: Workout?,
    workoutRecords: List<WorkoutRecord>,
    onEditExercise: (WorkoutRecord) -> Unit,
    onDeleteRecordsForExercise: (Int, String) -> Unit,
    onAddExerciseToWorkout: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedWorkoutId != null) {
        val dateStr = workout?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it.timestamp)) } ?: ""
        val groupedWorkoutRecords = workoutRecords.groupBy { it.exerciseName }

        Box(modifier = modifier.fillMaxSize()) {
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
                                onDeleteRecordsForExercise(selectedWorkoutId, exerciseName)
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
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a workout from the list")
        }
    }
}
