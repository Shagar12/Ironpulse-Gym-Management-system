package com.example.ui.screens.classes

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GymClassEntity
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

@Composable
fun ClassScheduleScreen(
    userRole: UserRole,
    classes: List<GymClassEntity>,
    allMembers: List<MemberEntity>,
    activeMemberId: String,
    selectedDay: String,
    selectedCategory: String,
    onDaySelect: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onBookClass: (String) -> Unit,
    onCancelBooking: (String) -> Unit,
    onAutoBookRecommended: () -> Unit,
    onRegenerateAutomatedSchedule: () -> Unit,
    onOpenAddClass: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeMember = allMembers.find { it.id == activeMemberId }
    var showOnlyMyBookings by remember { mutableStateOf(false) }

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val categories = listOf("ALL", "HIIT", "Strength", "Spin", "Yoga", "CrossFit", "Boxing")

    // Filter classes
    val filteredClasses = classes.filter { cls ->
        val matchesDay = cls.dayOfWeek.equals(selectedDay, ignoreCase = true)
        val matchesCat = selectedCategory == "ALL" || cls.category.equals(selectedCategory, ignoreCase = true)
        val matchesMyBooking = if (showOnlyMyBookings) {
            val bookings = cls.bookedMemberIds.split(",").map { it.trim() }
            bookings.contains(activeMemberId)
        } else true

        matchesDay && matchesCat && matchesMyBooking
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Automation & Actions Header Bar
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = GymCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AUTOMATED TIMETABLE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                            Text(
                                text = if (userRole == UserRole.COACH)
                                    "Auto-optimizes instructors, capacity & slots"
                                else
                                    "Tailored to your ${activeMember?.goalPhase ?: "fitness"} phase",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        if (userRole == UserRole.COACH) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GymIndigo.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GymIndigo.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .testTag("regenerate_schedule_button")
                                        .clickable { onRegenerateAutomatedSchedule() }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, tint = GymIndigo, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Auto-Gen", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GymIndigo)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GymCyan,
                                    modifier = Modifier
                                        .testTag("add_class_dialog_button")
                                        .clickable { onOpenAddClass() }
                                        .padding(horizontal = 9.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = GymDarkBg, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Class", fontSize = 9.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                                    }
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GymIndigo.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GymIndigo.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .testTag("auto_book_button")
                                    .clickable { onAutoBookRecommended() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GymIndigo, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Smart Auto-Book", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymIndigo)
                                }
                            }
                        }
                    }

                    if (userRole == UserRole.MEMBER) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filtered for ${activeMember?.name ?: "Athlete"}",
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (showOnlyMyBookings) GymEmerald.copy(alpha = 0.2f) else GymSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (showOnlyMyBookings) GymEmerald else GymCardBorder),
                                modifier = Modifier
                                    .clickable { showOnlyMyBookings = !showOnlyMyBookings }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (showOnlyMyBookings) "✓ My Booked Sessions" else "Show My Bookings",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showOnlyMyBookings) GymEmerald else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Day of Week Tabs
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(daysOfWeek) { day ->
                    val isSelected = selectedDay.equals(day, ignoreCase = true)
                    val shortName = day.take(3).uppercase()
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) GymCyan else GymSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GymCyan else GymCardBorder),
                        modifier = Modifier
                            .testTag("day_tab_$day")
                            .clickable { onDaySelect(day) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = shortName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) GymDarkBg else TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) GymCyan.copy(alpha = 0.2f) else GymSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GymCyan else GymCardBorder),
                        modifier = Modifier
                            .clickable { onCategorySelect(cat) }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) GymCyan else TextSecondary
                        )
                    }
                }
            }
        }

        // Classes List
        if (filteredClasses.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
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
                        Text("No sessions scheduled for $selectedDay ($selectedCategory)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Switch days or tap 'Auto-Gen' to regenerate schedule.", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        } else {
            items(filteredClasses, key = { it.id }) { gymClass ->
                val bookings = if (gymClass.bookedMemberIds.isBlank()) emptyList() else gymClass.bookedMemberIds.split(",").map { it.trim() }
                val isBookedByActive = bookings.contains(activeMemberId)
                val isFull = bookings.size >= gymClass.capacity
                val isSmartMatch = activeMember != null && (gymClass.targetPhase == activeMember.goalPhase || gymClass.targetPhase == "All")

                ClassCard(
                    gymClass = gymClass,
                    bookedCount = bookings.size,
                    isBookedByActive = isBookedByActive,
                    isFull = isFull,
                    isSmartMatch = isSmartMatch,
                    userRole = userRole,
                    bookedMemberNames = bookings.mapNotNull { id -> allMembers.find { it.id == id }?.name },
                    onBook = { onBookClass(gymClass.id) },
                    onCancel = { onCancelBooking(gymClass.id) }
                )
            }
        }
    }
}

