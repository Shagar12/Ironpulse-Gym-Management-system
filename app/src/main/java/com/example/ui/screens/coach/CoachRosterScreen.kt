package com.example.ui.screens.coach

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.MemberEntity
import com.example.ui.CoachTab
import com.example.ui.UserRole
import com.example.ui.components.BMIBadge
import com.example.ui.components.IntensityBadge
import com.example.ui.components.PhasePill
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
fun CoachRosterScreen(
    members: List<MemberEntity>,
    selectedMemberId: String,
    searchQuery: String,
    filterOption: String,
    onSearchChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onSelectMember: (String) -> Unit,
    onApproveMember: (String, Boolean) -> Unit = { _, _ -> },
    onApproveAsRole: (memberId: String, role: String) -> Unit = { _, _ -> },
    onRejectApplication: (memberId: String) -> Unit = {},
    onOpenAddMember: () -> Unit,
    onOpenAiGen: () -> Unit,
    onRegenerateSchedule: () -> Unit,
    onNavigateToProtocol: () -> Unit,
    onUpdateRole: (memberId: String, newRole: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val atGymCount = members.count { it.atGym }
    val pendingMembers = members.filter { !it.approved }
    val pendingCount = pendingMembers.size
    val expiringCount = members.count { it.subscriptionDaysLeft in 1..5 }

    // Filter members
    val filteredMembers = members.filter { m ->
        val matchesSearch = m.name.contains(searchQuery, ignoreCase = true) ||
                m.email.contains(searchQuery, ignoreCase = true) ||
                m.goalPhase.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (filterOption) {
            "AT_GYM" -> m.atGym
            "PENDING" -> !m.approved
            "EXPIRING" -> m.subscriptionDaysLeft <= 5
            else -> true
        }
        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    title = "ROSTER",
                    value = "${members.size}",
                    subtitle = "Athletes",
                    accent = GymCyan,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "AT GYM",
                    value = "$atGymCount",
                    subtitle = "Checked In",
                    accent = GymEmerald,
                    showPulse = atGymCount > 0,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "DUE SOON",
                    value = "$expiringCount",
                    subtitle = "≤ 5 Days",
                    accent = if (expiringCount > 0) GymRose else GymAmber,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "PENDING",
                    value = "$pendingCount",
                    subtitle = "Review",
                    accent = if (pendingCount > 0) GymAmber else TextMuted,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Toolbar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ATHLETE DIRECTORY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GymIndigo.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GymIndigo.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .testTag("open_ai_gen_button")
                            .clickable { onOpenAiGen() }
                            .padding(horizontal = 9.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GymIndigo, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI Gen", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymIndigo)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GymCyan,
                        modifier = Modifier
                            .testTag("add_athlete_button")
                            .clickable { onOpenAddMember() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = GymDarkBg, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Add", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by name, email, or goal phase...", fontSize = 11.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GymCyan,
                    unfocusedBorderColor = GymCardBorder,
                    focusedContainerColor = GymSurface,
                    unfocusedContainerColor = GymSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("roster_search_input")
            )
        }

        // Filter Chips Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val chips = listOf(
                    "ALL" to "All (${members.size})",
                    "AT_GYM" to "At Gym ($atGymCount)",
                    "EXPIRING" to "Expiring Soon ($expiringCount)",
                    "PENDING" to "Pending ($pendingCount)"
                )
                items(chips) { (key, label) ->
                    val isSelected = filterOption == key
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) GymCyan.copy(alpha = 0.2f) else GymSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GymCyan else GymCardBorder
                        ),
                        modifier = Modifier
                            .clickable { onFilterChange(key) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) GymCyan else TextSecondary
                        )
                    }
                }
            }
        }

        // Pending Signups Dedicated Review Section
        if (pendingMembers.isNotEmpty() && (filterOption == "ALL" || filterOption == "PENDING")) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GymAmber.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymAmber.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pending_signups_section")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
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
                                        .background(GymAmber.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = GymAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "NEW SIGNUPS AWAITING APPROVAL",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.8.sp,
                                        color = GymAmber
                                    )
                                    Text(
                                        text = "Coach must review and assign Coach or Member role",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GymAmber,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${pendingMembers.size} PENDING",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GymDarkBg
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        pendingMembers.forEach { applicant ->
                            PendingApplicantReviewCard(
                                applicant = applicant,
                                onApproveAsMember = { onApproveAsRole(applicant.id, "MEMBER") },
                                onApproveAsCoach = { onApproveAsRole(applicant.id, "COACH") },
                                onReject = { onRejectApplication(applicant.id) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Member Cards List
        if (filteredMembers.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = GymSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No athletes match this criteria", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Try adjusting your search query or filter chip.", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        } else {
            items(filteredMembers, key = { it.id }) { member ->
                val isSelected = member.id == selectedMemberId
                MemberRosterCard(
                    member = member,
                    isSelected = isSelected,
                    onClick = {
                        onSelectMember(member.id)
                        onNavigateToProtocol()
                    },
                    onApproveAsMember = { onApproveAsRole(member.id, "MEMBER") },
                    onApproveAsCoach = { onApproveAsRole(member.id, "COACH") },
                    onReject = { onRejectApplication(member.id) },
                    onToggleApproval = { approved -> onApproveMember(member.id, approved) },
                    onUpdateRole = onUpdateRole
                )
            }
        }
    }
}

@Composable
fun PendingApplicantReviewCard(
    applicant: MemberEntity,
    onApproveAsMember: () -> Unit,
    onApproveAsCoach: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GymSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GymAmber.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("applicant_review_card_${applicant.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = applicant.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        PhasePill(phase = applicant.goalPhase)
                    }
                    Text(
                        text = "${applicant.email} • ${applicant.gender} • ${applicant.age}y",
                        fontSize = 10.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GymRose.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymRose.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .testTag("btn_applicant_reject_${applicant.id}")
                        .clickable { onReject() }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = GymRose, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Decline", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymRose)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Applicant Biometrics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GymDarkBg.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(vertical = 5.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricColumn(title = "WEIGHT", value = "${applicant.currentWeight}kg", color = TextPrimary)
                MetricColumn(title = "TARGET", value = "${applicant.targetWeight}kg", color = GymCyan)
                MetricColumn(title = "HEIGHT", value = "${applicant.heightCm.toInt()}cm", color = TextSecondary)
                MetricColumn(title = "LEVEL", value = applicant.challengeLevel, color = GymAmber)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Direct Decision Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GymEmerald,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_approve_applicant_member_${applicant.id}")
                        .clickable { onApproveAsMember() }
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Approve as Member 🏋️", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GymCyan,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_approve_applicant_coach_${applicant.id}")
                        .clickable { onApproveAsCoach() }
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Approve as Coach 🛡️", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                    }
                }
            }
        }
    }
}

