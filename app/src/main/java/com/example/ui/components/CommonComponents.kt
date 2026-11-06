package com.example.ui.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemberEntity
import com.example.ui.UserRole
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

data class BMIInfo(
    val value: Double,
    val category: String,
    val color: Color,
    val bgColor: Color
)

fun calculateBMI(weightKg: Double, heightCm: Double): BMIInfo {
    if (weightKg <= 0.0 || heightCm <= 0.0) {
        return BMIInfo(0.0, "Unknown", TextMuted, GymSurfaceVariant)
    }
    val heightM = heightCm / 100.0
    val bmi = String.format(Locale.US, "%.1f", weightKg / (heightM * heightM)).toDoubleOrNull() ?: 0.0
    return when {
        bmi < 18.5 -> BMIInfo(bmi, "Underweight", GymCyan, GymCyan.copy(alpha = 0.15f))
        bmi < 25.0 -> BMIInfo(bmi, "Normal", GymEmerald, GymEmerald.copy(alpha = 0.15f))
        bmi < 30.0 -> BMIInfo(bmi, "Overweight", GymAmber, GymAmber.copy(alpha = 0.15f))
        else -> BMIInfo(bmi, "Obese", GymRose, GymRose.copy(alpha = 0.15f))
    }
}

@Composable
fun BMIBadge(weight: Double, height: Double, modifier: Modifier = Modifier) {
    val info = calculateBMI(weight, height)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = info.bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, info.color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "BMI ${info.value}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = info.color,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "(${info.category})",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = info.color
            )
        }
    }
}

@Composable
fun PhasePill(phase: String, modifier: Modifier = Modifier) {
    val (color, bg) = when (phase) {
        "Cutting" -> GymRose to GymRose.copy(alpha = 0.15f)
        "Lean Bulking" -> GymCyan to GymCyan.copy(alpha = 0.15f)
        "Bulking" -> GymEmerald to GymEmerald.copy(alpha = 0.15f)
        else -> GymAmber to GymAmber.copy(alpha = 0.15f)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Text(
            text = phase.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun IntensityBadge(level: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GymIndigo.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GymIndigo.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Text(
            text = level,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = GymIndigo,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun IronPulseHeader(
    role: UserRole,
    activeMemberName: String,
    allMembers: List<MemberEntity>,
    onToggleRole: () -> Unit,
    onSelectActiveMember: (String) -> Unit,
    onOpenEditProfile: () -> Unit = {},
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    Surface(
        color = GymDarkBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(listOf(GymCyan, GymEmerald))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "IronPulse Logo",
                        tint = GymDarkBg,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "IRON PULSE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = if (role == UserRole.COACH) "COACH PORTAL" else activeMemberName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (role == UserRole.COACH) GymCyan else GymEmerald,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Quick Portal & Account Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Edit Profile Button (for current user/member)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GymSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                    modifier = Modifier
                        .testTag("header_edit_profile_button")
                        .clickable { onOpenEditProfile() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Edit Profile",
                            tint = GymCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Edit Profile",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))

                // Switch Role Toggle
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (role == UserRole.COACH) GymCyan.copy(alpha = 0.15f) else GymEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (role == UserRole.COACH) GymCyan.copy(alpha = 0.4f) else GymEmerald.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .testTag("role_toggle_button")
                        .clickable { onToggleRole() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (role == UserRole.COACH) Icons.Default.Shield else Icons.Default.Person,
                            contentDescription = "Current Role",
                            tint = if (role == UserRole.COACH) GymCyan else GymEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (role == UserRole.COACH) "Coach View" else "Athlete View",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (role == UserRole.COACH) GymCyan else GymEmerald
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch",
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Sign Out Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GymRose.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymRose.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .testTag("header_sign_out_button")
                        .clickable { onSignOut() }
                        .padding(horizontal = 7.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sign Out",
                            tint = GymRose,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}
