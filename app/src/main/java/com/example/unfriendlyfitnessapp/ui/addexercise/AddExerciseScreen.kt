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

package com.example.unfriendlyfitnessapp.ui.addexercise

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unfriendlyfitnessapp.data.WorkoutRecord
import com.example.unfriendlyfitnessapp.ui.WorkoutViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    workoutId: Int? = null,
    existingRecord: WorkoutRecord? = null,
    onBack: () -> Unit,
    viewModel: WorkoutViewModel = viewModel()
) {
    val allExerciseNames by viewModel.allExerciseNames.collectAsState()
    var selectedExercise by remember { mutableStateOf(existingRecord?.exerciseName ?: "") }
    
    var exerciseSets by remember {
        mutableStateOf(
            if (existingRecord != null) listOf(existingRecord.weight.toString() to existingRecord.reps.toString())
            else listOf("" to "")
        ) 
    }

    val effectiveWorkoutId = workoutId ?: existingRecord?.workoutId

    LaunchedEffect(selectedExercise, effectiveWorkoutId) {
        if (effectiveWorkoutId != null && selectedExercise.isNotEmpty()) {
            val records = viewModel.getRecordsForExerciseInWorkout(effectiveWorkoutId, selectedExercise)
            if (records.isNotEmpty()) {
                exerciseSets = records.map { it.weight.toString() to it.reps.toString() }
            }
        }
    }
    
    var expanded by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var lastPerformances by remember { mutableStateOf<List<List<WorkoutRecord>>>(emptyList()) }
    val scope = rememberCoroutineScope()

    BackHandler(enabled = !showDiscardDialog) {
        showDiscardDialog = true
    }

    if (showDiscardDialog) {
        DiscardChangesDialog(
            onConfirm = { onBack() },
            onDismiss = { showDiscardDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(if (existingRecord != null) "Edit Exercise" else "Add Exercise") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (allExerciseNames.contains(selectedExercise) && effectiveWorkoutId != null) {
                                val setsData = exerciseSets.mapNotNull { (w, r) ->
                                    val weightVal = w.toDoubleOrNull()
                                    val repsVal = r.toIntOrNull()
                                    if (weightVal != null && repsVal != null) {
                                        weightVal to repsVal
                                    } else null
                                }
                                if (setsData.isNotEmpty()) {
                                    viewModel.saveExerciseSets(effectiveWorkoutId, selectedExercise, setsData)
                                    onBack()
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Save")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedExercise,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Exercise Name") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        allExerciseNames.forEach { exercise ->
                            DropdownMenuItem(
                                text = { Text(exercise) },
                                onClick = {
                                    selectedExercise = exercise
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (allExerciseNames.contains(selectedExercise)) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                lastPerformances = viewModel.getLastPerformances(selectedExercise)
                                showBottomSheet = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Previous Performances")
                    }
                }

                exerciseSets.forEachIndexed { index, set ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Set ${index + 1}", modifier = Modifier.width(48.dp))
                        OutlinedTextField(
                            value = set.first,
                            onValueChange = { newWeight ->
                                if (newWeight.isEmpty() || newWeight.toDoubleOrNull() != null || newWeight.endsWith(".")) {
                                    val newList = exerciseSets.toMutableList()
                                    newList[index] = newWeight to set.second
                                    exerciseSets = newList
                                }
                            },
                            label = { Text("Weight (lbs)") },
                            modifier = Modifier.weight(1.2f),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = set.second,
                            onValueChange = { newReps ->
                                if (newReps.isEmpty() || newReps.toIntOrNull() != null) {
                                    val newList = exerciseSets.toMutableList()
                                    newList[index] = set.first to newReps
                                    exerciseSets = newList
                                }
                            },
                            label = { Text("Reps") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            singleLine = true
                        )
                        if (exerciseSets.size > 1) {
                            IconButton(
                                onClick = {
                                    val newList = exerciseSets.toMutableList()
                                    newList.removeAt(index)
                                    exerciseSets = newList
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove Set",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(48.dp))
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        val lastSet = exerciseSets.lastOrNull()
                        exerciseSets = exerciseSets + (lastSet ?: ("" to ""))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Set")
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (showBottomSheet) {
        PreviousPerformancesBottomSheet(
            lastPerformances = lastPerformances,
            onDismissRequest = { showBottomSheet = false }
        )
    }
}