@Composable
fun MemberRosterCard(
    member: MemberEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onApproveAsMember: () -> Unit,
    onApproveAsCoach: () -> Unit,
    onReject: () -> Unit,
    onToggleApproval: (Boolean) -> Unit,
    onUpdateRole: (memberId: String, newRole: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) GymSurfaceVariant else GymSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) GymCyan.copy(alpha = 0.6f) else GymCardBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("member_card_${member.id}")
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Name, email, badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = member.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (member.atGym) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(GymEmerald)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IntensityBadge(level = member.challengeLevel)
                    }
                    Text(
                        text = "${member.email} • ${member.gender} • ${member.age}y",
                        fontSize = 10.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (member.atGym) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GymEmerald.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GymEmerald.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = GymEmerald, modifier = Modifier.size(10.dp))
                                    Text("AT GYM", fontSize = 8.sp, fontWeight = FontWeight.Black, color = GymEmerald)
                                }
                            }
                        }
                        PhasePill(phase = member.goalPhase)
                    }
                    BMIBadge(weight = member.currentWeight, height = member.heightCm)
                }
            }

            // Pending Approval Banner with Direct Role Decision
            if (!member.approved) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GymAmber.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymAmber.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth().testTag("pending_approval_card_${member.id}")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = GymAmber, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Awaiting Coach Decision", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = GymAmber)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GymRose.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GymRose.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .testTag("btn_decline_${member.id}")
                                    .clickable { onReject() }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = GymRose, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Decline", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymRose)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Assign role to approve and grant live access:",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GymEmerald,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_approve_member_${member.id}")
                                    .clickable { onApproveAsMember() }
                                    .padding(vertical = 7.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Approve as Member 🏋️", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GymCyan,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_approve_coach_${member.id}")
                                    .clickable { onApproveAsCoach() }
                                    .padding(vertical = 7.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Approve as Coach 🛡️", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                                }
                            }
                        }
                    }
                }
            }

            // Metrics Grid
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GymDarkBg.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(vertical = 7.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricColumn(title = "CURRENT", value = "${member.currentWeight}kg", color = TextPrimary)
                MetricColumn(title = "TARGET", value = "${member.targetWeight}kg", color = GymCyan)
                MetricColumn(title = "CALORIES", value = "${member.dailyCalories}k", color = GymAmber)
                MetricColumn(
                    title = "BILLING",
                    value = "${member.subscriptionDaysLeft}d left",
                    color = if (member.subscriptionDaysLeft <= 5) GymRose else GymEmerald
                )
            }

            // Role Management & Quick Actions
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Role indicator tag
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (member.role == "COACH") GymCyan.copy(alpha = 0.15f) else GymEmerald.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (member.role == "COACH") GymCyan.copy(alpha = 0.4f) else GymEmerald.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = if (member.role == "COACH") "STAFF • COACH" else "ATHLETE • MEMBER",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (member.role == "COACH") GymCyan else GymEmerald,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Revoke approval / put on hold button for approved members
                    if (member.approved && member.email != "coach@fitgym.com") {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GymSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                            modifier = Modifier
                                .testTag("btn_revoke_${member.id}")
                                .clickable { onToggleApproval(false) }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Put on Hold ⏸",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }
                    }

                    // Promote / Demote Button (Head coach coach@fitgym.com cannot be demoted)
                    if (member.email != "coach@fitgym.com") {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (member.role == "COACH") GymAmber.copy(alpha = 0.15f) else GymCyan.copy(alpha = 0.18f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (member.role == "COACH") GymAmber.copy(alpha = 0.5f) else GymCyan.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .testTag("btn_role_toggle_${member.id}")
                                .clickable {
                                    val nextRole = if (member.role == "COACH") "MEMBER" else "COACH"
                                    onUpdateRole(member.id, nextRole)
                                }
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (member.role == "COACH") "Revert to Member 👤" else "Promote to Coach 🛡️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (member.role == "COACH") GymAmber else GymCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricColumn(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = TextMuted, letterSpacing = 0.5.sp)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Black, color = color, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    showPulse: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GymSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = TextMuted)
                if (showPulse) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(GymEmerald)
                    )
                }
            }
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = accent,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(vertical = 1.dp)
            )
            Text(subtitle, fontSize = 8.sp, color = TextSecondary)
        }
    }
}
