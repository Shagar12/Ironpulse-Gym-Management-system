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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Restaurant
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealEntity
import com.example.data.model.MemberEntity
import com.example.ui.theme.GymAmber
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
fun MemberDietScreen(
    member: MemberEntity?,
    meals: List<MealEntity>,
    onToggleMeal: (MealEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = meals.count { it.isCompleted }
    val totalCount = meals.size.coerceAtLeast(1)
    val progress = (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Macro Targets Card
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
                            Text("DAILY NUTRITION PROTOCOL", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                        Text(
                            text = "${member?.dailyCalories ?: 2100} kcal",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = GymAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MacroDisplayBox(label = "PROTEIN", amount = "${member?.proteinG ?: 180}g", color = GymRose, modifier = Modifier.weight(1f))
                        MacroDisplayBox(label = "CARBS", amount = "${member?.carbsG ?: 190}g", color = GymEmerald, modifier = Modifier.weight(1f))
                        MacroDisplayBox(label = "FATS", amount = "${member?.fatG ?: 60}g", color = GymCyan, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("MEALS COMPLETED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("$completedCount of ${meals.size} done (${(progress * 100).toInt()}%)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymEmerald)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(5.dp),
                        color = GymEmerald,
                        trackColor = GymSurfaceVariant
                    )
                }
            }
        }

        // Meal Checklist Items
        item {
            Text(
                text = "MEAL SCHEDULE & LOG",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
        }

        if (meals.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GymSurface,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Text("No meals prescribed by coach yet.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            items(meals, key = { it.id }) { meal ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (meal.isCompleted) GymEmerald.copy(alpha = 0.08f) else GymSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (meal.isCompleted) GymEmerald.copy(alpha = 0.35f) else GymCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("meal_item_${meal.id}")
                        .clickable { onToggleMeal(meal) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = meal.isCompleted,
                            onCheckedChange = { onToggleMeal(meal) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = GymEmerald,
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
                                    text = meal.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (meal.isCompleted) TextMuted else TextPrimary,
                                    textDecoration = if (meal.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GymCyan.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = meal.time,
                                        fontSize = 8.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = GymCyan,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = meal.description,
                                fontSize = 10.sp,
                                color = if (meal.isCompleted) TextMuted.copy(alpha = 0.7f) else TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroDisplayBox(label: String, amount: String, color: Color, modifier: Modifier = Modifier) {
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
            Text(label, fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = TextMuted)
            Text(amount, fontSize = 12.sp, fontWeight = FontWeight.Black, color = color, fontFamily = FontFamily.Monospace)
        }
    }
}
