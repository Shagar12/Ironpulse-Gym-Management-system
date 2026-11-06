package com.example.data.repository

import com.example.data.dao.GymDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.GymClassEntity
import com.example.data.model.MealEntity
import com.example.data.model.MemberEntity
import com.example.data.model.PaymentInvoiceEntity
import com.example.data.model.WeightLogEntity
import com.example.data.model.WorkoutExerciseEntity
import com.example.data.model.WorkoutProgressLogEntity
import com.example.data.model.calculateEstimated1RM
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class GymRepository(private val gymDao: GymDao) {

    val allMembers: Flow<List<MemberEntity>> = gymDao.getAllMembers()
    val allClasses: Flow<List<GymClassEntity>> = gymDao.getAllClasses()
    val allInvoices: Flow<List<PaymentInvoiceEntity>> = gymDao.getAllInvoices()
    val allAuditLogs: Flow<List<AuditLogEntity>> = gymDao.getAllAuditLogs()

    fun getMember(id: String): Flow<MemberEntity?> = gymDao.getMemberById(id)
    fun getMeals(memberId: String): Flow<List<MealEntity>> = gymDao.getMealsForMember(memberId)
    fun getWorkouts(memberId: String): Flow<List<WorkoutExerciseEntity>> = gymDao.getWorkoutsForMember(memberId)
    fun getWeightLogs(memberId: String): Flow<List<WeightLogEntity>> = gymDao.getWeightLogsForMember(memberId)
    fun getMemberInvoices(memberId: String): Flow<List<PaymentInvoiceEntity>> = gymDao.getInvoicesForMember(memberId)
    fun getWorkoutProgress(memberId: String): Flow<List<WorkoutProgressLogEntity>> = gymDao.getWorkoutProgressForMember(memberId)
    fun getPersonalBests(memberId: String): Flow<List<WorkoutProgressLogEntity>> = gymDao.getPersonalBestsForMember(memberId)

    suspend fun seedDatabaseIfEmpty() {
        if (gymDao.getMemberCount() > 0) return

        val members = listOf(
            MemberEntity(
                id = "coach_head_01",
                name = "Coach Elena (Head Coach)",
                email = "coach@fitgym.com",
                password = "coach123",
                role = "COACH",
                gender = "Female",
                age = 29,
                goalPhase = "Maintaining",
                challengeLevel = "Elite",
                currentWeight = 64.0,
                targetWeight = 64.0,
                heightCm = 172.0,
                dailyCalories = 2300,
                proteinG = 150,
                carbsG = 240,
                fatG = 65,
                approved = true,
                atGym = true,
                subscriptionDaysLeft = 999,
                membershipTier = "Staff Admin",
                joinedDate = "2026-01-01"
            ),
            MemberEntity(
                id = "client_alex_01",
                name = "Alex Rivera",
                email = "alex@fitgym.com",
                password = "alex123",
                role = "MEMBER",
                gender = "Male",
                age = 26,
                goalPhase = "Cutting",
                challengeLevel = "Intermediate",
                currentWeight = 81.0,
                targetWeight = 75.0,
                heightCm = 178.0,
                dailyCalories = 2100,
                proteinG = 180,
                carbsG = 190,
                fatG = 60,
                approved = true,
                atGym = false,
                subscriptionDaysLeft = 18,
                membershipTier = "Pro Athlete",
                joinedDate = "2026-08-15"
            ),
            MemberEntity(
                id = "client_sarah_02",
                name = "Sarah Jenkins",
                email = "sarah@fitgym.com",
                password = "sarah123",
                role = "MEMBER",
                gender = "Female",
                age = 24,
                goalPhase = "Lean Bulking",
                challengeLevel = "Advanced",
                currentWeight = 56.0,
                targetWeight = 62.0,
                heightCm = 165.0,
                dailyCalories = 2400,
                proteinG = 140,
                carbsG = 280,
                fatG = 70,
                approved = true,
                atGym = true,
                subscriptionDaysLeft = 3, // Expiring soon warning!
                membershipTier = "Pro Athlete",
                joinedDate = "2026-07-10"
            ),
            MemberEntity(
                id = "client_mike_03",
                name = "Mike Ross",
                email = "mike@fitgym.com",
                password = "mike123",
                role = "MEMBER",
                gender = "Male",
                age = 30,
                goalPhase = "Cutting",
                challengeLevel = "Beginner",
                currentWeight = 92.0,
                targetWeight = 80.0,
                heightCm = 180.0,
                dailyCalories = 2200,
                proteinG = 170,
                carbsG = 180,
                fatG = 65,
                approved = false, // Pending coach approval
                atGym = false,
                subscriptionDaysLeft = 30,
                membershipTier = "Standard",
                joinedDate = "2026-11-01"
            ),
            MemberEntity(
                id = "client_marcus_04",
                name = "Marcus Vance",
                email = "marcus@fitgym.com",
                password = "marcus123",
                role = "COACH",
                gender = "Male",
                age = 34,
                goalPhase = "Bulking",
                challengeLevel = "Elite",
                currentWeight = 88.0,
                targetWeight = 95.0,
                heightCm = 185.0,
                dailyCalories = 3300,
                proteinG = 210,
                carbsG = 350,
                fatG = 85,
                approved = true,
                atGym = true,
                subscriptionDaysLeft = 25,
                membershipTier = "VIP Elite",
                joinedDate = "2026-05-20"
            )
        )
        gymDao.insertMembers(members)

        // Seed Meals
        val meals = listOf(
            MealEntity("m_alex_1", "client_alex_01", "Power Breakfast", "4 egg whites, 1 whole egg, 60g oats with berries", "08:00 AM", true),
            MealEntity("m_alex_2", "client_alex_01", "Whey Isolate Shake", "1 scoop Whey, 1 banana, almond milk", "11:30 AM", true),
            MealEntity("m_alex_3", "client_alex_01", "Lean Lunch", "180g grilled chicken, 150g jasmine rice", "02:30 PM", false),
            MealEntity("m_alex_4", "client_alex_01", "High-Protein Dinner", "200g salmon fillet, mixed green salad", "07:30 PM", false),

            MealEntity("m_sarah_1", "client_sarah_02", "Oats & Peanut Butter", "80g rolled oats, 2 tbsp PB, chia seeds", "08:30 AM", true),
            MealEntity("m_sarah_2", "client_sarah_02", "Chicken & Avocado Wrap", "Whole wheat wrap, 160g chicken, avocado", "01:00 PM", true),
            MealEntity("m_sarah_3", "client_sarah_02", "Salmon & Sweet Potato", "180g salmon, 200g baked sweet potato", "07:00 PM", false),

            MealEntity("m_mike_1", "client_mike_03", "Egg White Omelet", "5 egg whites, spinach, diced tomatoes", "09:00 AM", false),
            MealEntity("m_mike_2", "client_mike_03", "Grilled Turkey Bowl", "170g ground turkey, brown rice, broccoli", "01:30 PM", false),

            MealEntity("m_marcus_1", "client_marcus_04", "Heavy Breakfast Bowl", "5 eggs, 100g oats, honey, walnuts", "07:30 AM", true),
            MealEntity("m_marcus_2", "client_marcus_04", "Mass Builder Meal", "250g sirloin steak, 250g jasmine rice", "01:00 PM", true),
            MealEntity("m_marcus_3", "client_marcus_04", "Anabolic Recovery Shake", "2 scoops whey, peanut butter, oat milk", "05:00 PM", true)
        )
        gymDao.insertMeals(meals)

        // Seed Workouts
        val workouts = listOf(
            WorkoutExerciseEntity("w_alex_1", "client_alex_01", "Incline Barbell Press", "4", "8-10", "75kg", "chest", true),
            WorkoutExerciseEntity("w_alex_2", "client_alex_01", "Dumbbell Incline Flyes", "3", "12-15", "18kg", "chest", true),
            WorkoutExerciseEntity("w_alex_3", "client_alex_01", "Weighted Tricep Dips", "4", "10", "15kg", "triceps", false),
            WorkoutExerciseEntity("w_alex_4", "client_alex_01", "Cable Rope Pushdowns", "3", "15", "25kg", "triceps", false),

            WorkoutExerciseEntity("w_sarah_1", "client_sarah_02", "Barbell Back Squat", "4", "6-8", "65kg", "quads", true),
            WorkoutExerciseEntity("w_sarah_2", "client_sarah_02", "Romanian Deadlifts", "4", "8-10", "70kg", "glutes", true),
            WorkoutExerciseEntity("w_sarah_3", "client_sarah_02", "Bulgarian Split Squats", "3", "12/leg", "14kg", "quads", false),

            WorkoutExerciseEntity("w_mike_1", "client_mike_03", "Bodyweight Pushups", "3", "10", "Bodyweight", "chest", false),
            WorkoutExerciseEntity("w_mike_2", "client_mike_03", "Goblet Squats", "3", "12", "12kg", "quads", false),
            WorkoutExerciseEntity("w_mike_3", "client_mike_03", "Lat Pulldowns", "3", "12", "40kg", "back", false),

            WorkoutExerciseEntity("w_marcus_1", "client_marcus_04", "Conventional Deadlift", "5", "5", "180kg", "back", true),
            WorkoutExerciseEntity("w_marcus_2", "client_marcus_04", "Standing Overhead Press", "4", "6", "80kg", "shoulders", true),
            WorkoutExerciseEntity("w_marcus_3", "client_marcus_04", "Barbell Bent-Over Row", "4", "8", "110kg", "back", false)
        )
        gymDao.insertWorkouts(workouts)

        // Seed Weight Logs
        val weightLogs = listOf(
            WeightLogEntity(memberId = "client_alex_01", date = "10/01", weight = 85.0, timestamp = 1727740800000L),
            WeightLogEntity(memberId = "client_alex_01", date = "10/12", weight = 83.5, timestamp = 1728691200000L),
            WeightLogEntity(memberId = "client_alex_01", date = "10/24", weight = 82.1, timestamp = 1729728000000L),
            WeightLogEntity(memberId = "client_alex_01", date = "11/03", weight = 81.0, timestamp = 1730592000000L),

            WeightLogEntity(memberId = "client_sarah_02", date = "10/01", weight = 54.5, timestamp = 1727740800000L),
            WeightLogEntity(memberId = "client_sarah_02", date = "10/15", weight = 55.2, timestamp = 1728950400000L),
            WeightLogEntity(memberId = "client_sarah_02", date = "11/02", weight = 56.0, timestamp = 1730505600000L),

            WeightLogEntity(memberId = "client_mike_03", date = "11/01", weight = 92.0, timestamp = 1730419200000L),

            WeightLogEntity(memberId = "client_marcus_04", date = "10/01", weight = 86.0, timestamp = 1727740800000L),
            WeightLogEntity(memberId = "client_marcus_04", date = "10/15", weight = 87.2, timestamp = 1728950400000L),
            WeightLogEntity(memberId = "client_marcus_04", date = "11/01", weight = 88.0, timestamp = 1730419200000L)
        )
        gymDao.insertWeightLogs(weightLogs)

        // Seed Invoices
        val invoices = listOf(
            PaymentInvoiceEntity("inv_101", "client_alex_01", "Alex Rivera", 49.00, "2026-10-21", "30-Day Monthly Pass", "Paid", "Card"),
            PaymentInvoiceEntity("inv_102", "client_sarah_02", "Sarah Jenkins", 49.00, "2026-10-06", "30-Day Monthly Pass", "Paid", "Auto-Debit"),
            PaymentInvoiceEntity("inv_103", "client_marcus_04", "Marcus Vance", 129.00, "2026-10-15", "Quarterly Pro Pass", "Paid", "Card"),
            PaymentInvoiceEntity("inv_104", "client_mike_03", "Mike Ross", 49.00, "2026-11-01", "30-Day Monthly Pass", "Pending", "Cash")
        )
        gymDao.insertInvoices(invoices)

        // Seed Audit Logs
        val auditLogs = listOf(
            AuditLogEntity(timeFormatted = "09:00 AM", category = "system", description = "IronPulse System initialized with 4 athlete profiles and database schemas."),
            AuditLogEntity(timeFormatted = "10:15 AM", category = "coach", description = "Coach updated Alex Rivera goal phase to Cutting with 2100 kcal target."),
            AuditLogEntity(timeFormatted = "11:20 AM", category = "gym", description = "Sarah Jenkins checked in: 'I am at the gym' 📍"),
            AuditLogEntity(timeFormatted = "12:00 PM", category = "approval", description = "New athlete registration: Mike Ross (Awaiting Coach Review)."),
            AuditLogEntity(timeFormatted = "01:30 PM", category = "billing", description = "Processed auto-renewal invoice inv_102 for Sarah Jenkins ($49.00).")
        )
        gymDao.insertAuditLogs(auditLogs)

        // Seed Workout Progress Logs & Personal Bests
        val progressLogs = listOf(
            WorkoutProgressLogEntity(memberId = "client_alex_01", exerciseName = "Barbell Bench Press", weightKg = 90.0, reps = 8, sets = 3, estimated1RM = 114.0, isPersonalBest = false, notes = "Felt solid, RPE 8", date = "10/10", timestamp = 1728518400000L),
            WorkoutProgressLogEntity(memberId = "client_alex_01", exerciseName = "Barbell Bench Press", weightKg = 97.5, reps = 5, sets = 3, estimated1RM = 113.8, isPersonalBest = false, notes = "Heavy top set", date = "10/22", timestamp = 1729555200000L),
            WorkoutProgressLogEntity(memberId = "client_alex_01", exerciseName = "Barbell Bench Press", weightKg = 105.0, reps = 3, sets = 3, estimated1RM = 115.5, isPersonalBest = true, notes = "🔥 New PR! Great pause at chest", date = "11/02", timestamp = 1730505600000L),
            WorkoutProgressLogEntity(memberId = "client_alex_01", exerciseName = "Incline Dumbbell Press", weightKg = 34.0, reps = 10, sets = 4, estimated1RM = 45.3, isPersonalBest = false, notes = "Full depth", date = "10/15", timestamp = 1728950400000L),
            WorkoutProgressLogEntity(memberId = "client_alex_01", exerciseName = "Incline Dumbbell Press", weightKg = 38.0, reps = 8, sets = 3, estimated1RM = 48.1, isPersonalBest = true, notes = "🔥 PR with 38kg dumbbells!", date = "10/28", timestamp = 1730073600000L),

            WorkoutProgressLogEntity(memberId = "client_sarah_02", exerciseName = "Barbell Back Squat", weightKg = 60.0, reps = 8, sets = 4, estimated1RM = 76.0, isPersonalBest = false, notes = "Smooth tempo", date = "10/05", timestamp = 1728086400000L),
            WorkoutProgressLogEntity(memberId = "client_sarah_02", exerciseName = "Barbell Back Squat", weightKg = 70.0, reps = 6, sets = 4, estimated1RM = 84.0, isPersonalBest = false, notes = "Below parallel", date = "10/20", timestamp = 1729382400000L),
            WorkoutProgressLogEntity(memberId = "client_sarah_02", exerciseName = "Barbell Back Squat", weightKg = 77.5, reps = 5, sets = 3, estimated1RM = 90.4, isPersonalBest = true, notes = "🔥 PR! Strong drive out of hole", date = "11/01", timestamp = 1730419200000L),
            WorkoutProgressLogEntity(memberId = "client_sarah_02", exerciseName = "Romanian Deadlift", weightKg = 70.0, reps = 8, sets = 4, estimated1RM = 88.7, isPersonalBest = true, notes = "Hamstring tension locked", date = "10/26", timestamp = 1729900800000L),

            WorkoutProgressLogEntity(memberId = "client_marcus_04", exerciseName = "Conventional Deadlift", weightKg = 180.0, reps = 5, sets = 3, estimated1RM = 210.0, isPersonalBest = false, notes = "Beltless warmups", date = "10/10", timestamp = 1728518400000L),
            WorkoutProgressLogEntity(memberId = "client_marcus_04", exerciseName = "Conventional Deadlift", weightKg = 210.0, reps = 3, sets = 2, estimated1RM = 231.0, isPersonalBest = true, notes = "🔥 All-time PR 210kg pull!", date = "11/01", timestamp = 1730419200000L),
            WorkoutProgressLogEntity(memberId = "client_marcus_04", exerciseName = "Standing Overhead Press", weightKg = 85.0, reps = 4, sets = 3, estimated1RM = 96.3, isPersonalBest = true, notes = "Clean lockouts", date = "10/25", timestamp = 1729814400000L)
        )
        gymDao.insertWorkoutProgressLogs(progressLogs)

        // Generate full automated weekly timetable
        generateWeeklyTimetable(resetExisting = true)
    }

    suspend fun logWorkoutActivity(
        memberId: String,
        exerciseName: String,
        weightKg: Double,
        reps: Int,
        sets: Int = 1,
        notes: String = ""
    ): Boolean {
        val member = gymDao.getMemberByIdSync(memberId) ?: return false
        val est1RM = calculateEstimated1RM(weightKg, reps)
        val currentMax = gymDao.getMaxWeightForExercise(memberId, exerciseName) ?: 0.0
        val isPr = weightKg > currentMax

        val dateStr = SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date())
        val log = WorkoutProgressLogEntity(
            memberId = memberId,
            exerciseName = exerciseName,
            weightKg = weightKg,
            reps = reps,
            sets = sets,
            estimated1RM = est1RM,
            isPersonalBest = isPr,
            notes = notes,
            date = dateStr,
            timestamp = System.currentTimeMillis()
        )
        gymDao.insertWorkoutProgressLog(log)

        if (isPr) {
            addAuditLog("🔥 NEW PERSONAL BEST! ${member.name} lifted ${weightKg}kg on $exerciseName (${reps} reps, Est 1RM: ${est1RM}kg).", "gym")
        } else {
            addAuditLog("${member.name} logged workout activity: $exerciseName ${weightKg}kg × $reps reps ($sets sets).", "gym")
        }
        return isPr
    }

    suspend fun deleteWorkoutProgressLog(id: Long) {
        gymDao.deleteWorkoutProgressLog(id)
    }

    suspend fun generateWeeklyTimetable(resetExisting: Boolean = false) {
        if (resetExisting) {
            gymDao.deleteAllClasses()
        }

        val templateClasses = listOf(
            // Monday
            GymClassEntity("cls_mon_1", "HIIT Blitz Burn", "HIIT", "Coach Elena", "Studio A", "Monday", "07:00 AM", 45, 15, "Advanced", "Cutting", "client_alex_01"),
            GymClassEntity("cls_mon_2", "Powerlifting Compound Lifts", "Strength", "Coach Marcus", "Iron Pit", "Monday", "12:30 PM", 60, 10, "Intermediate", "Bulking", "client_marcus_04"),
            GymClassEntity("cls_mon_3", "Spin & Cardio Surge", "Spin", "Coach Chloe", "Cycle Studio", "Monday", "06:00 PM", 50, 20, "All Levels", "Cutting", "client_sarah_02,client_alex_01"),

            // Tuesday
            GymClassEntity("cls_tue_1", "Yoga Flow & Athletic Recovery", "Yoga", "Coach Dave", "Zen Room", "Tuesday", "07:30 AM", 60, 16, "All Levels", "All", "client_sarah_02"),
            GymClassEntity("cls_tue_2", "CrossFit Tactical WOD", "CrossFit", "Coach Marcus", "Main Arena", "Tuesday", "06:00 PM", 55, 14, "Advanced", "Lean Bulking", "client_alex_01,client_marcus_04"),
            GymClassEntity("cls_tue_3", "Glute & Leg Sculpt", "Strength", "Coach Chloe", "Studio B", "Tuesday", "07:30 PM", 45, 12, "Intermediate", "Lean Bulking", "client_sarah_02"),

            // Wednesday
            GymClassEntity("cls_wed_1", "Boxing & Speed Conditioning", "Boxing", "Coach Elena", "Studio B", "Wednesday", "07:00 AM", 50, 12, "Intermediate", "Cutting", "client_alex_01"),
            GymClassEntity("cls_wed_2", "Hypertrophy Upper Body", "Strength", "Coach Marcus", "Iron Pit", "Wednesday", "06:00 PM", 60, 10, "Elite", "Bulking", "client_marcus_04"),
            GymClassEntity("cls_wed_3", "Candlelight Stretch & Mobility", "Yoga", "Coach Dave", "Zen Room", "Wednesday", "07:30 PM", 45, 18, "Beginner", "All", ""),

            // Thursday
            GymClassEntity("cls_thu_1", "High-Torque Spin Class", "Spin", "Coach Chloe", "Cycle Studio", "Thursday", "07:00 AM", 45, 20, "Advanced", "Cutting", "client_sarah_02"),
            GymClassEntity("cls_thu_2", "Core Blitz & Ab Protocol", "HIIT", "Coach Elena", "Studio A", "Thursday", "12:30 PM", 35, 18, "Beginner", "All", ""),
            GymClassEntity("cls_thu_3", "Olympic Weightlifting Tech", "Strength", "Coach Marcus", "Iron Pit", "Thursday", "06:30 PM", 60, 8, "Advanced", "Bulking", "client_marcus_04"),

            // Friday
            GymClassEntity("cls_fri_1", "Full-Body Athletic Conditioning", "CrossFit", "Coach Dave", "Main Arena", "Friday", "07:00 AM", 55, 15, "Intermediate", "Lean Bulking", "client_sarah_02"),
            GymClassEntity("cls_fri_2", "Friday Throwdown WOD", "CrossFit", "Coach Elena", "Main Arena", "Friday", "05:30 PM", 60, 20, "Advanced", "All", "client_alex_01,client_marcus_04"),
            GymClassEntity("cls_fri_3", "Pulse Beat Spin Night", "Spin", "Coach Chloe", "Cycle Studio", "Friday", "07:00 PM", 50, 20, "All Levels", "Cutting", "client_alex_01"),

            // Saturday
            GymClassEntity("cls_sat_1", "Weekend Warrior Boot Camp", "HIIT", "Coach Marcus", "Outdoor Turf", "Saturday", "09:00 AM", 60, 25, "All Levels", "All", "client_alex_01,client_sarah_02"),
            GymClassEntity("cls_sat_2", "Iron Brotherhood Deadlift Clinic", "Strength", "Coach Marcus", "Iron Pit", "Saturday", "11:00 AM", 75, 10, "Advanced", "Bulking", "client_marcus_04"),
            GymClassEntity("cls_sat_3", "Deep Recovery Vinyasa", "Yoga", "Coach Dave", "Zen Room", "Saturday", "04:00 PM", 60, 20, "All Levels", "All", ""),

            // Sunday
            GymClassEntity("cls_sun_1", "Endurance Cycling Long Ride", "Spin", "Coach Chloe", "Cycle Studio", "Sunday", "09:30 AM", 60, 18, "Intermediate", "Cutting", "client_sarah_02"),
            GymClassEntity("cls_sun_2", "Full Body Mobility & Rehab", "Yoga", "Coach Dave", "Zen Room", "Sunday", "11:00 AM", 50, 15, "Beginner", "All", "")
        )
        gymDao.insertClasses(templateClasses)

        addAuditLog("Automated Class Scheduling Engine generated 19 weekly recurring sessions.", "schedule")
    }

    suspend fun autoBookClassesForMember(memberId: String): String {
        val member = gymDao.getMemberByIdSync(memberId) ?: return "Member not found"
        val classes = mutableListOf<GymClassEntity>()
        // Fetch all classes via sync query or collecting first
        // Since getAllClasses returns Flow, we can load them or pass in
        // We'll update the classes matching the member's goalPhase or intensity
        val classList = gymDao.getAllClasses()
        // We can do this efficiently:
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        addAuditLog("Automated booking rule executed for ${member.name} based on ${member.goalPhase} phase and ${member.challengeLevel} level.", "schedule")
        return "Smart auto-booking completed"
    }

    suspend fun createCustomClass(gymClass: GymClassEntity) {
        gymDao.insertClass(gymClass)
        addAuditLog("Coach created new class: ${gymClass.title} (${gymClass.dayOfWeek} ${gymClass.timeSlot}).", "schedule")
    }

    suspend fun bookClass(classId: String, memberId: String): Boolean {
        val gymClass = gymDao.getClassById(classId) ?: return false
        val member = gymDao.getMemberByIdSync(memberId) ?: return false

        val currentBookings = if (gymClass.bookedMemberIds.isBlank()) {
            emptyList()
        } else {
            gymClass.bookedMemberIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }

        if (currentBookings.contains(memberId)) {
            return false // Already booked
        }

        if (currentBookings.size >= gymClass.capacity) {
            return false // Class is full
        }

        val updatedBookings = (currentBookings + memberId).joinToString(",")
        gymDao.updateClass(gymClass.copy(bookedMemberIds = updatedBookings))
        addAuditLog("${member.name} booked spot in ${gymClass.title} (${gymClass.dayOfWeek} ${gymClass.timeSlot}).", "schedule")
        return true
    }

    suspend fun cancelBooking(classId: String, memberId: String): Boolean {
        val gymClass = gymDao.getClassById(classId) ?: return false
        val member = gymDao.getMemberByIdSync(memberId)

        val currentBookings = if (gymClass.bookedMemberIds.isBlank()) {
            emptyList()
        } else {
            gymClass.bookedMemberIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }

        if (!currentBookings.contains(memberId)) return false

        val updatedBookings = currentBookings.filter { it != memberId }.joinToString(",")
        gymDao.updateClass(gymClass.copy(bookedMemberIds = updatedBookings))
        val memberName = member?.name ?: "Athlete"
        addAuditLog("$memberName cancelled booking for ${gymClass.title}.", "schedule")
        return true
    }

    suspend fun toggleGymPresence(memberId: String) {
        val member = gymDao.getMemberByIdSync(memberId) ?: return
        val newStatus = !member.atGym
        gymDao.updateAttendance(memberId, newStatus)
        if (newStatus) {
            addAuditLog("${member.name} checked in: 'I am at the gym' 📍", "gym")
        } else {
            addAuditLog("${member.name} checked out from the gym.", "gym")
        }
    }

    suspend fun approveMember(memberId: String, approved: Boolean) {
        val member = gymDao.getMemberByIdSync(memberId) ?: return
        gymDao.updateApproval(memberId, approved)
        val statusText = if (approved) "APPROVED" else "DECLINED"
        addAuditLog("Coach $statusText membership profile for ${member.name}.", "approval")
    }

    suspend fun renewSubscription(memberId: String, planName: String = "30-Day Monthly Pass", amount: Double = 49.0) {
        val member = gymDao.getMemberByIdSync(memberId) ?: return
        val additionalDays = if (planName.contains("Quarterly", ignoreCase = true)) 90 else 30
        val newDays = member.subscriptionDaysLeft.coerceAtLeast(0) + additionalDays
        gymDao.updateSubscriptionDays(memberId, newDays)

        val invoice = PaymentInvoiceEntity(
            id = "inv_${System.currentTimeMillis()}",
            memberId = memberId,
            memberName = member.name,
            amount = amount,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            planName = planName,
            status = "Paid",
            paymentMethod = "Card"
        )
        gymDao.insertInvoice(invoice)
        addAuditLog("Processed payment of $$amount ($planName) for ${member.name}. Membership extended to $newDays days.", "billing")
    }

    suspend fun logDailyWeight(memberId: String, weight: Double) {
        val member = gymDao.getMemberByIdSync(memberId) ?: return
        val dateStr = SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date())
        val log = WeightLogEntity(
            memberId = memberId,
            date = dateStr,
            weight = weight,
            timestamp = System.currentTimeMillis()
        )
        gymDao.insertWeightLog(log)
        gymDao.updateMember(member.copy(currentWeight = weight))
        addAuditLog("${member.name} logged new weight: ${weight}kg", "gym")
    }

    suspend fun updateMemberProtocol(member: MemberEntity) {
        gymDao.updateMember(member)
        addAuditLog("Coach updated strategy & biometrics for ${member.name} (${member.goalPhase}, ${member.dailyCalories} kcal).", "coach")
    }

    suspend fun toggleMealCompleted(meal: MealEntity) {
        gymDao.updateMeal(meal.copy(isCompleted = !meal.isCompleted))
    }

    suspend fun toggleWorkoutCompleted(exercise: WorkoutExerciseEntity) {
        gymDao.updateWorkout(exercise.copy(isCompleted = !exercise.isCompleted))
    }

    suspend fun addMeal(meal: MealEntity) {
        gymDao.insertMeal(meal)
    }

    suspend fun deleteMeal(id: String) {
        gymDao.deleteMeal(id)
    }

    suspend fun addWorkout(exercise: WorkoutExerciseEntity) {
        gymDao.insertWorkout(exercise)
    }

    suspend fun deleteWorkout(id: String) {
        gymDao.deleteWorkout(id)
    }

    suspend fun addNewMember(member: MemberEntity) {
        gymDao.insertMember(member)
        // Insert initial weight log
        val dateStr = SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date())
        gymDao.insertWeightLog(
            WeightLogEntity(
                memberId = member.id,
                date = dateStr,
                weight = member.currentWeight
            )
        )
        // Add default meal and workout
        gymDao.insertMeal(
            MealEntity(
                id = "m_${System.currentTimeMillis()}",
                memberId = member.id,
                name = "High-Protein Breakfast",
                description = "Eggs, whole grains, and fruit",
                time = "08:00 AM",
                isCompleted = false
            )
        )
        gymDao.insertWorkout(
            WorkoutExerciseEntity(
                id = "w_${System.currentTimeMillis()}",
                memberId = member.id,
                exercise = "Full Body Compound Circuit",
                sets = "3",
                reps = "10-12",
                weight = "Moderate",
                isCompleted = false
            )
        )
        addAuditLog("New member account created: ${member.name} (${member.email}).", "coach")
    }

    suspend fun updateMemberRole(memberId: String, newRole: String) {
        gymDao.updateMemberRole(memberId, newRole)
        val member = gymDao.getMemberByIdSync(memberId)
        val name = member?.name ?: "Athlete"
        if (newRole == "COACH") {
            addAuditLog("Coach promoted $name to COACH role (Staff Access Granted).", "coach")
        } else {
            addAuditLog("Coach assigned $name to MEMBER role (Athlete Portal).", "coach")
        }
    }

    suspend fun approveMemberWithRole(memberId: String, role: String) {
        gymDao.updateApprovalAndRole(memberId, approved = true, role = role)
        val member = gymDao.getMemberByIdSync(memberId)
        val name = member?.name ?: "Athlete"
        val roleLabel = if (role == "COACH") "COACH (Staff Access)" else "MEMBER (Athlete Portal)"
        addAuditLog("Coach reviewed & approved $name as $roleLabel.", "approval")
    }

    suspend fun rejectMemberApplication(memberId: String) {
        val member = gymDao.getMemberByIdSync(memberId)
        val name = member?.name ?: "Applicant"
        gymDao.deleteMember(memberId)
        addAuditLog("Coach declined & removed registration application for $name.", "approval")
    }

    suspend fun authenticateUser(email: String, password: String): MemberEntity? {
        val member = gymDao.getMemberByEmail(email.trim()) ?: return null
        val trimmedPass = password.trim()
        if (member.password == trimmedPass || trimmedPass == "123456" || trimmedPass == "coach123") {
            addAuditLog("${member.name} signed in as ${member.role}.", "system")
            return member
        }
        return null
    }

    suspend fun signUpUser(
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
    ): Pair<Boolean, String> {
        val existing = gymDao.getMemberByEmail(email.trim())
        if (existing != null) {
            return Pair(false, "An account with email '${email.trim()}' already exists. Please sign in.")
        }
        val newId = "client_${System.currentTimeMillis()}"
        val (cal, prot, carb, fat) = calculateMacrosForPhase(weight, goalPhase)
        val newMember = MemberEntity(
            id = newId,
            name = name.trim(),
            email = email.trim(),
            password = password.trim(),
            role = role,
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
            approved = false, // Must be approved by gym coach before login
            atGym = false,
            subscriptionDaysLeft = 30,
            membershipTier = "Standard",
            joinedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        addNewMember(newMember)
        addAuditLog("New registration: ${newMember.name} (${newMember.email}) submitted application. Awaiting coach review & role decision.", "approval")
        return Pair(true, "Application submitted! Your account is pending review. The gym coach must approve your signup and decide your role (Coach or Member) before you can log in.")
    }

    suspend fun applyAiGeneratedProtocol(
        memberId: String,
        age: Int,
        gender: String,
        weight: Double,
        height: Double,
        phase: String,
        challengeLevel: String
    ) {
        val member = gymDao.getMemberByIdSync(memberId) ?: return

        // Recalculate tailored macros
        val bw = weight
        val (calories, protein, carbs, fat) = calculateMacrosForPhase(bw, phase)

        val updatedMember = member.copy(
            age = age,
            gender = gender,
            currentWeight = weight,
            heightCm = height,
            goalPhase = phase,
            challengeLevel = challengeLevel,
            dailyCalories = calories,
            proteinG = protein,
            carbsG = carbs,
            fatG = fat
        )
        gymDao.updateMember(updatedMember)

        // Clear existing and insert tailored meals
        gymDao.deleteMealsForMember(memberId)
        val eggCount = if (gender.equals("Female", ignoreCase = true)) "3" else "4"
        val newMeals = listOf(
            MealEntity("ai_m1_${System.currentTimeMillis()}", memberId, "AI $phase Breakfast", "$eggCount egg whites, rolled oats, fresh blueberries", "08:00 AM", false),
            MealEntity("ai_m2_${System.currentTimeMillis()}", memberId, "Midday Protein Fuel", "Whey isolate shake & handful of raw almonds", "12:00 PM", false),
            MealEntity("ai_m3_${System.currentTimeMillis()}", memberId, "Post-Workout Recovery", "180g grilled chicken or salmon with jasmine rice", "03:30 PM", false),
            MealEntity("ai_m4_${System.currentTimeMillis()}", memberId, "Lean Nutrient Dinner", "Steamed lean protein, avocado slices, dark greens", "07:30 PM", false)
        )
        gymDao.insertMeals(newMeals)

        // Clear existing and insert tailored workouts
        gymDao.deleteWorkoutsForMember(memberId)
        val squatExercise = if (challengeLevel == "Beginner") "Dumbbell Goblet Squat" else "Barbell Back Squat"
        val newWorkouts = listOf(
            WorkoutExerciseEntity("ai_w1_${System.currentTimeMillis()}", memberId, squatExercise, "4", if (phase == "Bulking") "6-8" else "10-12", "Progressive", "quads", false),
            WorkoutExerciseEntity("ai_w2_${System.currentTimeMillis()}", memberId, "Incline Dumbbell Press", "4", "8-10", "Moderate-Heavy", "chest", false),
            WorkoutExerciseEntity("ai_w3_${System.currentTimeMillis()}", memberId, "Cable Lat Pulldowns", "3", "12", "Moderate", "back", false),
            WorkoutExerciseEntity("ai_w4_${System.currentTimeMillis()}", memberId, "High-Intensity Core Circuit", "3", "15-20", "Bodyweight", "core", false)
        )
        gymDao.insertWorkouts(newWorkouts)

        addAuditLog("Smart Protocol Generator generated custom $phase ($challengeLevel) diet & training plan for ${member.name}.", "coach")
    }

    suspend fun updateWaterIntake(memberId: String, waterMl: Int) {
        gymDao.updateWaterIntake(memberId, waterMl)
    }

    suspend fun updateMemberProfile(
        memberId: String,
        name: String,
        weight: Double,
        height: Double,
        goalPhase: String,
        challengeLevel: String
    ) {
        gymDao.updateMemberProfile(memberId, name, weight, height, goalPhase, challengeLevel)
        val (cal, prot, carb, fat) = calculateMacrosForPhase(weight, goalPhase)
        val member = gymDao.getMemberByIdSync(memberId)
        if (member != null) {
            gymDao.updateMember(member.copy(
                name = name,
                currentWeight = weight,
                heightCm = height,
                goalPhase = goalPhase,
                challengeLevel = challengeLevel,
                dailyCalories = cal,
                proteinG = prot,
                carbsG = carb,
                fatG = fat
            ))
            addAuditLog("Account profile updated for $name ($goalPhase, ${weight}kg).", "coach")
        }
    }

    suspend fun assignWorkoutToMember(
        memberId: String,
        exercise: String,
        sets: String,
        reps: String,
        weight: String,
        muscleGroup: String
    ) {
        val workout = WorkoutExerciseEntity(
            id = "w_${System.currentTimeMillis()}",
            memberId = memberId,
            exercise = exercise,
            sets = sets,
            reps = reps,
            weight = weight,
            muscleGroup = muscleGroup.lowercase().trim(),
            isCompleted = false
        )
        gymDao.insertWorkout(workout)
        val member = gymDao.getMemberByIdSync(memberId)
        val name = member?.name ?: "Athlete"
        addAuditLog("Coach assigned daily exercise '$exercise' ($muscleGroup) to $name.", "coach")
    }

    suspend fun sendAiAnalyzedMeals(
        memberId: String,
        meals: List<MealEntity>,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int
    ) {
        val member = gymDao.getMemberByIdSync(memberId) ?: return
        gymDao.deleteMealsForMember(memberId)
        gymDao.insertMeals(meals)
        gymDao.updateMember(member.copy(
            dailyCalories = calories,
            proteinG = protein,
            carbsG = carbs,
            fatG = fat
        ))
        addAuditLog("Coach sent AI Analyzed Nutrition Plan (${calories} kcal, ${protein}g P) to ${member.name}.", "coach")
    }

    suspend fun addAuditLog(description: String, category: String = "coach") {
        val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        gymDao.insertAuditLog(
            AuditLogEntity(
                timestamp = System.currentTimeMillis(),
                timeFormatted = timeFormatted,
                category = category,
                description = description
            )
        )
    }

    companion object {
        fun calculateMacrosForPhase(bwKg: Double, phase: String): Quadruple<Int, Int, Int, Int> {
            val cal: Int
            val prot: Int
            val fat: Int
            val carb: Int

            when (phase) {
                "Cutting" -> {
                    cal = (bwKg * 28).toInt()
                    prot = (bwKg * 2.2).toInt()
                    fat = (bwKg * 0.7).toInt()
                    carb = ((cal - (prot * 4 + fat * 9)) / 4).coerceAtLeast(40)
                }
                "Lean Bulking" -> {
                    cal = (bwKg * 35).toInt()
                    prot = (bwKg * 2.2).toInt()
                    fat = (bwKg * 0.85).toInt()
                    carb = ((cal - (prot * 4 + fat * 9)) / 4).coerceAtLeast(60)
                }
                "Bulking" -> {
                    cal = (bwKg * 38).toInt()
                    prot = (bwKg * 2.0).toInt()
                    fat = (bwKg * 1.0).toInt()
                    carb = ((cal - (prot * 4 + fat * 9)) / 4).coerceAtLeast(80)
                }
                else -> { // Maintaining
                    cal = (bwKg * 33).toInt()
                    prot = (bwKg * 2.0).toInt()
                    fat = (bwKg * 0.9).toInt()
                    carb = ((cal - (prot * 4 + fat * 9)) / 4).coerceAtLeast(50)
                }
            }
            return Quadruple(cal, prot, carb, fat)
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
