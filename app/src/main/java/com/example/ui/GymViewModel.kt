package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuditLogEntity
import com.example.data.model.GymClassEntity
import com.example.data.model.MealEntity
import com.example.data.model.MemberEntity
import com.example.data.model.PaymentInvoiceEntity
import com.example.data.model.WeightLogEntity
import com.example.data.model.WorkoutExerciseEntity
import com.example.data.model.WorkoutProgressLogEntity
import com.example.data.repository.GymRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class UserRole {
    COACH,
    MEMBER
}

enum class CoachTab {
    ROSTER,
    PROTOCOL,
    WORKOUTS,
    BILLING,
    AUDIT
}

enum class MemberTab {
    HOME,
    WORKOUT,
    DIET,
    ANALYTICS,
    PASS
}

class GymViewModel(private val repository: GymRepository) : ViewModel() {

    // Authentication State
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _authUser = MutableStateFlow<MemberEntity?>(null)
    val authUser: StateFlow<MemberEntity?> = _authUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

    // Roles and navigation
    private val _currentRole = MutableStateFlow(UserRole.COACH)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _coachTab = MutableStateFlow(CoachTab.ROSTER)
    val coachTab: StateFlow<CoachTab> = _coachTab.asStateFlow()

    private val _memberTab = MutableStateFlow(MemberTab.HOME)
    val memberTab: StateFlow<MemberTab> = _memberTab.asStateFlow()

    // Selected member for coach viewing/editing
    private val _selectedMemberId = MutableStateFlow("client_alex_01")
    val selectedMemberId: StateFlow<String> = _selectedMemberId.asStateFlow()

    // Currently logged in member for Member Portal
    private val _activeMemberId = MutableStateFlow("client_alex_01")
    val activeMemberId: StateFlow<String> = _activeMemberId.asStateFlow()

    // Filter states
    private val _rosterSearchQuery = MutableStateFlow("")
    val rosterSearchQuery: StateFlow<String> = _rosterSearchQuery.asStateFlow()

    private val _rosterFilter = MutableStateFlow("ALL") // ALL, AT_GYM, PENDING, EXPIRING
    val rosterFilter: StateFlow<String> = _rosterFilter.asStateFlow()

    private val _classDayFilter = MutableStateFlow("Monday")
    val classDayFilter: StateFlow<String> = _classDayFilter.asStateFlow()

    private val _classCategoryFilter = MutableStateFlow("ALL") // ALL, HIIT, Strength, Spin, Yoga, CrossFit, Boxing
    val classCategoryFilter: StateFlow<String> = _classCategoryFilter.asStateFlow()

    // Dialog state controllers
    private val _showAddMemberDialog = MutableStateFlow(false)
    val showAddMemberDialog: StateFlow<Boolean> = _showAddMemberDialog.asStateFlow()

    private val _showAiGeneratorDialog = MutableStateFlow(false)
    val showAiGeneratorDialog: StateFlow<Boolean> = _showAiGeneratorDialog.asStateFlow()

    private val _showAddClassDialog = MutableStateFlow(false)
    val showAddClassDialog: StateFlow<Boolean> = _showAddClassDialog.asStateFlow()

    private val _showPaymentDialog = MutableStateFlow(false)
    val showPaymentDialog: StateFlow<Boolean> = _showPaymentDialog.asStateFlow()

    private val _showWeightLogDialog = MutableStateFlow(false)
    val showWeightLogDialog: StateFlow<Boolean> = _showWeightLogDialog.asStateFlow()

    private val _showAddMealDialog = MutableStateFlow(false)
    val showAddMealDialog: StateFlow<Boolean> = _showAddMealDialog.asStateFlow()

    private val _showAddExerciseDialog = MutableStateFlow(false)
    val showAddExerciseDialog: StateFlow<Boolean> = _showAddExerciseDialog.asStateFlow()

    private val _showLogWorkoutActivityDialog = MutableStateFlow(false)
    val showLogWorkoutActivityDialog: StateFlow<Boolean> = _showLogWorkoutActivityDialog.asStateFlow()

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    private val _showAssignWorkoutDialog = MutableStateFlow(false)
    val showAssignWorkoutDialog: StateFlow<Boolean> = _showAssignWorkoutDialog.asStateFlow()

    private val _showAiMealAnalyzerDialog = MutableStateFlow(false)
    val showAiMealAnalyzerDialog: StateFlow<Boolean> = _showAiMealAnalyzerDialog.asStateFlow()

