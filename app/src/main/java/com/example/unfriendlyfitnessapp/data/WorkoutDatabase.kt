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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Workout::class, WorkoutRecord::class], version = 6, exportSchema = false)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                )
                    .fallbackToDestructiveMigration(false)
                .addCallback(WorkoutDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class WorkoutDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.workoutDao())
                }
            }
        }

        suspend fun populateDatabase(workoutDao: WorkoutDao) {
            // Populate if we don't have enough workouts (20)
            if (workoutDao.getCount() < 240) { // Keep the exercise count check or change to workout count
                val exercises = listOf(
                    "Bench Press", "Squat", "Deadlift", "Overhead Press",
                    "Barbell Row", "Pull Ups", "Dips", "Lunges",
                    "Bicep Curls", "Tricep Extensions", "Leg Press", "Lat Pulldown"
                )
                // Generate 20 workouts (20 different days)
                for (day in 0 until 20) {
                    val timestamp = System.currentTimeMillis() - (day * 86400000L)
                    val workoutId = workoutDao.insertWorkout(Workout(timestamp = timestamp)).toInt()
                    
                    // Each workout has 12 unique exercises, each with 3 sets
                    exercises.forEachIndexed { index, name ->
                        for (setNum in 1..3) {
                            workoutDao.insert(
                                WorkoutRecord(
                                    workoutId = workoutId,
                                    exerciseName = name,
                                    weight = 20.0 + (index * 2),
                                    reps = 10,
                                    setNumber = setNum,
                                    timestamp = timestamp - (index * 1000) - (setNum * 100)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
