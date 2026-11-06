package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun WaterTrackerComponent(
    currentMl: Int,
    targetMl: Int = 3000,
    onAddWater: (Int) -> Unit,
    onResetWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (currentMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()
    val glasses = currentMl / 250
    val targetGlasses = targetMl / 250

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GymSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("water_tracker_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
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
                            .background(GymCyan.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = "Water Tracker",
                            tint = GymCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "HYDRATION & WATER TRACKER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "$glasses of $targetGlasses glasses (250ml each)",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (percentage >= 100) GymEmerald.copy(alpha = 0.18f) else GymCyan.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (percentage >= 100) GymEmerald.copy(alpha = 0.4f) else GymCyan.copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = if (percentage >= 100) "GOAL REACHED! 🎉" else "$percentage%",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = if (percentage >= 100) GymEmerald else GymCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gauge numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$currentMl",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary
                    )
                    Text(
                        text = " / $targetMl ml",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                Text(
                    text = if (currentMl >= targetMl) "Optimal hydration" else "${targetMl - currentMl} ml remaining",
                    fontSize = 10.sp,
                    color = if (currentMl >= targetMl) GymEmerald else TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (percentage >= 100) GymEmerald else GymCyan,
                trackColor = GymSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Add Hydration Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(250, 500, 750).forEach { amount ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GymSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("water_add_${amount}ml")
                            .clickable { onAddWater(amount) }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "+$amount ml",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = GymCyan,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                // Reset Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GymRose.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymRose.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .testTag("water_reset_button")
                        .clickable { onResetWater() }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset",
                            tint = GymRose,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Reset",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = GymRose
                        )
                    }
                }
            }
        }
    }
}
