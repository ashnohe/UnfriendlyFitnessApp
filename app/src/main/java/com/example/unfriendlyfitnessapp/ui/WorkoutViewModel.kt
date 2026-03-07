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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unfriendlyfitnessapp.data.Workout
import com.example.unfriendlyfitnessapp.data.WorkoutDatabase
import com.example.unfriendlyfitnessapp.data.WorkoutRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val db = WorkoutDatabase.getDatabase(application, viewModelScope)
    private val dao = db.workoutDao()

    val allWorkouts: StateFlow<List<Workout>> = dao.getAllWorkouts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allRecords: StateFlow<List<WorkoutRecord>> = dao.getAllRecords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val baseExercises = listOf(
        "Bench Press", "Squat", "Deadlift", "Overhead Press",
        "Barbell Row", "Pull Ups", "Dips", "Lunges",
        "Bicep Curls", "Tricep Extensions", "Leg Press", "Lat Pulldown"
    )

    val allExerciseNames: StateFlow<List<String>> = dao.getAllRecords()
        .map { records ->
            (baseExercises + records.map { it.exerciseName }).distinct().sorted()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = baseExercises
        )

    suspend fun addWorkout(): Int {
        return dao.insertWorkout(Workout()).toInt()
    }

    fun deleteWorkout(workoutId: Int) {
        viewModelScope.launch {
            dao.deleteRecordsForWorkout(workoutId)
            dao.deleteWorkout(workoutId)
        }
    }

    fun saveExerciseSets(workoutId: Int, exerciseName: String, sets: List<Pair<Double, Int>>) {
        viewModelScope.launch {
            dao.deleteRecordsForExercise(workoutId, exerciseName)
            sets.forEachIndexed { index, (weight, reps) ->
                dao.insert(
                    WorkoutRecord(
                        workoutId = workoutId,
                        exerciseName = exerciseName,
                        weight = weight,
                        reps = reps,
                        setNumber = index + 1
                    )
                )
            }
        }
    }

    fun deleteRecordsForExercise(workoutId: Int, exerciseName: String) {
        viewModelScope.launch {
            dao.deleteRecordsForExercise(workoutId, exerciseName)
        }
    }

    suspend fun getLastPerformances(exerciseName: String, limit: Int = 10): List<List<WorkoutRecord>> {
        val allRecords = dao.getRecordsForExercise(exerciseName)
        return allRecords.groupBy { it.workoutId }
            .values
            .sortedByDescending { it.first().timestamp }
            .take(limit)
    }

    suspend fun getRecordsForExerciseInWorkout(workoutId: Int, exerciseName: String): List<WorkoutRecord> {
        return dao.getRecordsForExerciseInWorkout(workoutId, exerciseName)
    }
}
