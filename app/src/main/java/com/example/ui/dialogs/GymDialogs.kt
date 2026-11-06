package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.sp
import com.example.data.model.calculateEstimated1RM
import com.example.ui.theme.GymAmber
import com.example.ui.theme.GymCardBorder
import com.example.ui.theme.GymCyan
import com.example.ui.theme.GymDarkBg
import com.example.ui.theme.GymEmerald
import com.example.ui.theme.GymIndigo
import com.example.ui.theme.GymRose
import com.example.ui.theme.GymSurface
import com.example.ui.theme.GymSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, gender: String, age: Int, weight: Double, height: Double, goalPhase: String, challengeLevel: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var ageStr by remember { mutableStateOf("25") }
    var weightStr by remember { mutableStateOf("75.0") }
    var heightStr by remember { mutableStateOf("175.0") }
    var goalPhase by remember { mutableStateOf("Cutting") }
    var challengeLevel by remember { mutableStateOf("Intermediate") }

    val genders = listOf("Male", "Female", "Other")
    val phases = listOf("Cutting", "Maintaining", "Lean Bulking", "Bulking")
    val levels = listOf("Beginner", "Intermediate", "Advanced", "Elite")

    var genderExpanded by remember { mutableStateOf(false) }
    var phaseExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = GymCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Member", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_member_name_input")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_member_email_input")
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text("Age") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Weight (kg)") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = heightStr,
                    onValueChange = { heightStr = it },
                    label = { Text("Height (cm)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Gender dropdown
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = it }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        colors = outlinedFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genders.forEach { g ->
                            DropdownMenuItem(text = { Text(g) }, onClick = { gender = g; genderExpanded = false })
                        }
                    }
                }

                // Goal Phase dropdown
                ExposedDropdownMenuBox(
                    expanded = phaseExpanded,
                    onExpandedChange = { phaseExpanded = it }
                ) {
                    OutlinedTextField(
                        value = goalPhase,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Goal Phase") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = phaseExpanded) },
                        colors = outlinedFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = phaseExpanded,
                        onDismissRequest = { phaseExpanded = false }
                    ) {
                        phases.forEach { p ->
                            DropdownMenuItem(text = { Text(p) }, onClick = { goalPhase = p; phaseExpanded = false })
                        }
                    }
                }

                // Challenge Level dropdown
                ExposedDropdownMenuBox(
                    expanded = levelExpanded,
                    onExpandedChange = { levelExpanded = it }
                ) {
                    OutlinedTextField(
                        value = challengeLevel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Intensity Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                        colors = outlinedFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = levelExpanded,
                        onDismissRequest = { levelExpanded = false }
                    ) {
                        levels.forEach { l ->
                            DropdownMenuItem(text = { Text(l) }, onClick = { challengeLevel = l; levelExpanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val age = ageStr.toIntOrNull() ?: 25
                    val weight = weightStr.toDoubleOrNull() ?: 75.0
                    val height = heightStr.toDoubleOrNull() ?: 175.0
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onConfirm(name.trim(), email.trim(), gender, age, weight, height, goalPhase, challengeLevel)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymCyan, contentColor = GymDarkBg),
                modifier = Modifier.testTag("submit_add_member_button")
            ) {
                Text("Enroll Member", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartProtocolDialog(
    memberName: String,
    currentAge: Int,
    currentGender: String,
    currentWeight: Double,
    currentHeight: Double,
    currentPhase: String,
    currentLevel: String,
    onDismiss: () -> Unit,
    onGenerate: (age: Int, gender: String, weight: Double, height: Double, goalPhase: String, challengeLevel: String) -> Unit
) {
    var ageStr by remember { mutableStateOf(currentAge.toString()) }
    var gender by remember { mutableStateOf(currentGender) }
    var weightStr by remember { mutableStateOf(currentWeight.toString()) }
    var heightStr by remember { mutableStateOf(currentHeight.toString()) }
    var goalPhase by remember { mutableStateOf(currentPhase) }
    var challengeLevel by remember { mutableStateOf(currentLevel) }

    val genders = listOf("Male", "Female", "Other")
    val phases = listOf("Cutting", "Maintaining", "Lean Bulking", "Bulking")
    val levels = listOf("Beginner", "Intermediate", "Advanced", "Elite")

    var phaseExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GymIndigo)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Smart Protocol Generator", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Generate tailored diet & workout protocols for $memberName based on biometrics & goals.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text("Age") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Weight (kg)") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = heightStr,
                    onValueChange = { heightStr = it },
                    label = { Text("Height (cm)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Goal Phase dropdown
                ExposedDropdownMenuBox(
                    expanded = phaseExpanded,
                    onExpandedChange = { phaseExpanded = it }
                ) {
                    OutlinedTextField(
                        value = goalPhase,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Goal Phase") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = phaseExpanded) },
                        colors = outlinedFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = phaseExpanded,
                        onDismissRequest = { phaseExpanded = false }
                    ) {
                        phases.forEach { p ->
                            DropdownMenuItem(text = { Text(p) }, onClick = { goalPhase = p; phaseExpanded = false })
                        }
                    }
                }

                // Challenge Level dropdown
                ExposedDropdownMenuBox(
                    expanded = levelExpanded,
                    onExpandedChange = { levelExpanded = it }
                ) {
                    OutlinedTextField(
                        value = challengeLevel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Intensity Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                        colors = outlinedFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = levelExpanded,
                        onDismissRequest = { levelExpanded = false }
                    ) {
                        levels.forEach { l ->
                            DropdownMenuItem(text = { Text(l) }, onClick = { challengeLevel = l; levelExpanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val age = ageStr.toIntOrNull() ?: currentAge
                    val weight = weightStr.toDoubleOrNull() ?: currentWeight
                    val height = heightStr.toDoubleOrNull() ?: currentHeight
                    onGenerate(age, gender, weight, height, goalPhase, challengeLevel)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymIndigo, contentColor = Color.White),
                modifier = Modifier.testTag("apply_ai_protocol_button")
            ) {
                Text("Generate Plan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, instructor: String, room: String, dayOfWeek: String, timeSlot: String, capacity: Int, intensity: String, targetPhase: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("HIIT") }
    var instructor by remember { mutableStateOf("Coach Elena") }
    var room by remember { mutableStateOf("Studio A") }
    var dayOfWeek by remember { mutableStateOf("Monday") }
    var timeSlot by remember { mutableStateOf("07:00 AM") }
    var capacityStr by remember { mutableStateOf("15") }
    var intensity by remember { mutableStateOf("Intermediate") }
    var targetPhase by remember { mutableStateOf("All") }

    val categories = listOf("HIIT", "Strength", "Spin", "Yoga", "CrossFit", "Boxing")
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val timeSlots = listOf("07:00 AM", "09:00 AM", "12:30 PM", "05:30 PM", "06:00 PM", "07:30 PM")

    var catExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }
    var slotExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GymCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Gym Class", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Class Title (e.g. HIIT Inferno)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("class_title_input")
                )

                // Category
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        colors = outlinedFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEach { c ->
                            DropdownMenuItem(text = { Text(c) }, onClick = { category = c; catExpanded = false })
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Day
                    ExposedDropdownMenuBox(
                        expanded = dayExpanded,
                        onExpandedChange = { dayExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = dayOfWeek,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Day") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                            colors = outlinedFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dayExpanded,
                            onDismissRequest = { dayExpanded = false }
                        ) {
                            days.forEach { d ->
                                DropdownMenuItem(text = { Text(d) }, onClick = { dayOfWeek = d; dayExpanded = false })
                            }
                        }
                    }

                    // Slot
                    ExposedDropdownMenuBox(
                        expanded = slotExpanded,
                        onExpandedChange = { slotExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = timeSlot,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Time") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = slotExpanded) },
                            colors = outlinedFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = slotExpanded,
                            onDismissRequest = { slotExpanded = false }
                        ) {
                            timeSlots.forEach { t ->
                                DropdownMenuItem(text = { Text(t) }, onClick = { timeSlot = t; slotExpanded = false })
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = instructor,
                        onValueChange = { instructor = it },
                        label = { Text("Instructor") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("Studio Room") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = capacityStr,
                    onValueChange = { capacityStr = it },
                    label = { Text("Capacity (Max Spots)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cap = capacityStr.toIntOrNull() ?: 15
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), category, instructor.trim(), room.trim(), dayOfWeek, timeSlot, cap, intensity, targetPhase)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymCyan, contentColor = GymDarkBg),
                modifier = Modifier.testTag("submit_add_class_button")
            ) {
                Text("Schedule Class", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun LogWeightDialog(
    currentWeight: Double,
    onDismiss: () -> Unit,
    onConfirm: (weight: Double) -> Unit
) {
    var weightInput by remember { mutableStateOf(currentWeight.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Scale, contentDescription = null, tint = GymCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Daily Weight", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Record today's weigh-in to track progress and update BMI metrics.", color = TextSecondary, fontSize = 11.sp)
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("weight_log_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightInput.toDoubleOrNull()
                    if (w != null && w > 20.0) {
                        onConfirm(w)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymCyan, contentColor = GymDarkBg),
                modifier = Modifier.testTag("submit_log_weight_button")
            ) {
                Text("Save Entry", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddMealDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, desc: String, time: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("08:00 AM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = GymEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Meal", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Meal Name (e.g. Post-Workout Shake)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("meal_name_input")
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description & Ingredients") },
                    colors = outlinedFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (e.g. 11:30 AM)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), desc.trim(), time.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymEmerald, contentColor = GymDarkBg)
            ) {
                Text("Add Meal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onConfirm: (exercise: String, sets: String, reps: String, weight: String) -> Unit
) {
    var exercise by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("4") }
    var reps by remember { mutableStateOf("10") }
    var weight by remember { mutableStateOf("Moderate") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = GymCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Exercise Routine", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = exercise,
                    onValueChange = { exercise = it },
                    label = { Text("Exercise (e.g. Barbell Squats)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("exercise_name_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { sets = it },
                        label = { Text("Sets") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Target Load / Weight (e.g. 70kg)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (exercise.isNotBlank()) {
                        onConfirm(exercise.trim(), sets.trim(), reps.trim(), weight.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymCyan, contentColor = GymDarkBg)
            ) {
                Text("Add Routine", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogWorkoutActivityDialog(
    onDismiss: () -> Unit,
    onConfirm: (exercise: String, weightKg: Double, reps: Int, sets: Int, notes: String) -> Unit
) {
    var exercise by remember { mutableStateOf("Barbell Bench Press") }
    var weightStr by remember { mutableStateOf("80.0") }
    var repsStr by remember { mutableStateOf("5") }
    var setsStr by remember { mutableStateOf("3") }
    var notes by remember { mutableStateOf("") }

    val quickExercises = listOf(
        "Barbell Bench Press",
        "Barbell Back Squat",
        "Conventional Deadlift",
        "Overhead Press",
        "Incline Dumbbell Press",
        "Barbell Row",
        "Weighted Dips",
        "Pull-Ups"
    )

    val currentWeight = weightStr.toDoubleOrNull() ?: 0.0
    val currentReps = repsStr.toIntOrNull() ?: 1
    val currentSets = setsStr.toIntOrNull() ?: 1
    val est1RM = com.example.data.model.calculateEstimated1RM(currentWeight, currentReps)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = GymCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Workout Activity / PR", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Record weight lifted and reps to track progression and personal bests.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                // Quick Exercise Chips
                Text("POPULAR LIFTS", fontSize = 8.sp, fontWeight = FontWeight.Black, color = TextMuted, letterSpacing = 0.5.sp)
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(quickExercises) { qe ->
                        val isSelected = exercise == qe
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) GymCyan.copy(alpha = 0.2f) else GymSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GymCyan else GymCardBorder),
                            modifier = Modifier
                                .clickable { exercise = qe }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = qe,
                                fontSize = 8.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GymCyan else TextSecondary
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = exercise,
                    onValueChange = { exercise = it },
                    label = { Text("Exercise Name") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("log_exercise_name_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Weight (kg)") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("log_weight_input")
                    )
                    OutlinedTextField(
                        value = repsStr,
                        onValueChange = { repsStr = it },
                        label = { Text("Reps") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("log_reps_input")
                    )
                    OutlinedTextField(
                        value = setsStr,
                        onValueChange = { setsStr = it },
                        label = { Text("Sets") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Estimated 1RM Preview Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GymSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ESTIMATED 1-REP MAX (1RM)", fontSize = 8.sp, fontWeight = FontWeight.Black, color = TextMuted)
                            Text("Based on Epley Formula", fontSize = 8.sp, color = TextMuted)
                        }
                        Text(
                            text = "$est1RM kg",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = GymEmerald
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (e.g. RPE 8.5, clean form)") },
                    colors = outlinedFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (exercise.isNotBlank() && currentWeight > 0.0) {
                        onConfirm(exercise.trim(), currentWeight, currentReps, currentSets, notes.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymCyan, contentColor = GymDarkBg),
                modifier = Modifier.testTag("submit_log_activity_button")
            ) {
                Text("Save Workout Log", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GymCyan,
    unfocusedBorderColor = GymSurfaceVariant,
    focusedLabelColor = GymCyan,
    unfocusedLabelColor = TextSecondary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = GymCyan
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    initialName: String,
    initialWeight: Double,
    initialHeight: Double,
    initialGoalPhase: String,
    initialChallengeLevel: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, weight: Double, height: Double, goalPhase: String, challengeLevel: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var weightStr by remember { mutableStateOf(initialWeight.toString()) }
    var heightStr by remember { mutableStateOf(initialHeight.toString()) }
    var goalPhase by remember { mutableStateOf(initialGoalPhase) }
    var challengeLevel by remember { mutableStateOf(initialChallengeLevel) }

    val phases = listOf("Cutting", "Maintaining", "Lean Bulking", "Bulking")
    val levels = listOf("Beginner", "Intermediate", "Advanced", "Elite")

    var phaseExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Scale, contentDescription = null, tint = GymCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Account Details", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Update athlete biometrics, active goal phase, and workout challenge level.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Weight (kg)") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("edit_profile_weight_input")
                    )
                    OutlinedTextField(
                        value = heightStr,
                        onValueChange = { heightStr = it },
                        label = { Text("Height (cm)") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("edit_profile_height_input")
                    )
                }

                // Goal Phase dropdown
                ExposedDropdownMenuBox(
                    expanded = phaseExpanded,
                    onExpandedChange = { phaseExpanded = it }
                ) {
                    OutlinedTextField(
                        value = goalPhase,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Goal Phase") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = phaseExpanded) },
                        colors = outlinedFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = phaseExpanded,
                        onDismissRequest = { phaseExpanded = false }
                    ) {
                        phases.forEach { p ->
                            DropdownMenuItem(text = { Text(p) }, onClick = { goalPhase = p; phaseExpanded = false })
                        }
                    }
                }

                // Challenge Level dropdown
                ExposedDropdownMenuBox(
                    expanded = levelExpanded,
                    onExpandedChange = { levelExpanded = it }
                ) {
                    OutlinedTextField(
                        value = challengeLevel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Intensity Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                        colors = outlinedFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = levelExpanded,
                        onDismissRequest = { levelExpanded = false }
                    ) {
                        levels.forEach { l ->
                            DropdownMenuItem(text = { Text(l) }, onClick = { challengeLevel = l; levelExpanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightStr.toDoubleOrNull() ?: initialWeight
                    val h = heightStr.toDoubleOrNull() ?: initialHeight
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), w, h, goalPhase, challengeLevel)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymCyan, contentColor = GymDarkBg),
                modifier = Modifier.testTag("submit_edit_profile_button")
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignWorkoutDialog(
    athleteName: String,
    onDismiss: () -> Unit,
    onConfirm: (exercise: String, sets: String, reps: String, weight: String, muscleGroup: String) -> Unit
) {
    var exercise by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("4") }
    var reps by remember { mutableStateOf("10") }
    var weight by remember { mutableStateOf("Moderate") }
    var muscleGroup by remember { mutableStateOf("chest") }

    val muscleGroups = listOf(
        "chest" to "Chest (Pectorals)",
        "shoulders" to "Shoulders (Deltoids)",
        "biceps" to "Biceps (Arms)",
        "triceps" to "Triceps (Arms)",
        "back" to "Back (Lats & Traps)",
        "core" to "Core (Abdominals)",
        "quads" to "Quads (Quadriceps)",
        "glutes" to "Glutes & Hamstrings",
        "calves" to "Calves (Lower Leg)"
    )
    var muscleExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = GymCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Assign Daily Workout", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Prescribe an exercise for $athleteName for today. Target muscle will glow in their anatomy map upon completion.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = exercise,
                    onValueChange = { exercise = it },
                    label = { Text("Exercise Name (e.g. Barbell Bench Press)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("assign_exercise_name_input")
                )

                // Muscle Group Dropdown
                ExposedDropdownMenuBox(
                    expanded = muscleExpanded,
                    onExpandedChange = { muscleExpanded = it }
                ) {
                    OutlinedTextField(
                        value = muscleGroups.find { it.first == muscleGroup }?.second ?: muscleGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Muscle Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = muscleExpanded) },
                        colors = outlinedFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = muscleExpanded,
                        onDismissRequest = { muscleExpanded = false }
                    ) {
                        muscleGroups.forEach { (key, label) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = { muscleGroup = key; muscleExpanded = false })
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { sets = it },
                        label = { Text("Sets") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        colors = outlinedFieldColors(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Prescribed Weight / Load (e.g. 75kg)") },
                    colors = outlinedFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (exercise.isNotBlank()) {
                        onConfirm(exercise.trim(), sets.trim(), reps.trim(), weight.trim(), muscleGroup)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymCyan, contentColor = GymDarkBg),
                modifier = Modifier.testTag("submit_assign_workout_button")
            ) {
                Text("Assign Workout", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun CoachAiMealAnalyzerDialog(
    athleteName: String,
    athleteAge: Int,
    athleteGender: String,
    athleteWeight: Double,
    athleteHeight: Double,
    athleteGoalPhase: String,
    onDismiss: () -> Unit,
    onSendMealsToClient: (meals: List<com.example.data.model.MealEntity>, calories: Int, protein: Int, carbs: Int, fat: Int) -> Unit
) {
    val s = if (athleteGender.equals("Female", ignoreCase = true)) -161.0 else 5.0
    val bmr = (10.0 * athleteWeight) + (6.25 * athleteHeight) - (5.0 * athleteAge) + s
    val tdee = (bmr * 1.55).toInt()

    val (cal, prot, carb, fat) = com.example.data.repository.GymRepository.calculateMacrosForPhase(athleteWeight, athleteGoalPhase)

    val generatedMeals = listOf(
        com.example.data.model.MealEntity(
            id = "ai_m1_${System.currentTimeMillis()}",
            memberId = "",
            name = "Smart $athleteGoalPhase Breakfast",
            description = "4 egg whites + 1 whole egg, 75g oats, cinnamon & berries",
            time = "08:00 AM",
            isCompleted = false
        ),
        com.example.data.model.MealEntity(
            id = "ai_m2_${System.currentTimeMillis()}",
            memberId = "",
            name = "Midday High-Protein Fuel",
            description = "200g grilled chicken breast, 180g jasmine rice, steamed broccoli",
            time = "12:30 PM",
            isCompleted = false
        ),
        com.example.data.model.MealEntity(
            id = "ai_m3_${System.currentTimeMillis()}",
            memberId = "",
            name = "Pre-Workout Anabolic Fuel",
            description = "1 scoop whey isolate, 1 banana, rice cakes with peanut butter",
            time = "04:30 PM",
            isCompleted = false
        ),
        com.example.data.model.MealEntity(
            id = "ai_m4_${System.currentTimeMillis()}",
            memberId = "",
            name = "Recovery Dinner",
            description = "200g wild salmon fillet, baked sweet potato, asparagus",
            time = "08:00 PM",
            isCompleted = false
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GymIndigo)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Coach AI Nutrition Analyzer", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "AI biometric analysis for $athleteName based on ${athleteWeight}kg BW and $athleteGoalPhase phase.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GymSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("BMR: ${bmr.toInt()} kcal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("TDEE: $tdee kcal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GymCyan)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prescribed: $cal kcal • ${prot}g Protein • ${carb}g Carbs • ${fat}g Fat",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = GymEmerald
                        )
                    }
                }

                Text("PRESCRIBED MEALS FOR CLIENT", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextMuted)

                generatedMeals.forEach { meal ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GymDarkBg.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(meal.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(meal.time, fontSize = 9.sp, color = GymCyan)
                            }
                            Text(meal.description, fontSize = 9.sp, color = TextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSendMealsToClient(generatedMeals, cal, prot, carb, fat)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymEmerald, contentColor = GymDarkBg),
                modifier = Modifier.testTag("send_ai_meals_to_client_button")
            ) {
                Text("Send Meals to Client", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
