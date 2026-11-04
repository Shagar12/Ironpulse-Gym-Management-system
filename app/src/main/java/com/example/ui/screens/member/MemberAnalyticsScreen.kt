package com.example.ui.screens.member

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemberEntity
import com.example.data.model.WeightLogEntity
import com.example.ui.components.BMIBadge
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
import java.util.Locale

@Composable
fun MemberAnalyticsScreen(
    member: MemberEntity?,
    weightLogs: List<WeightLogEntity>,
    modifier: Modifier = Modifier
) {
    var chartViewType by remember { mutableStateOf("bar") } // "bar" or "pie"

    if (member == null) {
        Box(modifier = modifier.fillMaxSize().background(GymDarkBg), contentAlignment = Alignment.Center) {
            Text("No athlete profile loaded.", color = TextSecondary)
        }
        return
    }

    val bmiInfo = calculateBMI(member.currentWeight, member.heightCm)
    val weightDelta = member.currentWeight - member.targetWeight

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Header & View Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShowChart, contentDescription = null, tint = GymCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("30-DAY PROGRESS ANALYTICS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GymSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (chartViewType == "bar") GymCyan else Color.Transparent,
                            modifier = Modifier
                                .testTag("toggle_bar_chart")
                                .clickable { chartViewType = "bar" }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Bar Trend",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chartViewType == "bar") GymDarkBg else TextSecondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (chartViewType == "pie") GymCyan else Color.Transparent,
                            modifier = Modifier
                                .testTag("toggle_pie_chart")
                                .clickable { chartViewType = "pie" }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Macro Pie",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chartViewType == "pie") GymDarkBg else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Main Visual Card: Bar Chart or Macro Donut
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (chartViewType == "bar") {
                        Text("WEIGHT TREND OVER TIME (KG)", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextMuted, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        if (weightLogs.isEmpty()) {
                            Text("No weight entries logged yet.", color = TextMuted, fontSize = 11.sp)
                        } else {
                            // Custom Canvas Bar Chart
                            val maxWeight = (weightLogs.maxOfOrNull { it.weight } ?: 90.0) + 5.0
                            val minWeight = ((weightLogs.minOfOrNull { it.weight } ?: 70.0) - 5.0).coerceAtLeast(0.0)
                            val range = (maxWeight - minWeight).coerceAtLeast(1.0)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .padding(horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                weightLogs.takeLast(6).forEach { log ->
                                    val fraction = ((log.weight - minWeight) / range).toFloat().coerceIn(0.15f, 1f)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = "${log.weight}",
                                            fontSize = 8.5.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = GymCyan
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height((110 * fraction).dp)
                                                .background(
                                                    Brush.verticalGradient(listOf(GymCyan, GymEmerald)),
                                                    RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = log.date,
                                            fontSize = 8.5.sp,
                                            color = TextMuted,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Donut Macro Breakdown
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("MACRONUTRIENT RATIO DISTRIBUTION", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextMuted, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp)) {
                                val totalGrams = (member.proteinG + member.carbsG + member.fatG).coerceAtLeast(1)
                                val protAngle = (member.proteinG.toFloat() / totalGrams) * 360f
                                val carbAngle = (member.carbsG.toFloat() / totalGrams) * 360f
                                val fatAngle = (member.fatG.toFloat() / totalGrams) * 360f

                                Canvas(modifier = Modifier.size(120.dp)) {
                                    val strokeWidth = 18.dp.toPx()
                                    // Protein arc
                                    drawArc(
                                        color = GymRose,
                                        startAngle = -90f,
                                        sweepAngle = protAngle,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                    )
                                    // Carbs arc
                                    drawArc(
                                        color = GymEmerald,
                                        startAngle = -90f + protAngle,
                                        sweepAngle = carbAngle,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                    )
                                    // Fat arc
                                    drawArc(
                                        color = GymCyan,
                                        startAngle = -90f + protAngle + carbAngle,
                                        sweepAngle = fatAngle,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${member.dailyCalories}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextPrimary
                                    )
                                    Text("kcal/day", fontSize = 8.sp, color = TextMuted)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                MacroLegendCard("Protein", "${member.proteinG}g", GymRose, Modifier.weight(1f))
                                MacroLegendCard("Carbs", "${member.carbsG}g", GymEmerald, Modifier.weight(1f))
                                MacroLegendCard("Fats", "${member.fatG}g", GymCyan, Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Body Composition Metrics Grid
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("BIOMETRIC COMPOSITION", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCard(
                            label = "CURRENT WEIGHT",
                            value = "${member.currentWeight} kg",
                            sub = "BMI ${bmiInfo.value}",
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "TARGET WEIGHT",
                            value = "${member.targetWeight} kg",
                            sub = if (weightDelta > 0) "-${String.format(Locale.US, "%.1f", weightDelta)} kg to go" else "Goal Achieved!",
                            color = GymCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCard(
                            label = "GOAL STRATEGY",
                            value = member.goalPhase,
                            sub = "Intensity: ${member.challengeLevel}",
                            color = GymEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "HEIGHT",
                            value = "${member.heightCm.toInt()} cm",
                            sub = "Category: ${bmiInfo.category}",
                            color = GymIndigo,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacroLegendCard(name: String, grams: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = GymSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(4.dp))
                Text(name, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }
            Text(grams, fontSize = 11.sp, fontWeight = FontWeight.Black, color = color, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, sub: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = GymSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = TextMuted)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color, modifier = Modifier.padding(vertical = 2.dp))
            Text(sub, fontSize = 8.5.sp, color = TextSecondary)
        }
    }
}
