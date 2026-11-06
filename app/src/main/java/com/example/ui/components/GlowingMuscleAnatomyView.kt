package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class MuscleZoneInfo(
    val id: String,
    val name: String,
    val category: String, // Upper Body, Torso, Lower Body
    val iconDescription: String
)

val STANDARD_MUSCLE_ZONES = listOf(
    MuscleZoneInfo("chest", "Pectorals (Chest)", "Upper Body", "Incline Press, Pushups"),
    MuscleZoneInfo("shoulders", "Deltoids (Shoulders)", "Upper Body", "Overhead Press, Lateral Raises"),
    MuscleZoneInfo("biceps", "Biceps (Arms)", "Upper Body", "Barbell Curls, Hammer Curls"),
    MuscleZoneInfo("triceps", "Triceps (Arms)", "Upper Body", "Dips, Cable Pushdowns"),
    MuscleZoneInfo("back", "Lats & Traps (Back)", "Torso", "Deadlifts, Lat Pulldowns, Rows"),
    MuscleZoneInfo("core", "Abdominals (Core)", "Torso", "Planks, Hanging Leg Raises"),
    MuscleZoneInfo("quads", "Quadriceps (Legs)", "Lower Body", "Squats, Leg Press, Lunges"),
    MuscleZoneInfo("glutes", "Glutes & Hamstrings", "Lower Body", "Romanian Deadlifts, Hip Thrusts"),
    MuscleZoneInfo("calves", "Calves (Lower Leg)", "Lower Body", "Standing Calf Raises")
)

@Composable
fun GlowingMuscleAnatomyView(
    workouts: List<WorkoutExerciseEntity>,
    onToggleWorkout: ((WorkoutExerciseEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Determine which muscle groups have completed exercises
    val activeMuscles = workouts.filter { it.isCompleted }
        .map { it.muscleGroup.lowercase().trim() }
        .toSet()

    val totalZones = STANDARD_MUSCLE_ZONES.size
    val activeCount = STANDARD_MUSCLE_ZONES.count { activeMuscles.contains(it.id) }
    val activationPercentage = (activeCount.toFloat() / totalZones.toFloat()).coerceIn(0f, 1f)

    val infiniteTransition = rememberInfiniteTransition(label = "neon_glow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GymSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (activeCount > 0) GymCyan.copy(alpha = 0.5f) else GymCardBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("glowing_muscle_anatomy_view")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeCount > 0) GymCyan.copy(alpha = 0.2f) else GymSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (activeCount > 0) GymCyan else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "GLOWING MUSCLE ANATOMY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (activeCount > 0) "Real-time stimulus tracker" else "Complete exercises to light up muscles",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeCount > 0) GymEmerald.copy(alpha = 0.15f) else GymSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (activeCount > 0) GymEmerald.copy(alpha = 0.4f) else GymCardBorder
                    )
                ) {
                    Text(
                        text = "$activeCount / $totalZones ACTIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = if (activeCount > 0) GymEmerald else TextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Activation Progress Bar
            LinearProgressIndicator(
                progress = { activationPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GymCyan,
                trackColor = GymSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Body Zones Visualizer Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                STANDARD_MUSCLE_ZONES.forEach { zone ->
                    val isActive = activeMuscles.contains(zone.id)
                    val exercisesForZone = workouts.filter { it.muscleGroup.lowercase().trim() == zone.id }
                    val completedExercises = exercisesForZone.filter { it.isCompleted }

                    val animatedBorderColor by animateColorAsState(
                        targetValue = if (isActive) GymCyan.copy(alpha = pulseAlpha) else GymCardBorder,
                        label = "border_color"
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isActive) GymCyan.copy(alpha = 0.08f) else GymDarkBg.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isActive) 1.5.dp else 1.dp,
                            animatedBorderColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("muscle_zone_${zone.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Glowing Pulse Indicator Dot
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isActive) GymCyan.copy(alpha = pulseAlpha) else TextMuted.copy(alpha = 0.3f)
                                        )
                                        .then(
                                            if (isActive) {
                                                Modifier.border(1.dp, GymCyan, CircleShape)
                                            } else Modifier
                                        )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = zone.name,
                                            fontSize = 11.sp,
                                            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isActive) TextPrimary else TextSecondary
                                        )
                                        if (isActive) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "🔥 ACTIVE",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = GymCyan
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (exercisesForZone.isNotEmpty()) {
                                            "${completedExercises.size} / ${exercisesForZone.size} exercises complete (${exercisesForZone.joinToString { it.exercise }})"
                                        } else {
                                            zone.category
                                        },
                                        fontSize = 8.5.sp,
                                        color = if (isActive) GymEmerald else TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }

                            if (isActive) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GymCyan.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCyan.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "STIMULATED",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GymCyan,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = "Resting",
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
