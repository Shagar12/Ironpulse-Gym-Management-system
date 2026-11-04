package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AuditLogEntity
import com.example.data.model.GymClassEntity
import com.example.data.model.MealEntity
import com.example.data.model.MemberEntity
import com.example.data.model.PaymentInvoiceEntity
import com.example.data.model.WeightLogEntity
import com.example.data.model.WorkoutExerciseEntity
import com.example.data.model.WorkoutProgressLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GymDao {

    // --- Members ---
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    fun getMemberById(id: String): Flow<MemberEntity?>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberByIdSync(id: String): MemberEntity?

    @Query("SELECT COUNT(*) FROM members")
    suspend fun getMemberCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun deleteMember(id: String)

    @Query("UPDATE members SET atGym = :atGym WHERE id = :id")
    suspend fun updateAttendance(id: String, atGym: Boolean)

    @Query("UPDATE members SET approved = :approved WHERE id = :id")
    suspend fun updateApproval(id: String, approved: Boolean)

    @Query("SELECT * FROM members WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getMemberByEmail(email: String): MemberEntity?

    @Query("UPDATE members SET role = :role WHERE id = :id")
    suspend fun updateMemberRole(id: String, role: String)

    @Query("UPDATE members SET approved = :approved, role = :role WHERE id = :id")
    suspend fun updateApprovalAndRole(id: String, approved: Boolean, role: String)

    @Query("UPDATE members SET subscriptionDaysLeft = :daysLeft WHERE id = :id")
    suspend fun updateSubscriptionDays(id: String, daysLeft: Int)

    // --- Meals ---
    @Query("SELECT * FROM meals WHERE memberId = :memberId")
    fun getMealsForMember(memberId: String): Flow<List<MealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealEntity>)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMeal(id: String)

    @Query("DELETE FROM meals WHERE memberId = :memberId")
    suspend fun deleteMealsForMember(memberId: String)

    // --- Workouts ---
    @Query("SELECT * FROM workout_exercises WHERE memberId = :memberId")
    fun getWorkoutsForMember(memberId: String): Flow<List<WorkoutExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(exercise: WorkoutExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(exercises: List<WorkoutExerciseEntity>)

    @Update
    suspend fun updateWorkout(exercise: WorkoutExerciseEntity)

    @Query("DELETE FROM workout_exercises WHERE id = :id")
    suspend fun deleteWorkout(id: String)

    @Query("DELETE FROM workout_exercises WHERE memberId = :memberId")
    suspend fun deleteWorkoutsForMember(memberId: String)

    // --- Weight Logs ---
    @Query("SELECT * FROM weight_logs WHERE memberId = :memberId ORDER BY timestamp ASC")
    fun getWeightLogsForMember(memberId: String): Flow<List<WeightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(log: WeightLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLogs(logs: List<WeightLogEntity>)

    // --- Classes ---
    @Query("SELECT * FROM gym_classes ORDER BY dayOfWeek, timeSlot")
    fun getAllClasses(): Flow<List<GymClassEntity>>

    @Query("SELECT * FROM gym_classes WHERE id = :id LIMIT 1")
    suspend fun getClassById(id: String): GymClassEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(gymClass: GymClassEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<GymClassEntity>)

    @Update
    suspend fun updateClass(gymClass: GymClassEntity)

    @Query("DELETE FROM gym_classes WHERE id = :id")
    suspend fun deleteClass(id: String)

    @Query("DELETE FROM gym_classes")
    suspend fun deleteAllClasses()

    // --- Invoices ---
    @Query("SELECT * FROM payment_invoices ORDER BY date DESC")
    fun getAllInvoices(): Flow<List<PaymentInvoiceEntity>>

    @Query("SELECT * FROM payment_invoices WHERE memberId = :memberId ORDER BY date DESC")
    fun getInvoicesForMember(memberId: String): Flow<List<PaymentInvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: PaymentInvoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<PaymentInvoiceEntity>)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AuditLogEntity>)

    // --- Workout Progress & Personal Bests ---
    @Query("SELECT * FROM workout_progress_logs WHERE memberId = :memberId ORDER BY timestamp DESC")
    fun getWorkoutProgressForMember(memberId: String): Flow<List<WorkoutProgressLogEntity>>

    @Query("SELECT * FROM workout_progress_logs WHERE memberId = :memberId AND isPersonalBest = 1 ORDER BY weightKg DESC")
    fun getPersonalBestsForMember(memberId: String): Flow<List<WorkoutProgressLogEntity>>

    @Query("SELECT MAX(weightKg) FROM workout_progress_logs WHERE memberId = :memberId AND exerciseName = :exerciseName")
    suspend fun getMaxWeightForExercise(memberId: String, exerciseName: String): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutProgressLog(log: WorkoutProgressLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutProgressLogs(logs: List<WorkoutProgressLogEntity>)

    @Query("DELETE FROM workout_progress_logs WHERE id = :id")
    suspend fun deleteWorkoutProgressLog(id: Long)
}