@Composable
fun ClassCard(
    gymClass: GymClassEntity,
    bookedCount: Int,
    isBookedByActive: Boolean,
    isFull: Boolean,
    isSmartMatch: Boolean,
    userRole: UserRole,
    bookedMemberNames: List<String>,
    onBook: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedAttendees by remember { mutableStateOf(false) }

    val catColor = when (gymClass.category) {
        "HIIT" -> GymRose
        "Strength" -> GymCyan
        "Spin" -> GymAmber
        "Yoga" -> GymEmerald
        "CrossFit" -> GymIndigo
        else -> GymCyan
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GymSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isBookedByActive) GymEmerald.copy(alpha = 0.7f)
            else if (isSmartMatch && userRole == UserRole.MEMBER) GymCyan.copy(alpha = 0.5f)
            else GymCardBorder
        ),
        modifier = modifier.fillMaxWidth().testTag("class_card_${gymClass.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Category, Time slot, Smart Match
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = catColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, catColor.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = gymClass.category.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = catColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = GymCyan, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${gymClass.timeSlot} (${gymClass.durationMinutes}m)",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                if (isSmartMatch && userRole == UserRole.MEMBER) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GymCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GymCyan.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GymCyan, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("SMART MATCH", fontSize = 8.sp, fontWeight = FontWeight.Black, color = GymCyan)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Class Title & Instructor
            Text(gymClass.title, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(gymClass.instructor, fontSize = 10.sp, color = TextSecondary)
                    Text(" • ", fontSize = 10.sp, color = TextMuted)
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(gymClass.room, fontSize = 10.sp, color = TextSecondary)
                }

                Text(
                    text = "${gymClass.intensity} • Target: ${gymClass.targetPhase}",
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Capacity Meter
            val progress = (bookedCount.toFloat() / gymClass.capacity.toFloat()).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Capacity: $bookedCount / ${gymClass.capacity} Spots Booked",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFull) GymRose else TextSecondary
                )
                if (isFull) {
                    Text("FULL", fontSize = 9.sp, fontWeight = FontWeight.Black, color = GymRose)
                } else {
                    Text("${gymClass.capacity - bookedCount} spots left", fontSize = 9.sp, color = GymEmerald, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(5.dp),
                color = if (isFull) GymRose else if (progress > 0.8f) GymAmber else GymCyan,
                trackColor = GymSurfaceVariant,
            )

            // Bottom Actions Row
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View Attendees button
                if (bookedCount > 0) {
                    Text(
                        text = if (expandedAttendees) "Hide Attendees ▲" else "View Attendees ($bookedCount) ▼",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymCyan,
                        modifier = Modifier.clickable { expandedAttendees = !expandedAttendees }
                    )
                } else {
                    Text("No bookings yet", fontSize = 9.sp, color = TextMuted)
                }

                // Booking Button for Members
                if (userRole == UserRole.MEMBER) {
                    if (isBookedByActive) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GymRose.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GymRose.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .testTag("cancel_booking_button_${gymClass.id}")
                                .clickable { onCancel() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Cancel Booking", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymRose)
                        }
                    } else if (isFull) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GymSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Class Full", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GymCyan,
                            modifier = Modifier
                                .testTag("book_class_button_${gymClass.id}")
                                .clickable { onBook() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Book Spot", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymDarkBg)
                        }
                    }
                }
            }

            // Expanded Attendees Roster
            if (expandedAttendees && bookedMemberNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GymSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("ENROLLED ATHLETES:", fontSize = 8.sp, fontWeight = FontWeight.Black, color = GymCyan)
                        bookedMemberNames.forEach { name ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(4.dp).background(GymEmerald, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(name, fontSize = 10.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
