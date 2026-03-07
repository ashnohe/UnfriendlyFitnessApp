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

package com.example.unfriendlyfitnessapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Query("SELECT * FROM workout_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<WorkoutRecord>>

    @Insert
    suspend fun insert(record: WorkoutRecord): Long

    @Update
    suspend fun update(record: WorkoutRecord): Int

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: Int): Int

    @Query("DELETE FROM workout_records WHERE workoutId = :workoutId")
    suspend fun deleteRecordsForWorkout(workoutId: Int): Int

    @Query("DELETE FROM workout_records WHERE workoutId = :workoutId AND exerciseName = :exerciseName")
    suspend fun deleteRecordsForExercise(workoutId: Int, exerciseName: String): Int

    @Query("SELECT * FROM workout_records WHERE workoutId = :workoutId ORDER BY timestamp ASC")
    fun getRecordsForWorkout(workoutId: Int): Flow<List<WorkoutRecord>>

    @Query("SELECT * FROM workout_records WHERE workoutId = :workoutId AND exerciseName = :exerciseName ORDER BY setNumber ASC")
    suspend fun getRecordsForExerciseInWorkout(workoutId: Int, exerciseName: String): List<WorkoutRecord>

    @Query("SELECT * FROM workout_records WHERE exerciseName = :name ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastRecordForExercise(name: String): WorkoutRecord?

    @Query("SELECT * FROM workout_records WHERE exerciseName = :name ORDER BY timestamp DESC")
    suspend fun getRecordsForExercise(name: String): List<WorkoutRecord>

    @Query("SELECT COUNT(*) FROM workout_records")
    suspend fun getCount(): Int
}