    // Toast/Snackbar notice
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Reactive streams
    val members: StateFlow<List<MemberEntity>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val classes: StateFlow<List<GymClassEntity>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<PaymentInvoiceEntity>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMember: StateFlow<MemberEntity?> = _activeMemberId
        .flatMapLatest { id -> repository.getMember(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedMember: StateFlow<MemberEntity?> = _selectedMemberId
        .flatMapLatest { id -> repository.getMember(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMeals: StateFlow<List<MealEntity>> = _activeMemberId
        .flatMapLatest { id -> repository.getMeals(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeWorkouts: StateFlow<List<WorkoutExerciseEntity>> = _activeMemberId
        .flatMapLatest { id -> repository.getWorkouts(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeWeightLogs: StateFlow<List<WeightLogEntity>> = _activeMemberId
        .flatMapLatest { id -> repository.getWeightLogs(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMemberInvoices: StateFlow<List<PaymentInvoiceEntity>> = _activeMemberId
        .flatMapLatest { id -> repository.getMemberInvoices(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMemberProgressLogs: StateFlow<List<WorkoutProgressLogEntity>> = _activeMemberId
        .flatMapLatest { id -> repository.getWorkoutProgress(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMemberPersonalBests: StateFlow<List<WorkoutProgressLogEntity>> = _activeMemberId
        .flatMapLatest { id -> repository.getPersonalBests(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Coach protocol view streams
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedMeals: StateFlow<List<MealEntity>> = _selectedMemberId
        .flatMapLatest { id -> repository.getMeals(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedWorkouts: StateFlow<List<WorkoutExerciseEntity>> = _selectedMemberId
        .flatMapLatest { id -> repository.getWorkouts(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedMemberProgressLogs: StateFlow<List<WorkoutProgressLogEntity>> = _selectedMemberId
        .flatMapLatest { id -> repository.getWorkoutProgress(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // Role switching
    fun setRole(role: UserRole) {
        _currentRole.value = role
        if (role == UserRole.COACH) {
            _coachTab.value = CoachTab.ROSTER
        } else {
            _memberTab.value = MemberTab.HOME
        }
    }

    fun setCoachTab(tab: CoachTab) { _coachTab.value = tab }
    fun setMemberTab(tab: MemberTab) { _memberTab.value = tab }

    fun setSelectedMemberId(id: String) { _selectedMemberId.value = id }
    fun setActiveMemberId(id: String) { _activeMemberId.value = id }

    fun setRosterSearch(query: String) { _rosterSearchQuery.value = query }
    fun setRosterFilter(filter: String) { _rosterFilter.value = filter }
    fun setClassDayFilter(day: String) { _classDayFilter.value = day }
    fun setClassCategoryFilter(cat: String) { _classCategoryFilter.value = cat }

    // Dialog toggles
    fun setShowAddMemberDialog(show: Boolean) { _showAddMemberDialog.value = show }
    fun setShowAiGeneratorDialog(show: Boolean) { _showAiGeneratorDialog.value = show }
    fun setShowAddClassDialog(show: Boolean) { _showAddClassDialog.value = show }
    fun setShowPaymentDialog(show: Boolean) { _showPaymentDialog.value = show }
    fun setShowWeightLogDialog(show: Boolean) { _showWeightLogDialog.value = show }
    fun setShowAddMealDialog(show: Boolean) { _showAddMealDialog.value = show }
    fun setShowAddExerciseDialog(show: Boolean) { _showAddExerciseDialog.value = show }
    fun setShowLogWorkoutActivityDialog(show: Boolean) { _showLogWorkoutActivityDialog.value = show }
    fun setShowEditProfileDialog(show: Boolean) { _showEditProfileDialog.value = show }
    fun setShowAssignWorkoutDialog(show: Boolean) { _showAssignWorkoutDialog.value = show }
    fun setShowAiMealAnalyzerDialog(show: Boolean) { _showAiMealAnalyzerDialog.value = show }

    fun addWater(memberId: String, amountMl: Int) {
        viewModelScope.launch {
            val member = members.value.find { it.id == memberId } ?: return@launch
            val newWater = (member.waterIntakeMl + amountMl).coerceAtLeast(0)
            repository.updateWaterIntake(memberId, newWater)
            _statusMessage.value = "Hydration logged: +${amountMl}ml ($newWater / 3000 ml)"
        }
    }

    fun resetWater(memberId: String) {
        viewModelScope.launch {
            repository.updateWaterIntake(memberId, 0)
            _statusMessage.value = "Daily water tracker reset to 0 ml."
        }
    }

    fun updateMemberProfile(
        memberId: String,
        name: String,
        weight: Double,
        height: Double,
        goalPhase: String,
        challengeLevel: String
    ) {
        viewModelScope.launch {
            repository.updateMemberProfile(memberId, name, weight, height, goalPhase, challengeLevel)
            if (_authUser.value?.id == memberId) {
                _authUser.value = _authUser.value?.copy(
                    name = name,
                    currentWeight = weight,
                    heightCm = height,
                    goalPhase = goalPhase,
                    challengeLevel = challengeLevel
                )
            }
            _showEditProfileDialog.value = false
            _statusMessage.value = "Profile updated: $name ($goalPhase, ${weight}kg)"
        }
    }

    fun assignWorkout(
        memberId: String,
        exercise: String,
        sets: String,
        reps: String,
        weight: String,
        muscleGroup: String
    ) {
        viewModelScope.launch {
            repository.assignWorkoutToMember(memberId, exercise, sets, reps, weight, muscleGroup)
            _showAssignWorkoutDialog.value = false
            _statusMessage.value = "Daily workout assigned: $exercise ($muscleGroup)"
        }
    }

    fun sendAiAnalyzedMeals(
        memberId: String,
        meals: List<MealEntity>,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int
    ) {
        viewModelScope.launch {
            repository.sendAiAnalyzedMeals(memberId, meals, calories, protein, carbs, fat)
            _showAiMealAnalyzerDialog.value = false
            _statusMessage.value = "Prescribed AI meal plan sent to athlete ($calories kcal, ${protein}g protein)!"
        }
    }

    fun dismissStatusMessage() { _statusMessage.value = null }

    // Member Attendance Check-In
    fun toggleGymPresence(memberId: String) {
        viewModelScope.launch {
            repository.toggleGymPresence(memberId)
        }
    }

    // Member Approval
    fun setMemberApproval(memberId: String, approved: Boolean) {
        viewModelScope.launch {
            repository.approveMember(memberId, approved)
            _statusMessage.value = if (approved) "Member approved" else "Member declined"
        }
    }

    // Subscription Renewal
    fun renewSubscription(memberId: String, plan: String = "30-Day Monthly Pass", amount: Double = 49.0) {
        viewModelScope.launch {
            repository.renewSubscription(memberId, plan, amount)
            _statusMessage.value = "Subscription renewed ($$amount)"
        }
    }

    // Class Booking & Cancellation
    fun bookClass(classId: String, memberId: String) {
        viewModelScope.launch {
            val success = repository.bookClass(classId, memberId)
            if (success) {
                _statusMessage.value = "Class booked successfully! Spot reserved."
            } else {
                _statusMessage.value = "Unable to book. Class may be full or already booked."
            }
        }
    }

    fun cancelClassBooking(classId: String, memberId: String) {
        viewModelScope.launch {
            val success = repository.cancelBooking(classId, memberId)
            if (success) {
                _statusMessage.value = "Class booking cancelled."
            }
        }
    }

    // Automated Timetable Regeneration
    fun regenerateAutomatedWeeklySchedule() {
        viewModelScope.launch {
            repository.generateWeeklyTimetable(resetExisting = true)
            _statusMessage.value = "Automated class timetable regenerated for all 7 days!"
        }
    }

    // Smart Auto-Book for Member
    fun autoBookMatchingClasses(memberId: String) {
        viewModelScope.launch {
            val member = members.value.find { it.id == memberId } ?: return@launch
            val targetPhase = member.goalPhase
            val currentClasses = classes.value
            var bookedCount = 0

            for (cls in currentClasses) {
                if (bookedCount >= 3) break
                val isPhaseMatch = cls.targetPhase == targetPhase || cls.targetPhase == "All"
                val bookings = if (cls.bookedMemberIds.isBlank()) emptyList() else cls.bookedMemberIds.split(",")
                if (isPhaseMatch && !bookings.contains(memberId) && bookings.size < cls.capacity) {
                    val ok = repository.bookClass(cls.id, memberId)
                    if (ok) bookedCount++
                }
            }
            _statusMessage.value = if (bookedCount > 0) {
                "Automated smart scheduling booked $bookedCount optimized sessions for $targetPhase!"
            } else {
                "All matching classes are already booked or at capacity."
            }
        }
    }

    // Log Daily Weight
    fun logDailyWeight(memberId: String, weight: Double) {
        viewModelScope.launch {
            repository.logDailyWeight(memberId, weight)
            _statusMessage.value = "Logged weight: $weight kg"
            _showWeightLogDialog.value = false
        }
    }

    // Goal Phase & Macro recalculation
    fun updateGoalPhase(memberId: String, phase: String) {
        viewModelScope.launch {
            val member = members.value.find { it.id == memberId } ?: return@launch
            val (cal, prot, carb, fat) = GymRepository.calculateMacrosForPhase(member.currentWeight, phase)
            val updated = member.copy(
                goalPhase = phase,
                dailyCalories = cal,
                proteinG = prot,
                carbsG = carb,
                fatG = fat
            )
            repository.updateMemberProtocol(updated)
            _statusMessage.value = "Updated phase to $phase with tailored macros."
        }
    }

    fun updateChallengeLevel(memberId: String, level: String) {
        viewModelScope.launch {
            val member = members.value.find { it.id == memberId } ?: return@launch
            repository.updateMemberProtocol(member.copy(challengeLevel = level))
        }
    }

    fun updateBiometrics(memberId: String, weight: Double, height: Double, age: Int, gender: String) {
        viewModelScope.launch {
            val member = members.value.find { it.id == memberId } ?: return@launch
            val (cal, prot, carb, fat) = GymRepository.calculateMacrosForPhase(weight, member.goalPhase)
            val updated = member.copy(
                currentWeight = weight,
                heightCm = height,
                age = age,
                gender = gender,
                dailyCalories = cal,
                proteinG = prot,
                carbsG = carb,
                fatG = fat
            )
            repository.updateMemberProtocol(updated)
            _statusMessage.value = "Biometrics and macro targets refreshed."
        }
    }

    fun updateCustomMacros(memberId: String, calories: Int, prot: Int, carb: Int, fat: Int) {
        viewModelScope.launch {
            val member = members.value.find { it.id == memberId } ?: return@launch
            repository.updateMemberProtocol(member.copy(
                dailyCalories = calories,
                proteinG = prot,
                carbsG = carb,
                fatG = fat
            ))
        }
    }

    // Meal & Workout Toggles
    fun toggleMeal(meal: MealEntity) {
        viewModelScope.launch { repository.toggleMealCompleted(meal) }
    }

    fun toggleWorkout(exercise: WorkoutExerciseEntity) {
        viewModelScope.launch { repository.toggleWorkoutCompleted(exercise) }
    }

    fun addMeal(memberId: String, name: String, desc: String, time: String) {
        viewModelScope.launch {
            val meal = MealEntity(
                id = "m_${System.currentTimeMillis()}",
                memberId = memberId,
                name = name,
                description = desc,
                time = time,
                isCompleted = false
            )
            repository.addMeal(meal)
            _showAddMealDialog.value = false
        }
    }

    fun deleteMeal(mealId: String) {
        viewModelScope.launch { repository.deleteMeal(mealId) }
    }

    fun addWorkout(memberId: String, exercise: String, sets: String, reps: String, weight: String) {
        viewModelScope.launch {
            val workout = WorkoutExerciseEntity(
                id = "w_${System.currentTimeMillis()}",
                memberId = memberId,
                exercise = exercise,
                sets = sets,
                reps = reps,
                weight = weight,
                isCompleted = false
            )
            repository.addWorkout(workout)
            _showAddExerciseDialog.value = false
        }
    }

    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch { repository.deleteWorkout(workoutId) }
    }

    fun addNewMember(
        name: String,
        email: String,
        gender: String,
        age: Int,
        weight: Double,
        height: Double,
        goalPhase: String,
        challengeLevel: String
    ) {
        viewModelScope.launch {
            val newId = "client_${System.currentTimeMillis()}"
            val (cal, prot, carb, fat) = GymRepository.calculateMacrosForPhase(weight, goalPhase)
            val newMember = MemberEntity(
                id = newId,
                name = name,
                email = email,
                gender = gender,
                age = age,
                goalPhase = goalPhase,
                challengeLevel = challengeLevel,
                currentWeight = weight,
                targetWeight = (weight * 0.95).toInt().toDouble(),
                heightCm = height,
                dailyCalories = cal,
                proteinG = prot,
                carbsG = carb,
                fatG = fat,
                approved = true,
                atGym = false,
                subscriptionDaysLeft = 30,
                membershipTier = "Standard",
                joinedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            repository.addNewMember(newMember)
            _selectedMemberId.value = newId
            _activeMemberId.value = newId
            _showAddMemberDialog.value = false
            _statusMessage.value = "New athlete $name enrolled!"
        }
    }

    fun runSmartProtocolGenerator(
        memberId: String,
        age: Int,
        gender: String,
        weight: Double,
        height: Double,
        goalPhase: String,
        challengeLevel: String
    ) {
        viewModelScope.launch {
            repository.applyAiGeneratedProtocol(
                memberId = memberId,
                age = age,
                gender = gender,
                weight = weight,
                height = height,
                phase = goalPhase,
                challengeLevel = challengeLevel
            )
            _showAiGeneratorDialog.value = false
            _statusMessage.value = "Tailored diet and training protocol applied!"
        }
    }

    fun addNewClass(
        title: String,
        category: String,
        instructor: String,
        room: String,
        dayOfWeek: String,
        timeSlot: String,
        capacity: Int,
        intensity: String,
        targetPhase: String
    ) {
        viewModelScope.launch {
            val newClass = GymClassEntity(
                id = "cls_${System.currentTimeMillis()}",
                title = title,
                category = category,
                instructor = instructor,
                room = room,
                dayOfWeek = dayOfWeek,
                timeSlot = timeSlot,
                capacity = capacity,
                intensity = intensity,
                targetPhase = targetPhase,
                bookedMemberIds = "",
                isAutomated = false
            )
            repository.createCustomClass(newClass)
            _showAddClassDialog.value = false
            _statusMessage.value = "Class created: $title"
        }
    }

    fun logWorkoutActivity(
        memberId: String,
        exerciseName: String,
        weightKg: Double,
        reps: Int,
        sets: Int = 1,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val isPr = repository.logWorkoutActivity(memberId, exerciseName, weightKg, reps, sets, notes)
            _showLogWorkoutActivityDialog.value = false
            _statusMessage.value = if (isPr) {
                "🔥 NEW PERSONAL BEST! $exerciseName ($weightKg kg)"
            } else {
                "Logged $exerciseName: $weightKg kg × $reps reps"
            }
        }
    }

    fun deleteProgressLog(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutProgressLog(id)
            _statusMessage.value = "Entry removed"
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authError.value = null
            val member = repository.authenticateUser(email, password)
            if (member != null) {
                if (!member.approved) {
                    _authError.value = "⚠️ Account Pending Approval: A gym coach must approve your signup and assign your role (Coach or Member) before you can log in."
                    return@launch
                }
                _authUser.value = member
                _isAuthenticated.value = true
                _authError.value = null
                _authSuccessMessage.value = null
                if (member.role == "COACH") {
                    _currentRole.value = UserRole.COACH
                    _coachTab.value = CoachTab.ROSTER
                } else {
                    _currentRole.value = UserRole.MEMBER
                    _activeMemberId.value = member.id
                    _memberTab.value = MemberTab.HOME
                }
            } else {
                _authError.value = "Invalid email or password. Please verify credentials or use quick-demo."
            }
        }
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        role: String = "MEMBER",
        gender: String = "Male",
        age: Int = 25,
        weight: Double = 75.0,
        height: Double = 175.0,
        goalPhase: String = "Cutting",
        challengeLevel: String = "Intermediate"
    ) {
        viewModelScope.launch {
            _authError.value = null
            _authSuccessMessage.value = null
            val (success, message) = repository.signUpUser(
                name = name,
                email = email,
                password = password,
                role = role,
                gender = gender,
                age = age,
                weight = weight,
                height = height,
                goalPhase = goalPhase,
                challengeLevel = challengeLevel
            )
            if (success) {
                _authSuccessMessage.value = message
                _authError.value = null
            } else {
                _authError.value = message
            }
        }
    }

    fun signOut() {
        _isAuthenticated.value = false
        _authUser.value = null
        _authError.value = null
        _authSuccessMessage.value = null
    }

    fun clearAuthMessages() {
        _authError.value = null
        _authSuccessMessage.value = null
    }

    fun approveMemberAsRole(memberId: String, role: String) {
        viewModelScope.launch {
            repository.approveMemberWithRole(memberId, role)
            _statusMessage.value = if (role == "COACH") {
                "⚡ Approved as COACH! Staff access granted live."
            } else {
                "✅ Approved as MEMBER! Athlete portal enabled live."
            }
        }
    }

    fun rejectMemberApplication(memberId: String) {
        viewModelScope.launch {
            repository.rejectMemberApplication(memberId)
            _statusMessage.value = "Application declined & removed."
        }
    }

    fun updateMemberRole(memberId: String, newRole: String) {
        viewModelScope.launch {
            repository.updateMemberRole(memberId, newRole)
            if (_authUser.value?.id == memberId) {
                _authUser.value = _authUser.value?.copy(role = newRole)
                _currentRole.value = if (newRole == "COACH") UserRole.COACH else UserRole.MEMBER
            }
            _statusMessage.value = if (newRole == "COACH") {
                "Member promoted to COACH! Staff privileges unlocked."
            } else {
                "Role set to MEMBER. Athlete will use Member Portal."
            }
        }
    }
}

class GymViewModelFactory(private val repository: GymRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GymViewModel::class.java)) {
            return GymViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
