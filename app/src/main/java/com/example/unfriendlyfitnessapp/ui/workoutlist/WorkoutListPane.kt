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
import androidx.compose.foundation.lazy.items
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
fun WorkoutListPane(
    workouts: List<Workout>,
    records: List<WorkoutRecord>,
    onWorkoutClick: (Int) -> Unit,
    onDeleteWorkout: (Int) -> Unit,
    onAddWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
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
                        .clickable { onWorkoutClick(workout.id) }
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
                    IconButton(onClick = { onDeleteWorkout(workout.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Workout")
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
        FloatingActionButton(
            onClick = onAddWorkout,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Workout")
        }
    }
}
