package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password: String = "123456",
    val role: String = "MEMBER", // "COACH" or "MEMBER"
    val gender: String, // Male, Female, Other
    val age: Int,
    val goalPhase: String, // Cutting, Maintaining, Lean Bulking, Bulking
    val challengeLevel: String, // Beginner, Intermediate, Advanced, Elite
    val currentWeight: Double,
    val targetWeight: Double,
    val heightCm: Double,
    val dailyCalories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val approved: Boolean,
    val atGym: Boolean,
    val subscriptionDaysLeft: Int,
    val waterIntakeMl: Int = 1750,
    val membershipTier: String = "Pro Athlete",
    val joinedDate: String = "2026-10-01"
)

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val memberId: String,
    val name: String,
    val description: String,
    val time: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "workout_exercises")
data class WorkoutExerciseEntity(
    @PrimaryKey val id: String,
    val memberId: String,
    val exercise: String,
    val sets: String,
    val reps: String,
    val weight: String,
    val muscleGroup: String = "chest",
    val isCompleted: Boolean = false
)

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: String,
    val date: String,
    val weight: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "gym_classes")
data class GymClassEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // HIIT, Strength, Spin, Yoga, CrossFit, Boxing
    val instructor: String,
    val room: String,
    val dayOfWeek: String, // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    val timeSlot: String, // "07:00 AM", "12:30 PM", "06:00 PM", "07:30 PM"
    val durationMinutes: Int = 50,
    val capacity: Int = 15,
    val intensity: String = "Intermediate", // Beginner, Intermediate, Advanced, All Levels
    val targetPhase: String = "All", // Cutting, Bulking, Lean Bulking, All
    val bookedMemberIds: String = "", // Comma-separated member IDs
    val isAutomated: Boolean = true
)

@Entity(tableName = "payment_invoices")
data class PaymentInvoiceEntity(
    @PrimaryKey val id: String,
    val memberId: String,
    val memberName: String,
    val amount: Double,
    val date: String,
    val planName: String, // 30-Day Monthly Pass, Quarterly Pro, Annual VIP
    val status: String = "Paid", // Paid, Due, Refunded
    val paymentMethod: String = "Card"
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String,
    val category: String, // system, coach, gym, billing, schedule, approval
    val description: String
)

@Entity(tableName = "workout_progress_logs")
data class WorkoutProgressLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: String,
    val exerciseName: String,
    val weightKg: Double,
    val reps: Int,
    val sets: Int = 1,
    val estimated1RM: Double,
    val isPersonalBest: Boolean = false,
    val notes: String = "",
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun calculateEstimated1RM(weightKg: Double, reps: Int): Double {
    if (reps <= 1) return weightKg
    val raw = weightKg * (1.0 + reps.toDouble() / 30.0)
    return String.format(java.util.Locale.US, "%.1f", raw).toDoubleOrNull() ?: weightKg
}

