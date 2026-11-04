package com.example.ui.screens.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealEntity
import com.example.data.model.MemberEntity
import com.example.data.model.WorkoutExerciseEntity
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

@Composable
fun CoachProtocolScreen(
    member: MemberEntity?,
    meals: List<MealEntity>,
    workouts: List<WorkoutExerciseEntity>,
    onUpdatePhase: (String) -> Unit,
    onUpdateLevel: (String) -> Unit,
    onUpdateBiometrics: (weight: Double, height: Double, age: Int, gender: String) -> Unit,
    onUpdateMacros: (calories: Int, prot: Int, carb: Int, fat: Int) -> Unit,
    onDeleteMeal: (String) -> Unit,
    onDeleteWorkout: (String) -> Unit,
    onOpenAddMeal: () -> Unit,
    onOpenAddWorkout: () -> Unit,
    onOpenAiGen: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (member == null) {
        Box(
            modifier = modifier.fillMaxSize().background(GymDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Text("Select an athlete from the roster to edit protocols.", color = TextSecondary, fontSize = 12.sp)
        }
        return
    }

    var weightInput by remember(member.currentWeight) { mutableStateOf(member.currentWeight.toString()) }
    var heightInput by remember(member.heightCm) { mutableStateOf(member.heightCm.toString()) }
    var ageInput by remember(member.age) { mutableStateOf(member.age.toString()) }

    var calInput by remember(member.dailyCalories) { mutableStateOf(member.dailyCalories.toString()) }
    var protInput by remember(member.proteinG) { mutableStateOf(member.proteinG.toString()) }
    var carbInput by remember(member.carbsG) { mutableStateOf(member.carbsG.toString()) }
    var fatInput by remember(member.fatG) { mutableStateOf(member.fatG.toString()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Member Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("EDITING PROTOCOL", fontSize = 8.sp, fontWeight = FontWeight.Black, color = GymCyan, letterSpacing = 1.sp)
                            Text(member.name, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Text("${member.email} • ${member.membershipTier}", fontSize = 10.sp, color = TextMuted)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GymIndigo.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GymIndigo.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .testTag("coach_ai_plan_button")
                                .clickable { onOpenAiGen() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GymIndigo, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Generator", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymIndigo)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Biometrics Inputs
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BiometricInputField(
                            label = "Weight (kg)",
                            value = weightInput,
                            onValueChange = {
                                weightInput = it
                                val w = it.toDoubleOrNull() ?: member.currentWeight
                                onUpdateBiometrics(w, member.heightCm, member.age, member.gender)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        BiometricInputField(
                            label = "Height (cm)",
                            value = heightInput,
                            onValueChange = {
                                heightInput = it
                                val h = it.toDoubleOrNull() ?: member.heightCm
                                onUpdateBiometrics(member.currentWeight, h, member.age, member.gender)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        BiometricInputField(
                            label = "Age",
                            value = ageInput,
                            onValueChange = {
                                ageInput = it
                                val a = it.toIntOrNull() ?: member.age
                                onUpdateBiometrics(member.currentWeight, member.heightCm, a, member.gender)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Goal Phase Strategy Selection
                    Text("GOAL PHASE STRATEGY (MACRO AUTO-CALC)", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = TextSecondary, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Cutting", "Maintaining", "Lean Bulking", "Bulking").forEach { phase ->
                            val isSelected = member.goalPhase == phase
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) GymCyan else GymSurfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("phase_btn_$phase")
                                    .clickable { onUpdatePhase(phase) }
                                    .padding(vertical = 7.dp),
                                contentColor = if (isSelected) GymDarkBg else TextSecondary
                            ) {
                                Text(
                                    text = phase,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Intensity Level Selection
                    Text("CHALLENGE & INTENSITY LEVEL", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = TextSecondary, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Beginner", "Intermediate", "Advanced", "Elite").forEach { lvl ->
                            val isSelected = member.challengeLevel == lvl
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) GymIndigo else GymSurfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpdateLevel(lvl) }
                                    .padding(vertical = 7.dp),
                                contentColor = if (isSelected) Color.White else TextSecondary
                            ) {
                                Text(
                                    text = lvl,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Nutrition & Macro Editor
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = GymEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DAILY MACRO NUTRITION", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GymEmerald.copy(alpha = 0.2f),
                            modifier = Modifier
                                .testTag("coach_add_meal_button")
                                .clickable { onOpenAddMeal() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = GymEmerald, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Add Meal", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymEmerald)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Macro Boxes
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MacroInputBox(
                            title = "ENERGY",
                            unit = "kcal",
                            value = calInput,
                            onValueChange = {
                                calInput = it
                                onUpdateMacros(it.toIntOrNull() ?: member.dailyCalories, member.proteinG, member.carbsG, member.fatG)
                            },
                            color = GymAmber,
                            modifier = Modifier.weight(1f)
                        )
                        MacroInputBox(
                            title = "PROTEIN",
                            unit = "g",
                            value = protInput,
                            onValueChange = {
                                protInput = it
                                onUpdateMacros(member.dailyCalories, it.toIntOrNull() ?: member.proteinG, member.carbsG, member.fatG)
                            },
                            color = GymRose,
                            modifier = Modifier.weight(1f)
                        )
                        MacroInputBox(
                            title = "CARBS",
                            unit = "g",
                            value = carbInput,
                            onValueChange = {
                                carbInput = it
                                onUpdateMacros(member.dailyCalories, member.proteinG, it.toIntOrNull() ?: member.carbsG, member.fatG)
                            },
                            color = GymEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        MacroInputBox(
                            title = "FATS",
                            unit = "g",
                            value = fatInput,
                            onValueChange = {
                                fatInput = it
                                onUpdateMacros(member.dailyCalories, member.proteinG, member.carbsG, it.toIntOrNull() ?: member.fatG)
                            },
                            color = GymCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Meals list
                    if (meals.isEmpty()) {
                        Text("No meals prescribed yet. Tap '+ Add Meal' or use 'AI Generator'.", color = TextMuted, fontSize = 10.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            meals.forEach { meal ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GymSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(meal.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(meal.time, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = GymCyan)
                                            }
                                            Text(meal.description, fontSize = 9.sp, color = TextMuted, maxLines = 1)
                                        }
                                        IconButton(onClick = { onDeleteMeal(meal.id) }, modifier = Modifier.size(26.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GymRose, modifier = Modifier.size(15.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Workout Protocol Editor
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = GymCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WORKOUT PROTOCOL", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GymCyan.copy(alpha = 0.2f),
                            modifier = Modifier
                                .testTag("coach_add_workout_button")
                                .clickable { onOpenAddWorkout() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = GymCyan, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Add Routine", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymCyan)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (workouts.isEmpty()) {
                        Text("No exercises prescribed yet. Tap '+ Add Routine' or use 'AI Generator'.", color = TextMuted, fontSize = 10.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            workouts.forEach { ex ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GymSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ex.exercise, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text(
                                                "${ex.sets} sets × ${ex.reps} reps • ${ex.weight}",
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = GymCyan
                                            )
                                        }
                                        IconButton(onClick = { onDeleteWorkout(ex.id) }, modifier = Modifier.size(26.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GymRose, modifier = Modifier.size(15.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BiometricInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 9.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GymCyan,
            unfocusedBorderColor = GymCardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        modifier = modifier
    )
}

@Composable
fun MacroInputBox(
    title: String,
    unit: String,
    value: String,
    onValueChange: (String) -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = GymSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = TextMuted)
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = color,
                    unfocusedTextColor = color
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            )
            Text(unit, fontSize = 7.5.sp, color = TextSecondary)
        }
    }
}
