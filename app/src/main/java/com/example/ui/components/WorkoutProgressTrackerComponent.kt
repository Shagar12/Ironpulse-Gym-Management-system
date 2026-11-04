package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkoutProgressLogEntity
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
import java.util.Locale

@Composable
fun WorkoutProgressTrackerComponent(
    progressLogs: List<WorkoutProgressLogEntity>,
    personalBests: List<WorkoutProgressLogEntity>,
    onOpenLogActivity: () -> Unit,
    onDeleteLog: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedExerciseFilter by remember { mutableStateOf("ALL") }

    // Derive distinct exercise list from logs
    val distinctExercises = remember(progressLogs) {
        listOf("ALL") + progressLogs.map { it.exerciseName }.distinct()
    }

    // Group PRs by exercise: take highest weight for each exercise
    val highestPrs = remember(progressLogs) {
        progressLogs.groupBy { it.exerciseName }
            .mapValues { entry -> entry.value.maxByOrNull { it.weightKg }!! }
            .values
            .sortedByDescending { it.weightKg }
    }

    val filteredLogs = remember(progressLogs, selectedExerciseFilter) {
        if (selectedExerciseFilter == "ALL") progressLogs
        else progressLogs.filter { it.exerciseName.equals(selectedExerciseFilter, ignoreCase = true) }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {

        // Header & Quick Action Row
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GymSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = GymCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WORKOUT PROGRESS & PRS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    }
                    Text("Track weight lifted, 1-Rep Max & personal records", fontSize = 9.5.sp, color = TextMuted)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GymCyan,
                    modifier = Modifier
                        .testTag("open_log_activity_button")
                        .clickable { onOpenLogActivity() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = GymDarkBg, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Log Lift", fontSize = 11.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                    }
                }
            }
        }

        // Personal Bests Trophy Rack
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GymAmber, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PERSONAL BESTS (PRs)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "${highestPrs.size} Records Set",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymAmber
                )
            }

            if (highestPrs.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GymSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No personal bests recorded yet. Tap 'Log Lift' to record your first PR!",
                        color = TextMuted,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(highestPrs) { pr ->
                        PersonalBestCard(pr = pr)
                    }
                }
            }
        }

        // Exercise Filter Chips
        if (distinctExercises.size > 2) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(distinctExercises) { exercise ->
                    val isSelected = selectedExerciseFilter == exercise
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) GymCyan.copy(alpha = 0.2f) else GymSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GymCyan else GymCardBorder),
                        modifier = Modifier
                            .clickable { selectedExerciseFilter = exercise }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (exercise == "ALL") "All Lifts" else exercise,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) GymCyan else TextSecondary
                        )
                    }
                }
            }
        }

        // Activity Log History
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = GymCyan, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ACTIVITY HISTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "${filteredLogs.size} Sessions",
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                )
            }

            if (filteredLogs.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GymSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No workout activities logged for this filter.",
                        color = TextMuted,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    filteredLogs.forEach { log ->
                        ActivityLogItem(log = log, onDelete = { onDeleteLog(log.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalBestCard(pr: WorkoutProgressLogEntity, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GymSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.linearGradient(listOf(GymAmber, GymRose))
        ),
        modifier = modifier.width(180.dp).testTag("pr_card_${pr.exerciseName}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GymAmber.copy(alpha = 0.2f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = GymAmber, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("PR RECORD", fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = GymAmber)
                    }
                }
                Text(pr.date, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(pr.exerciseName, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${pr.weightKg}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = GymCyan
                )
                Text(" kg", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.padding(bottom = 2.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "× ${pr.reps} reps",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = GymSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Est. 1RM", fontSize = 8.sp, color = TextMuted)
                    Text("${pr.estimated1RM} kg", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymEmerald, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(
    log: WorkoutProgressLogEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GymSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (log.isPersonalBest) GymAmber.copy(alpha = 0.5f) else GymCardBorder
        ),
        modifier = modifier.fillMaxWidth().testTag("activity_log_${log.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (log.isPersonalBest) GymAmber.copy(alpha = 0.15f) else GymCyan.copy(alpha = 0.12f),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (log.isPersonalBest) Icons.Default.LocalFireDepartment else Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = if (log.isPersonalBest) GymAmber else GymCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(log.exerciseName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        if (log.isPersonalBest) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GymAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "PR!",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GymAmber,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(log.date, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${log.weightKg} kg",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = GymCyan
                        )
                        Text(
                            text = " × ${log.reps} reps",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextSecondary
                        )
                        if (log.sets > 1) {
                            Text(
                                text = " (${log.sets} sets)",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Text(
                        text = "1RM ~ ${log.estimated1RM} kg",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymEmerald,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (log.notes.isNotBlank()) {
                    Text(
                        text = "\"${log.notes}\"",
                        fontSize = 8.5.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).padding(start = 4.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Log", tint = TextMuted, modifier = Modifier.size(14.dp))
            }
        }
    }
}
