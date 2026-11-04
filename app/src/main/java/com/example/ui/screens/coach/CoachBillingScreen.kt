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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemberEntity
import com.example.data.model.PaymentInvoiceEntity
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
import java.util.Locale

@Composable
fun CoachBillingScreen(
    members: List<MemberEntity>,
    invoices: List<PaymentInvoiceEntity>,
    onRenewSubscription: (memberId: String, planName: String, amount: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalRevenue = invoices.filter { it.status == "Paid" }.sumOf { it.amount }
    val activeMembersCount = members.count { it.subscriptionDaysLeft > 0 }
    val dueSoonCount = members.count { it.subscriptionDaysLeft in 1..5 }
    val expiredCount = members.count { it.subscriptionDaysLeft <= 0 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Summary Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "FINANCIAL DASHBOARD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BillingSummaryCard(
                        title = "TOTAL REVENUE",
                        value = "$${String.format(Locale.US, "%.0f", totalRevenue)}",
                        subtitle = "Collected YTD",
                        accent = GymEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    BillingSummaryCard(
                        title = "ACTIVE ATHLETES",
                        value = "$activeMembersCount",
                        subtitle = "Paying Accounts",
                        accent = GymCyan,
                        modifier = Modifier.weight(1f)
                    )
                    BillingSummaryCard(
                        title = "RENEWAL DUE",
                        value = "$dueSoonCount",
                        subtitle = "≤ 5 Days Left",
                        accent = if (dueSoonCount > 0) GymAmber else TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Active Membership Cycles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "30-DAY SUBSCRIPTION BILLING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${members.size} Accounts",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymCyan
                )
            }
        }

        items(members, key = { it.id }) { member ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (member.subscriptionDaysLeft <= 5) GymRose.copy(alpha = 0.5f) else GymCardBorder
                ),
                modifier = Modifier.fillMaxWidth().testTag("billing_card_${member.id}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(member.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GymSurfaceVariant
                            ) {
                                Text(
                                    text = member.membershipTier,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GymCyan,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val daysColor = when {
                                member.subscriptionDaysLeft <= 0 -> GymRose
                                member.subscriptionDaysLeft <= 5 -> GymAmber
                                else -> GymEmerald
                            }
                            Text(
                                text = if (member.subscriptionDaysLeft <= 0) "Subscription Expired" else "${member.subscriptionDaysLeft} days remaining",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = daysColor,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = " • Joined ${member.joinedDate}",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Renewal Action Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GymEmerald.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GymEmerald.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .testTag("renew_button_${member.id}")
                            .clickable { onRenewSubscription(member.id, "30-Day Monthly Pass", 49.0) }
                            .padding(horizontal = 9.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = GymEmerald, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Renew $49", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymEmerald)
                        }
                    }
                }
            }
        }

        // Recent Invoices Ledger
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PAYMENT INVOICES & AUDIT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${invoices.size} Invoices",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
            }
        }

        items(invoices, key = { it.id }) { inv ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GymSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GymCyan.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = GymCyan, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(inv.memberName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${inv.planName} • ${inv.date}", fontSize = 9.sp, color = TextMuted)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+$${String.format(Locale.US, "%.2f", inv.amount)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = GymEmerald
                        )
                        Text(
                            text = "${inv.status} (${inv.paymentMethod})",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (inv.status == "Paid") GymEmerald else GymAmber
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BillingSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GymSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = TextMuted)
            Text(
                value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = accent,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(vertical = 1.dp)
            )
            Text(subtitle, fontSize = 8.sp, color = TextSecondary)
        }
    }
}
