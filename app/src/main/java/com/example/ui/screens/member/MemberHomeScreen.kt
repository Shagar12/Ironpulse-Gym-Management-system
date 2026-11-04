package com.example.ui.screens.member

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.components.BMIBadge
import com.example.ui.components.IntensityBadge
import com.example.ui.components.PhasePill
import com.example.ui.components.calculateBMI
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
fun MemberHomeScreen(
    member: MemberEntity?,
    meals: List<MealEntity>,
    workouts: List<WorkoutExerciseEntity>,
    onToggleCheckIn: () -> Unit,
    onLogWeight: (Double) -> Unit,
    onRenewSubscription: (plan: String, amount: Double) -> Unit,
    onNavigateToDiet: () -> Unit,
    onNavigateToWorkout: () -> Unit,
    onNavigateToClasses: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (member == null) {
        Box(
            modifier = modifier.fillMaxSize().background(GymDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading athlete profile...", color = TextSecondary)
        }
        return
    }

    var weightInput by remember { mutableStateOf("") }

    val completedMeals = meals.count { it.isCompleted }
    val totalMeals = meals.size.coerceAtLeast(1)
    val mealProgress = (completedMeals.toFloat() / totalMeals.toFloat()).coerceIn(0f, 1f)

    val completedWorkouts = workouts.count { it.isCompleted }
    val totalWorkouts = workouts.size.coerceAtLeast(1)
    val workoutProgress = (completedWorkouts.toFloat() / totalWorkouts.toFloat()).coerceIn(0f, 1f)

    val bmiInfo = calculateBMI(member.currentWeight, member.heightCm)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Expiration Warning Banner if <= 5 days
        if (member.subscriptionDaysLeft <= 5) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GymRose.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymRose.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = GymRose, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (member.subscriptionDaysLeft <= 0) "Membership Expired" else "Membership Due in ${member.subscriptionDaysLeft} Days",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GymRose
                                )
                                Text("Renew 30-day cycle to maintain facility access.", fontSize = 9.sp, color = TextMuted)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GymCyan,
                            modifier = Modifier
                                .testTag("renew_now_banner_button")
                                .clickable { onRenewSubscription("30-Day Monthly Pass", 49.0) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Pay $49", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                        }
                    }
                }
            }
        }

        // Gym Check-In Interactive Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (member.atGym) GymEmerald.copy(alpha = 0.12f) else GymSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (member.atGym) GymEmerald.copy(alpha = 0.5f) else GymCardBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (member.atGym) GymEmerald else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GYM PRESENCE STATUS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                        Text(
                            text = if (member.atGym)
                                "Coach notified: You are currently checked into the facility 📍"
                            else
                                "Tap to check in when you arrive at IronPulse Gym",
                            fontSize = 10.sp,
                            color = if (member.atGym) GymEmerald else TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (member.atGym) GymEmerald else GymSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (member.atGym) GymEmerald else GymCardBorder
                        ),
                        modifier = Modifier
                            .testTag("toggle_check_in_button")
                            .clickable { onToggleCheckIn() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (member.atGym) "Checked In ✓" else "I am at Gym",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (member.atGym) GymDarkBg else TextPrimary
                        )
                    }
                }
            }
        }

        // Goal Phase & Targets Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ACTIVE PROGRAM", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextMuted, letterSpacing = 1.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IntensityBadge(level = member.challengeLevel)
                            PhasePill(phase = member.goalPhase)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${member.currentWeight}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(" kg", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
                            }
                            Text("Target: ${member.targetWeight} kg", fontSize = 10.sp, color = GymCyan, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = GymAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${member.dailyCalories} kcal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = GymAmber
                                )
                            }
                            Text("Daily Energy Limit", fontSize = 9.sp, color = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Macro Split Summary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GymDarkBg, RoundedCornerShape(8.dp))
                            .padding(vertical = 6.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Protein: ${member.proteinG}g", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymRose)
                        Text("Carbs: ${member.carbsG}g", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymEmerald)
                        Text("Fat: ${member.fatG}g", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymCyan)
                    }
                }
            }
        }

        // Dynamic BMI Health Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GymSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Scale, contentDescription = null, tint = GymCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BODY MASS INDEX (BMI)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                        Text(
                            text = "Height: ${member.heightCm.toInt()} cm • Weight: ${member.currentWeight} kg",
                            fontSize = 10.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${bmiInfo.value}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = bmiInfo.color
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = bmiInfo.bgColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, bmiInfo.color.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = bmiInfo.category.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = bmiInfo.color,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Daily Weight Logger
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("LOG TODAY'S WEIGH-IN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("Instant biometric sync with progress tracking and BMI gauge.", fontSize = 9.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            placeholder = { Text("Enter weight in kg (e.g. 80.5)", fontSize = 11.sp, color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymCyan,
                                unfocusedBorderColor = GymCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f).testTag("quick_weight_input")
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GymCyan,
                            modifier = Modifier
                                .testTag("quick_log_weight_button")
                                .clickable {
                                    val w = weightInput.toDoubleOrNull()
                                    if (w != null && w > 20.0) {
                                        onLogWeight(w)
                                        weightInput = ""
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 13.dp)
                        ) {
                            Text("Log", fontSize = 11.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                        }
                    }
                }
            }
        }

        // Daily Protocol Completion Cards
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Diet Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GymSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDiet() }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("MEALS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = GymEmerald)
                            Text("$completedMeals / $totalMeals", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { mealProgress },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = GymEmerald,
                            trackColor = GymSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Daily Nutrition Plan", fontSize = 8.5.sp, color = TextMuted)
                    }
                }

                // Workout Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GymSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToWorkout() }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("WORKOUT", fontSize = 9.sp, fontWeight = FontWeight.Black, color = GymCyan)
                            Text("$completedWorkouts / $totalWorkouts", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { workoutProgress },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = GymCyan,
                            trackColor = GymSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Training Protocol", fontSize = 8.5.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}
