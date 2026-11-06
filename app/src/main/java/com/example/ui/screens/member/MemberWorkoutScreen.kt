package com.example.ui.screens.member

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemberEntity
import com.example.data.model.WorkoutExerciseEntity
import com.example.ui.theme.GymAmber
import com.example.ui.theme.GymCardBorder
import com.example.ui.theme.GymCyan
import com.example.ui.theme.GymDarkBg
import com.example.ui.theme.GymEmerald
import com.example.ui.theme.GymIndigo
import com.example.ui.theme.GymSurface
import com.example.ui.theme.GymSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.example.data.model.WorkoutProgressLogEntity
import com.example.ui.components.WorkoutProgressTrackerComponent

import androidx.compose.material.icons.filled.Bolt
import com.example.ui.components.GlowingMuscleAnatomyView

@Composable
fun MemberWorkoutScreen(
    member: MemberEntity?,
    workouts: List<WorkoutExerciseEntity>,
    onToggleWorkout: (WorkoutExerciseEntity) -> Unit,
    progressLogs: List<WorkoutProgressLogEntity> = emptyList(),
    personalBests: List<WorkoutProgressLogEntity> = emptyList(),
    onOpenLogActivity: () -> Unit = {},
    onDeleteLog: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var workoutSubTab by remember { mutableIntStateOf(0) }
    val completedCount = workouts.count { it.isCompleted }
    val totalCount = workouts.size.coerceAtLeast(1)
    val progress = (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
    val allCompleted = workouts.isNotEmpty() && completedCount == workouts.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Workout Sub-Tab Row: Daily Checklist vs Glowing Anatomy vs PRs
        item {
            TabRow(
                selectedTabIndex = workoutSubTab,
                containerColor = GymSurface,
                contentColor = GymCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[workoutSubTab]),
                        color = when (workoutSubTab) {
                            0 -> GymCyan
                            1 -> GymEmerald
                            else -> GymAmber
                        },
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = workoutSubTab == 0,
                    onClick = { workoutSubTab = 0 },
                    modifier = Modifier.testTag("tab_workout_checklist"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ROUTINE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = workoutSubTab == 1,
                    onClick = { workoutSubTab = 1 },
                    modifier = Modifier.testTag("tab_workout_anatomy"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(13.dp), tint = GymEmerald)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ANATOMY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = workoutSubTab == 2,
                    onClick = { workoutSubTab = 2 },
                    modifier = Modifier.testTag("tab_workout_prs"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(13.dp), tint = GymAmber)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PRS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }

        if (workoutSubTab == 1) {
            item {
                GlowingMuscleAnatomyView(
                    workouts = workouts,
                    onToggleWorkout = onToggleWorkout
                )
            }
        } else if (workoutSubTab == 2) {
            item {
                WorkoutProgressTrackerComponent(
                    progressLogs = progressLogs,
                    personalBests = personalBests,
                    onOpenLogActivity = onOpenLogActivity,
                    onDeleteLog = onDeleteLog
                )
            }
        } else {
        // Workout Header Progress Card
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
                            Text("TODAY'S TRAINING PROTOCOL", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                        Text(
                            text = "${member?.challengeLevel ?: "Intermediate"}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GymIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("COMPLETION STATUS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("$completedCount of ${workouts.size} exercises (${(progress * 100).toInt()}%)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymCyan)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(5.dp),
                        color = GymCyan,
                        trackColor = GymSurfaceVariant
                    )

                    if (allCompleted) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GymEmerald.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GymEmerald.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GymEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Workout Complete! Fantastic work hitting today's targets.", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GymEmerald)
                            }
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "EXERCISE ROUTINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
        }

        if (workouts.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GymSurface,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Text("No exercises prescribed yet for today.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(16.dp))
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
                        if (ex.isCompleted) GymCyan.copy(alpha = 0.35f) else GymCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workout_item_${ex.id}")
                        .clickable { onToggleWorkout(ex) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = ex.isCompleted,
                            onCheckedChange = { onToggleWorkout(ex) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = GymCyan,
                                uncheckedColor = TextMuted,
                                checkmarkColor = GymDarkBg
                            )
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ex.exercise,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ex.isCompleted) TextMuted else TextPrimary,
                                    textDecoration = if (ex.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GymIndigo.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = ex.weight,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = GymIndigo,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${ex.sets} sets × ${ex.reps} reps",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (ex.isCompleted) TextMuted else GymCyan
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (ex.isCompleted) GymEmerald.copy(alpha = 0.2f) else GymCyan.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (ex.isCompleted) GymEmerald.copy(alpha = 0.4f) else GymCyan.copy(alpha = 0.3f))
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
                        }
                    }
                }
            }
        }
    }
}
}
