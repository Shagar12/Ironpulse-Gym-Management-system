package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GymAmber
import com.example.ui.theme.GymCardBorder
import com.example.ui.theme.GymCyan
import com.example.ui.theme.GymDarkBg
import com.example.ui.theme.GymEmerald
import com.example.ui.theme.GymRose
import com.example.ui.theme.GymSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    errorMessage: String?,
    successMessage: String?,
    onSignIn: (email: String, password: String) -> Unit,
    onSignUp: (
        name: String,
        email: String,
        password: String,
        role: String,
        gender: String,
        age: Int,
        weight: Double,
        height: Double,
        goalPhase: String,
        challengeLevel: String
    ) -> Unit,
    onClearMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Sign In states
    var signInEmail by remember { mutableStateOf("coach@fitgym.com") }
    var signInPassword by remember { mutableStateOf("123456") }
    var showSignInPassword by remember { mutableStateOf(false) }

    // Sign Up states
    var signUpName by remember { mutableStateOf("") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var showSignUpPassword by remember { mutableStateOf(false) }
    var signUpRole by remember { mutableStateOf("MEMBER") } // "MEMBER" or "COACH"
    var signUpGender by remember { mutableStateOf("Male") }
    var signUpAge by remember { mutableStateOf("26") }
    var signUpWeight by remember { mutableStateOf("75.0") }
    var signUpHeight by remember { mutableStateOf("178.0") }
    var signUpGoalPhase by remember { mutableStateOf("Cutting") }
    var signUpLevel by remember { mutableStateOf("Intermediate") }

    val scrollState = rememberScrollState()

    // If signup succeeded, switch tab to Sign In and fill email
    LaunchedEffect(successMessage) {
        if (successMessage != null && selectedTabIndex == 1) {
            signInEmail = signUpEmail
            signInPassword = signUpPassword
            selectedTabIndex = 0
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo & Brand Header
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(GymCyan.copy(alpha = 0.25f), GymEmerald.copy(alpha = 0.15f))
                        )
                    )
                    .border(2.dp, GymCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = "IronPulse Logo",
                    tint = GymCyan,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "IRONPULSE",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
            Text(
                text = "High-Performance Athletic & Gym Management OS",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Navigation (Sign In vs Sign Up)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = GymSurface,
                contentColor = GymCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = if (selectedTabIndex == 0) GymCyan else GymEmerald,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, GymCardBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        onClearMessages()
                        selectedTabIndex = 0
                    },
                    modifier = Modifier.testTag("tab_sign_in"),
                    text = {
                        Text(
                            text = "SIGN IN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTabIndex == 0) GymCyan else TextMuted
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        onClearMessages()
                        selectedTabIndex = 1
                    },
                    modifier = Modifier.testTag("tab_sign_up"),
                    text = {
                        Text(
                            text = "SIGN UP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTabIndex == 1) GymEmerald else TextMuted
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Feedback Banners
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GymRose.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GymRose, GymAmber))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = GymRose,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = successMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GymEmerald.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GymEmerald, GymCyan))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = GymEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = successMessage ?: "",
                            color = GymEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Main Tab Content Card
            Card(
                colors = CardDefaults.cardColors(containerColor = GymSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GymCardBorder, GymCyan.copy(alpha = 0.2f)))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedTabIndex == 0) {
                        // SIGN IN FORM
                        Text(
                            text = "Portal Authentication",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "Sign in to access coach administration or athlete performance portal.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = signInEmail,
                            onValueChange = { signInEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Icon(Icons.Default.Mail, contentDescription = null, tint = GymCyan)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymCyan,
                                unfocusedBorderColor = GymCardBorder,
                                focusedLabelColor = GymCyan,
                                unfocusedLabelColor = TextMuted,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = signInPassword,
                            onValueChange = { signInPassword = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = GymCyan)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showSignInPassword = !showSignInPassword }) {
                                    Icon(
                                        imageVector = if (showSignInPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = TextMuted
                                    )
                                }
                            },
                            visualTransformation = if (showSignInPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymCyan,
                                unfocusedBorderColor = GymCardBorder,
                                focusedLabelColor = GymCyan,
                                unfocusedLabelColor = TextMuted,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input")
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (signInEmail.isNotBlank() && signInPassword.isNotBlank()) {
                                    onSignIn(signInEmail.trim(), signInPassword.trim())
                                }
                            },
                            enabled = signInEmail.isNotBlank() && signInPassword.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GymCyan,
                                contentColor = GymDarkBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_signin_button")
                        ) {
                            Text(
                                text = "SIGN IN TO PORTAL",
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        // Quick Demo Logins
                        Text(
                            text = "QUICK DEMO ACCOUNTS",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        QuickDemoChip(
                            title = "🛡️ Coach Elena Rostova",
                            subtitle = "Full Staff & Roster Administration",
                            email = "coach@fitgym.com",
                            badgeColor = GymCyan,
                            onClick = {
                                signInEmail = "coach@fitgym.com"
                                signInPassword = "coach"
                                onSignIn("coach@fitgym.com", "coach")
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        QuickDemoChip(
                            title = "🏋️ Alex Rivera",
                            subtitle = "Member • Cutting Phase • Checked In",
                            email = "alex@fitgym.com",
                            badgeColor = GymEmerald,
                            onClick = {
                                signInEmail = "alex@fitgym.com"
                                signInPassword = "password"
                                onSignIn("alex@fitgym.com", "password")
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        QuickDemoChip(
                            title = "⚡ Sarah Jenkins",
                            subtitle = "Member • Strength / Bulking Phase",
                            email = "sarah@fitgym.com",
                            badgeColor = GymAmber,
                            onClick = {
                                signInEmail = "sarah@fitgym.com"
                                signInPassword = "password"
                                onSignIn("sarah@fitgym.com", "password")
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        QuickDemoChip(
                            title = "⏳ Mike Ross (Pending Signup)",
                            subtitle = "Applicant • Awaiting Coach Review & Role Decision",
                            email = "mike@fitgym.com",
                            badgeColor = GymAmber,
                            onClick = {
                                signInEmail = "mike@fitgym.com"
                                signInPassword = "password"
                                onSignIn("mike@fitgym.com", "password")
                            }
                        )
                    } else {
                        // SIGN UP FORM
                        Text(
                            text = "Register New Account",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "Submit registration. Gym coach must review your application and assign your role (Coach or Member) before access is activated.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Coach Review Notice Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GymAmber.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GymAmber.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = GymAmber, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "COACH APPROVAL REQUIRED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GymAmber,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Your account will be held in pending status until the head coach reviews and approves you as a Coach or Member.",
                                        fontSize = 9.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Role Selector
                        Text(
                            text = "ACCOUNT TYPE / ROLE",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = signUpRole == "MEMBER",
                                onClick = { signUpRole = "MEMBER" },
                                label = { Text("Athlete (Member)", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GymEmerald,
                                    selectedLabelColor = GymDarkBg,
                                    selectedLeadingIconColor = GymDarkBg
                                ),
                                modifier = Modifier.weight(1f).testTag("signup_role_member")
                            )

                            FilterChip(
                                selected = signUpRole == "COACH",
                                onClick = { signUpRole = "COACH" },
                                label = { Text("Staff (Coach)", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GymCyan,
                                    selectedLabelColor = GymDarkBg,
                                    selectedLeadingIconColor = GymDarkBg
                                ),
                                modifier = Modifier.weight(1f).testTag("signup_role_coach")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = signUpName,
                            onValueChange = { signUpName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Jordan Lee") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GymEmerald) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymEmerald,
                                unfocusedBorderColor = GymCardBorder,
                                focusedLabelColor = GymEmerald,
                                unfocusedLabelColor = TextMuted,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("auth_signup_name")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = signUpEmail,
                            onValueChange = { signUpEmail = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("jordan@example.com") },
                            leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = GymEmerald) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymEmerald,
                                unfocusedBorderColor = GymCardBorder,
                                focusedLabelColor = GymEmerald,
                                unfocusedLabelColor = TextMuted,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("auth_signup_email")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = signUpPassword,
                            onValueChange = { signUpPassword = it },
                            label = { Text("Create Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GymEmerald) },
                            trailingIcon = {
                                IconButton(onClick = { showSignUpPassword = !showSignUpPassword }) {
                                    Icon(
                                        imageVector = if (showSignUpPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = TextMuted
                                    )
                                }
                            },
                            visualTransformation = if (showSignUpPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymEmerald,
                                unfocusedBorderColor = GymCardBorder,
                                focusedLabelColor = GymEmerald,
                                unfocusedLabelColor = TextMuted,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("auth_signup_password")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Biometrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = signUpAge,
                                onValueChange = { signUpAge = it },
                                label = { Text("Age") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GymEmerald,
                                    unfocusedBorderColor = GymCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = signUpWeight,
                                onValueChange = { signUpWeight = it },
                                label = { Text("Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GymEmerald,
                                    unfocusedBorderColor = GymCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = signUpHeight,
                                onValueChange = { signUpHeight = it },
                                label = { Text("Height (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GymEmerald,
                                    unfocusedBorderColor = GymCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Goal Phase Choice
                        Text(
                            text = "PRIMARY GOAL PHASE",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Bulking", "Cutting", "Recomp", "Endurance").forEach { phase ->
                                val selected = signUpGoalPhase == phase
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) GymEmerald.copy(alpha = 0.25f) else GymDarkBg)
                                        .border(
                                            1.dp,
                                            if (selected) GymEmerald else GymCardBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { signUpGoalPhase = phase }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = phase,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) GymEmerald else TextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (signUpName.isNotBlank() && signUpEmail.isNotBlank() && signUpPassword.isNotBlank()) {
                                    onSignUp(
                                        signUpName.trim(),
                                        signUpEmail.trim(),
                                        signUpPassword.trim(),
                                        signUpRole,
                                        signUpGender,
                                        signUpAge.toIntOrNull() ?: 25,
                                        signUpWeight.toDoubleOrNull() ?: 75.0,
                                        signUpHeight.toDoubleOrNull() ?: 175.0,
                                        signUpGoalPhase,
                                        signUpLevel
                                    )
                                }
                            },
                            enabled = signUpName.isNotBlank() && signUpEmail.isNotBlank() && signUpPassword.length >= 3,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GymEmerald,
                                contentColor = GymDarkBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_signup_button")
                        ) {
                            Text(
                                text = "COMPLETE SIGN UP",
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun QuickDemoChip(
    title: String,
    subtitle: String,
    email: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymDarkBg),
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GymCardBorder, badgeColor.copy(alpha = 0.3f)))),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
            Text(
                text = "1-CLICK",
                color = badgeColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
