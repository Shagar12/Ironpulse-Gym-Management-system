package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.GymDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.GymClassEntity
import com.example.data.model.MealEntity
import com.example.data.model.MemberEntity
import com.example.data.model.PaymentInvoiceEntity
import com.example.data.model.WeightLogEntity
import com.example.data.model.WorkoutExerciseEntity
import com.example.data.model.WorkoutProgressLogEntity

@Database(
    entities = [
        MemberEntity::class,
        MealEntity::class,
        WorkoutExerciseEntity::class,
        WeightLogEntity::class,
        GymClassEntity::class,
        PaymentInvoiceEntity::class,
        AuditLogEntity::class,
        WorkoutProgressLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gymDao(): GymDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ironpulse_gym_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
