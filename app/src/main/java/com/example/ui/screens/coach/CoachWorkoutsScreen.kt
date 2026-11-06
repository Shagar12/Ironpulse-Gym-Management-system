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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemberEntity
import com.example.data.model.WorkoutExerciseEntity
import com.example.ui.components.GlowingMuscleAnatomyView
import com.example.ui.theme.GymCardBorder
import com.example.ui.theme.GymCyan
import com.example.ui.theme.GymDarkBg
import com.example.ui.theme.GymEmerald
import com.example.ui.theme.GymRose
import com.example.ui.theme.GymSurface
import com.example.ui.theme.GymSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CoachWorkoutsScreen(
    members: List<MemberEntity>,
    selectedMember: MemberEntity?,
    workouts: List<WorkoutExerciseEntity>,
    onSelectMember: (String) -> Unit,
    onOpenAssignWorkout: () -> Unit,
    onDeleteWorkout: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var athleteMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Athlete Selector Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SELECT ATHLETE FOR WORKOUT ASSIGNMENT", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = GymCyan, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GymSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                                modifier = Modifier
                                    .testTag("coach_athlete_selector")
                                    .clickable { athleteMenuExpanded = true }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = GymCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = selectedMember?.name ?: "Select Athlete",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("▼", fontSize = 9.sp, color = TextMuted)
                                }
                            }

                            DropdownMenu(
                                expanded = athleteMenuExpanded,
                                onDismissRequest = { athleteMenuExpanded = false },
                                modifier = Modifier.background(GymSurface)
                            ) {
                                members.forEach { m ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(m.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Text("${m.goalPhase} • ${m.challengeLevel}", fontSize = 10.sp, color = TextMuted)
                                            }
                                        },
                                        onClick = {
                                            onSelectMember(m.id)
                                            athleteMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GymEmerald,
                            modifier = Modifier
                                .testTag("coach_assign_workout_btn")
                                .clickable { onOpenAssignWorkout() }
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = GymDarkBg, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Assign Workout", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                            }
                        }
                    }
                }
            }
        }

        // Glowing Muscle Anatomy Component
        item {
            GlowingMuscleAnatomyView(
                workouts = workouts
            )
        }

        // Assigned Exercises List
        item {
            Text("ASSIGNED WORKOUT EXERCISES FOR TODAY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary, letterSpacing = 0.5.sp)
        }

        if (workouts.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GymSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No workouts assigned for this athlete today. Tap 'Assign Workout' to prescribe exercises.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(workouts, key = { it.id }) { ex ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (ex.isCompleted) GymCyan.copy(alpha = 0.08f) else GymSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (ex.isCompleted) GymCyan.copy(alpha = 0.4f) else GymCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(ex.exercise, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (ex.isCompleted) GymEmerald.copy(alpha = 0.2f) else GymCyan.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (ex.isCompleted) "✓ ${ex.muscleGroup.uppercase()}" else ex.muscleGroup.uppercase(),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (ex.isCompleted) GymEmerald else GymCyan,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${ex.sets} sets × ${ex.reps} reps • ${ex.weight}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        IconButton(onClick = { onDeleteWorkout(ex.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GymRose, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
