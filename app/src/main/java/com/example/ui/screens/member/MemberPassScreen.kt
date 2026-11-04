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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.GymIndigo
import com.example.ui.theme.GymRose
import com.example.ui.theme.GymSurface
import com.example.ui.theme.GymSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun MemberPassScreen(
    member: MemberEntity?,
    invoices: List<PaymentInvoiceEntity>,
    onRenew: (plan: String, amount: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    if (member == null) {
        Box(modifier = modifier.fillMaxSize().background(GymDarkBg), contentAlignment = Alignment.Center) {
            Text("Loading membership details...", color = TextSecondary)
        }
        return
    }

    val isExpired = member.subscriptionDaysLeft <= 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Digital Pass Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isExpired) GymRose.copy(alpha = 0.6f) else GymCyan.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    GymSurfaceVariant,
                                    if (isExpired) GymRose.copy(alpha = 0.15f) else GymCyan.copy(alpha = 0.12f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = GymCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("IRON PULSE ATHLETIC CLUB", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary, letterSpacing = 1.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isExpired) GymRose.copy(alpha = 0.2f) else GymEmerald.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isExpired) "EXPIRED" else "ACTIVE PASS",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isExpired) GymRose else GymEmerald,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(member.name, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("Member ID: ${member.id}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = GymCyan)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("TIER", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(member.membershipTier, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GymIndigo)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("VALIDITY", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(
                                text = if (isExpired) "Renew to Unlock" else "${member.subscriptionDaysLeft} Days Remaining",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (member.subscriptionDaysLeft <= 5) GymRose else GymEmerald,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Stylized Barcode Graphic
                        Column(horizontalAlignment = Alignment.End) {
                            Icon(Icons.Default.QrCode2, contentDescription = "Pass QR", tint = TextPrimary, modifier = Modifier.size(46.dp))
                            Text("TAP AT TURNSTILE", fontSize = 7.sp, fontWeight = FontWeight.Black, color = TextMuted)
                        }
                    }
                }
            }
        }

        // Perks & Privileges
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("MEMBERSHIP PRIVILEGES", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    PerkRow("Full 24/7 Access to Gym Floor & Free Weights Area")
                    PerkRow("Unlimited Automated Group Class Bookings (HIIT, Spin, CrossFit)")
                    PerkRow("Personalized Macro Nutrition & Workout Protocol Sync with Coach")
                    PerkRow("Digital Barcode Check-In & Live Attendance Logging")
                }
            }
        }

        // Renewal Actions Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("EXTEND MEMBERSHIP", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("Secure instant renewal and uninterrupted gym access.", fontSize = 9.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GymCyan,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("renew_monthly_button")
                                .clickable { onRenew("30-Day Monthly Pass", 49.0) }
                                .padding(vertical = 12.dp),
                            contentColor = GymDarkBg
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Monthly Pass", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                Text("$49.00 / 30 Days", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GymIndigo,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("renew_quarterly_button")
                                .clickable { onRenew("Quarterly Pro Pass", 129.0) }
                                .padding(vertical = 12.dp),
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Quarterly Pro", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                Text("$129.00 / 90 Days", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Payment History & Invoices
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MY INVOICES & RECEIPTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${invoices.size} Transactions",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        if (invoices.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GymSurface,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                ) {
                    Text("No billing invoices recorded yet.", color = TextMuted, fontSize = 10.sp, modifier = Modifier.padding(14.dp))
                }
            }
        } else {
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
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = GymCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(inv.planName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Invoice ${inv.id} • ${inv.date}", fontSize = 8.5.sp, color = TextMuted)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "$${String.format(Locale.US, "%.2f", inv.amount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = GymEmerald
                            )
                            Text(inv.status, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = GymEmerald)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerkRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GymEmerald, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 9.5.sp, color = TextSecondary)
    }
}
