package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.ui.screens.intro.IronPulseIntroScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.repository.GymRepository
import com.example.ui.CoachTab
import com.example.ui.GymViewModel
import com.example.ui.GymViewModelFactory
import com.example.ui.MemberTab
import com.example.ui.UserRole
import com.example.ui.components.IronPulseHeader
import com.example.ui.dialogs.AddClassDialog
import com.example.ui.dialogs.AddExerciseDialog
import com.example.ui.dialogs.AddMealDialog
import com.example.ui.dialogs.AddMemberDialog
import com.example.ui.dialogs.LogWeightDialog
import com.example.ui.dialogs.LogWorkoutActivityDialog
import com.example.ui.dialogs.SmartProtocolDialog
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.classes.ClassScheduleScreen
import com.example.ui.screens.coach.CoachAuditScreen
import com.example.ui.screens.coach.CoachBillingScreen
import com.example.ui.screens.coach.CoachProtocolScreen
import com.example.ui.screens.coach.CoachRosterScreen
import com.example.ui.screens.member.MemberAnalyticsScreen
import com.example.ui.screens.member.MemberDietScreen
import com.example.ui.screens.member.MemberHomeScreen
import com.example.ui.screens.member.MemberPassScreen
import com.example.ui.screens.member.MemberWorkoutScreen
import com.example.ui.theme.GymCardBorder
import com.example.ui.theme.GymCyan
import com.example.ui.theme.GymDarkBg
import com.example.ui.theme.GymEmerald
import com.example.ui.theme.GymSurface
import com.example.ui.theme.IronPulseTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: GymViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = GymRepository(database.gymDao())
        GymViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IronPulseTheme {
                IronPulseApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun IronPulseApp(viewModel: GymViewModel) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val coachTab by viewModel.coachTab.collectAsStateWithLifecycle()
    val memberTab by viewModel.memberTab.collectAsStateWithLifecycle()

    val members by viewModel.members.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()

    val selectedMemberId by viewModel.selectedMemberId.collectAsStateWithLifecycle()
    val activeMemberId by viewModel.activeMemberId.collectAsStateWithLifecycle()

    val selectedMember by viewModel.selectedMember.collectAsStateWithLifecycle()
    val activeMember by viewModel.activeMember.collectAsStateWithLifecycle()

    val activeMeals by viewModel.activeMeals.collectAsStateWithLifecycle()
    val activeWorkouts by viewModel.activeWorkouts.collectAsStateWithLifecycle()
    val activeWeightLogs by viewModel.activeWeightLogs.collectAsStateWithLifecycle()
    val activeMemberInvoices by viewModel.activeMemberInvoices.collectAsStateWithLifecycle()

    val selectedMeals by viewModel.selectedMeals.collectAsStateWithLifecycle()
    val selectedWorkouts by viewModel.selectedWorkouts.collectAsStateWithLifecycle()

    val rosterSearchQuery by viewModel.rosterSearchQuery.collectAsStateWithLifecycle()
    val rosterFilter by viewModel.rosterFilter.collectAsStateWithLifecycle()
    val classDayFilter by viewModel.classDayFilter.collectAsStateWithLifecycle()
    val classCategoryFilter by viewModel.classCategoryFilter.collectAsStateWithLifecycle()

    // Auth & Splash State
    var showIntroSplash by rememberSaveable { mutableStateOf(true) }
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    val authUser by viewModel.authUser.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authSuccessMessage by viewModel.authSuccessMessage.collectAsStateWithLifecycle()

    // Workout Progress Logs & Personal Bests
    val activeMemberProgressLogs by viewModel.activeMemberProgressLogs.collectAsStateWithLifecycle()
    val activeMemberPersonalBests by viewModel.activeMemberPersonalBests.collectAsStateWithLifecycle()
    val showLogWorkoutActivityDialog by viewModel.showLogWorkoutActivityDialog.collectAsStateWithLifecycle()

    // Dialogs
    val showAddMemberDialog by viewModel.showAddMemberDialog.collectAsStateWithLifecycle()
    val showAiGenDialog by viewModel.showAiGeneratorDialog.collectAsStateWithLifecycle()
    val showAddClassDialog by viewModel.showAddClassDialog.collectAsStateWithLifecycle()
    val showWeightLogDialog by viewModel.showWeightLogDialog.collectAsStateWithLifecycle()
    val showAddMealDialog by viewModel.showAddMealDialog.collectAsStateWithLifecycle()
    val showAddExerciseDialog by viewModel.showAddExerciseDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissStatusMessage()
        }
    }

    // Monolithic Twin Bars & Typographic Reveal Splash Screen
    if (showIntroSplash) {
        IronPulseIntroScreen(onFinished = { showIntroSplash = false })
        return
    }

    // Portal Authentication (Sign In & Sign Up)
    if (!isAuthenticated) {
        AuthScreen(
            errorMessage = authError,
            successMessage = authSuccessMessage,
            onSignIn = { email, password -> viewModel.signIn(email, password) },
            onSignUp = { name, email, password, role, gender, age, weight, height, phase, level ->
                viewModel.signUp(name, email, password, role, gender, age, weight, height, phase, level)
            },
            onClearMessages = { viewModel.clearAuthMessages() }
        )
        return
    }

    // Handle back button
    BackHandler(enabled = currentRole == UserRole.COACH && coachTab != CoachTab.ROSTER) {
        viewModel.setCoachTab(CoachTab.ROSTER)
    }
    BackHandler(enabled = currentRole == UserRole.MEMBER && memberTab != MemberTab.HOME) {
        viewModel.setMemberTab(MemberTab.HOME)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            IronPulseHeader(
                role = currentRole,
                activeMemberName = activeMember?.name ?: authUser?.name ?: "Athlete",
                allMembers = members,
                onToggleRole = {
                    val nextRole = if (currentRole == UserRole.COACH) UserRole.MEMBER else UserRole.COACH
                    viewModel.setRole(nextRole)
                },
                onSelectActiveMember = { id ->
                    viewModel.setActiveMemberId(id)
                },
                onSignOut = {
                    viewModel.signOut()
                }
            )
        },
        bottomBar = {
            IronPulseBottomNavigation(
                role = currentRole,
                currentCoachTab = coachTab,
                currentMemberTab = memberTab,
                onSelectCoachTab = { viewModel.setCoachTab(it) },
                onSelectMemberTab = { viewModel.setMemberTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = GymDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GymDarkBg)
        ) {
            if (currentRole == UserRole.COACH) {
                when (coachTab) {
                    CoachTab.ROSTER -> {
                        CoachRosterScreen(
                            members = members,
                            selectedMemberId = selectedMemberId,
                            searchQuery = rosterSearchQuery,
                            filterOption = rosterFilter,
                            onSearchChange = { viewModel.setRosterSearch(it) },
                            onFilterChange = { viewModel.setRosterFilter(it) },
                            onSelectMember = { id ->
                                viewModel.setSelectedMemberId(id)
                                viewModel.setActiveMemberId(id)
                            },
                            onApproveMember = { id, approved -> viewModel.setMemberApproval(id, approved) },
                            onApproveAsRole = { id, role -> viewModel.approveMemberAsRole(id, role) },
                            onRejectApplication = { id -> viewModel.rejectMemberApplication(id) },
                            onOpenAddMember = { viewModel.setShowAddMemberDialog(true) },
                            onOpenAiGen = { viewModel.setShowAiGeneratorDialog(true) },
                            onRegenerateSchedule = { viewModel.regenerateAutomatedWeeklySchedule() },
                            onNavigateToProtocol = { viewModel.setCoachTab(CoachTab.PROTOCOL) },
                            onUpdateRole = { id, role -> viewModel.updateMemberRole(id, role) }
                        )
                    }
                    CoachTab.PROTOCOL -> {
                        CoachProtocolScreen(
                            member = selectedMember,
                            meals = selectedMeals,
                            workouts = selectedWorkouts,
                            onUpdatePhase = { phase -> selectedMember?.let { viewModel.updateGoalPhase(it.id, phase) } },
                            onUpdateLevel = { lvl -> selectedMember?.let { viewModel.updateChallengeLevel(it.id, lvl) } },
                            onUpdateBiometrics = { w, h, a, g -> selectedMember?.let { viewModel.updateBiometrics(it.id, w, h, a, g) } },
                            onUpdateMacros = { cal, p, c, f -> selectedMember?.let { viewModel.updateCustomMacros(it.id, cal, p, c, f) } },
                            onDeleteMeal = { viewModel.deleteMeal(it) },
                            onDeleteWorkout = { viewModel.deleteWorkout(it) },
                            onOpenAddMeal = { viewModel.setShowAddMealDialog(true) },
                            onOpenAddWorkout = { viewModel.setShowAddExerciseDialog(true) },
                            onOpenAiGen = { viewModel.setShowAiGeneratorDialog(true) }
                        )
                    }
                    CoachTab.CLASSES -> {
                        ClassScheduleScreen(
                            userRole = UserRole.COACH,
                            classes = classes,
                            allMembers = members,
                            activeMemberId = activeMemberId,
                            selectedDay = classDayFilter,
                            selectedCategory = classCategoryFilter,
                            onDaySelect = { viewModel.setClassDayFilter(it) },
                            onCategorySelect = { viewModel.setClassCategoryFilter(it) },
                            onBookClass = { classId -> viewModel.bookClass(classId, activeMemberId) },
                            onCancelBooking = { classId -> viewModel.cancelClassBooking(classId, activeMemberId) },
                            onAutoBookRecommended = { viewModel.autoBookMatchingClasses(activeMemberId) },
                            onRegenerateAutomatedSchedule = { viewModel.regenerateAutomatedWeeklySchedule() },
                            onOpenAddClass = { viewModel.setShowAddClassDialog(true) }
                        )
                    }
                    CoachTab.BILLING -> {
                        CoachBillingScreen(
                            members = members,
                            invoices = invoices,
                            onRenewSubscription = { memberId, plan, amount ->
                                viewModel.renewSubscription(memberId, plan, amount)
                            }
                        )
                    }
                    CoachTab.AUDIT -> {
                        CoachAuditScreen(auditLogs = auditLogs)
                    }
                }
            } else {
                when (memberTab) {
                    MemberTab.HOME -> {
                        MemberHomeScreen(
                            member = activeMember,
                            meals = activeMeals,
                            workouts = activeWorkouts,
                            onToggleCheckIn = { activeMember?.let { viewModel.toggleGymPresence(it.id) } },
                            onLogWeight = { w -> activeMember?.let { viewModel.logDailyWeight(it.id, w) } },
                            onRenewSubscription = { plan, amount ->
                                activeMember?.let { viewModel.renewSubscription(it.id, plan, amount) }
                            },
                            onNavigateToDiet = { viewModel.setMemberTab(MemberTab.DIET) },
                            onNavigateToWorkout = { viewModel.setMemberTab(MemberTab.WORKOUT) },
                            onNavigateToClasses = { viewModel.setMemberTab(MemberTab.CLASSES) }
                        )
                    }
                    MemberTab.DIET -> {
                        MemberDietScreen(
                            member = activeMember,
                            meals = activeMeals,
                            onToggleMeal = { viewModel.toggleMeal(it) }
                        )
                    }
                    MemberTab.WORKOUT -> {
                        MemberWorkoutScreen(
                            member = activeMember,
                            workouts = activeWorkouts,
                            onToggleWorkout = { viewModel.toggleWorkout(it) },
                            progressLogs = activeMemberProgressLogs,
                            personalBests = activeMemberPersonalBests,
                            onOpenLogActivity = { viewModel.setShowLogWorkoutActivityDialog(true) },
                            onDeleteLog = { viewModel.deleteProgressLog(it) }
                        )
                    }
                    MemberTab.CLASSES -> {
                        ClassScheduleScreen(
                            userRole = UserRole.MEMBER,
                            classes = classes,
                            allMembers = members,
                            activeMemberId = activeMemberId,
                            selectedDay = classDayFilter,
                            selectedCategory = classCategoryFilter,
                            onDaySelect = { viewModel.setClassDayFilter(it) },
                            onCategorySelect = { viewModel.setClassCategoryFilter(it) },
                            onBookClass = { classId -> viewModel.bookClass(classId, activeMemberId) },
                            onCancelBooking = { classId -> viewModel.cancelClassBooking(classId, activeMemberId) },
                            onAutoBookRecommended = { viewModel.autoBookMatchingClasses(activeMemberId) },
                            onRegenerateAutomatedSchedule = { viewModel.regenerateAutomatedWeeklySchedule() },
                            onOpenAddClass = { viewModel.setShowAddClassDialog(true) }
                        )
                    }
                    MemberTab.ANALYTICS -> {
                        MemberAnalyticsScreen(
                            member = activeMember,
                            weightLogs = activeWeightLogs
                        )
                    }
                    MemberTab.PASS -> {
                        MemberPassScreen(
                            member = activeMember,
                            invoices = activeMemberInvoices,
                            onRenew = { plan, amount ->
                                activeMember?.let { viewModel.renewSubscription(it.id, plan, amount) }
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { viewModel.setShowAddMemberDialog(false) },
            onConfirm = { name, email, gender, age, weight, height, phase, level ->
                viewModel.addNewMember(name, email, gender, age, weight, height, phase, level)
            }
        )
    }

    if (showAiGenDialog) {
        val target = if (currentRole == UserRole.COACH) selectedMember else activeMember
        if (target != null) {
            SmartProtocolDialog(
                memberName = target.name,
                currentAge = target.age,
                currentGender = target.gender,
                currentWeight = target.currentWeight,
                currentHeight = target.heightCm,
                currentPhase = target.goalPhase,
                currentLevel = target.challengeLevel,
                onDismiss = { viewModel.setShowAiGeneratorDialog(false) },
                onGenerate = { age, gender, weight, height, phase, level ->
                    viewModel.runSmartProtocolGenerator(target.id, age, gender, weight, height, phase, level)
                }
            )
        }
    }

    if (showAddClassDialog) {
        AddClassDialog(
            onDismiss = { viewModel.setShowAddClassDialog(false) },
            onConfirm = { title, cat, inst, room, day, time, cap, intensity, targetPhase ->
                viewModel.addNewClass(title, cat, inst, room, day, time, cap, intensity, targetPhase)
            }
        )
    }

    if (showWeightLogDialog) {
        val currentW = activeMember?.currentWeight ?: 75.0
        LogWeightDialog(
            currentWeight = currentW,
            onDismiss = { viewModel.setShowWeightLogDialog(false) },
            onConfirm = { w -> activeMember?.let { viewModel.logDailyWeight(it.id, w) } }
        )
    }

    if (showAddMealDialog) {
        val memberId = if (currentRole == UserRole.COACH) selectedMemberId else activeMemberId
        AddMealDialog(
            onDismiss = { viewModel.setShowAddMealDialog(false) },
            onConfirm = { name, desc, time -> viewModel.addMeal(memberId, name, desc, time) }
        )
    }

    if (showAddExerciseDialog) {
        val memberId = if (currentRole == UserRole.COACH) selectedMemberId else activeMemberId
        AddExerciseDialog(
            onDismiss = { viewModel.setShowAddExerciseDialog(false) },
            onConfirm = { ex, sets, reps, load -> viewModel.addWorkout(memberId, ex, sets, reps, load) }
        )
    }

    if (showLogWorkoutActivityDialog) {
        LogWorkoutActivityDialog(
            onDismiss = { viewModel.setShowLogWorkoutActivityDialog(false) },
            onConfirm = { exercise, weight, reps, sets, notes ->
                viewModel.logWorkoutActivity(activeMemberId, exercise, weight, reps, sets, notes)
            }
        )
    }
}

@Composable
fun IronPulseBottomNavigation(
    role: UserRole,
    currentCoachTab: CoachTab,
    currentMemberTab: MemberTab,
    onSelectCoachTab: (CoachTab) -> Unit,
    onSelectMemberTab: (MemberTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = GymSurface,
        modifier = modifier.fillMaxWidth()
    ) {
        if (role == UserRole.COACH) {
            NavigationBarItem(
                selected = currentCoachTab == CoachTab.ROSTER,
                onClick = { onSelectCoachTab(CoachTab.ROSTER) },
                icon = { Icon(Icons.Default.People, contentDescription = "Roster") },
                label = { Text("Roster", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymCyan),
                modifier = Modifier.testTag("nav_coach_roster")
            )
            NavigationBarItem(
                selected = currentCoachTab == CoachTab.PROTOCOL,
                onClick = { onSelectCoachTab(CoachTab.PROTOCOL) },
                icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Protocol") },
                label = { Text("Protocol", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymCyan),
                modifier = Modifier.testTag("nav_coach_protocol")
            )
            NavigationBarItem(
                selected = currentCoachTab == CoachTab.CLASSES,
                onClick = { onSelectCoachTab(CoachTab.CLASSES) },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Schedule") },
                label = { Text("Classes", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymCyan),
                modifier = Modifier.testTag("nav_coach_classes")
            )
            NavigationBarItem(
                selected = currentCoachTab == CoachTab.BILLING,
                onClick = { onSelectCoachTab(CoachTab.BILLING) },
                icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Billing") },
                label = { Text("Billing", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymCyan),
                modifier = Modifier.testTag("nav_coach_billing")
            )
            NavigationBarItem(
                selected = currentCoachTab == CoachTab.AUDIT,
                onClick = { onSelectCoachTab(CoachTab.AUDIT) },
                icon = { Icon(Icons.Default.Assignment, contentDescription = "Audit") },
                label = { Text("Audit", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymCyan),
                modifier = Modifier.testTag("nav_coach_audit")
            )
        } else {
            NavigationBarItem(
                selected = currentMemberTab == MemberTab.HOME,
                onClick = { onSelectMemberTab(MemberTab.HOME) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymEmerald),
                modifier = Modifier.testTag("nav_member_home")
            )
            NavigationBarItem(
                selected = currentMemberTab == MemberTab.DIET,
                onClick = { onSelectMemberTab(MemberTab.DIET) },
                icon = { Icon(Icons.Default.Restaurant, contentDescription = "Diet") },
                label = { Text("Diet", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymEmerald),
                modifier = Modifier.testTag("nav_member_diet")
            )
            NavigationBarItem(
                selected = currentMemberTab == MemberTab.WORKOUT,
                onClick = { onSelectMemberTab(MemberTab.WORKOUT) },
                icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Workout") },
                label = { Text("Routine", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymEmerald),
                modifier = Modifier.testTag("nav_member_workout")
            )
            NavigationBarItem(
                selected = currentMemberTab == MemberTab.CLASSES,
                onClick = { onSelectMemberTab(MemberTab.CLASSES) },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Classes") },
                label = { Text("Classes", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymEmerald),
                modifier = Modifier.testTag("nav_member_classes")
            )
            NavigationBarItem(
                selected = currentMemberTab == MemberTab.ANALYTICS,
                onClick = { onSelectMemberTab(MemberTab.ANALYTICS) },
                icon = { Icon(Icons.Default.BarChart, contentDescription = "Analytics") },
                label = { Text("Analytics", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymEmerald),
                modifier = Modifier.testTag("nav_member_analytics")
            )
            NavigationBarItem(
                selected = currentMemberTab == MemberTab.PASS,
                onClick = { onSelectMemberTab(MemberTab.PASS) },
                icon = { Icon(Icons.Default.CreditCard, contentDescription = "Pass") },
                label = { Text("Pass", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = navigationColors(GymEmerald),
                modifier = Modifier.testTag("nav_member_pass")
            )
        }
    }
}

@Composable
private fun navigationColors(activeColor: androidx.compose.ui.graphics.Color) = NavigationBarItemDefaults.colors(
    selectedIconColor = GymDarkBg,
    selectedTextColor = activeColor,
    indicatorColor = activeColor,
    unselectedIconColor = TextMuted,
    unselectedTextColor = TextMuted
)
