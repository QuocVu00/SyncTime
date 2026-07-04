package com.example.synctime.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.synctime.data.local.dao.*
import com.example.synctime.data.local.entity.*
import com.example.synctime.data.local.util.Converters

@Database(
    entities = [
        UserEntity::class,
        BranchEntity::class,
        ShiftEntity::class,
        ScheduleEntity::class,
        AttendanceEntity::class,
        RequestEntity::class,
        PositionSalaryEntity::class,
        DeviceEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun branchDao(): BranchDao
    abstract fun shiftDao(): ShiftDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun requestDao(): RequestDao
    abstract fun positionSalaryDao(): PositionSalaryDao
    abstract fun deviceDao(): DeviceDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "synctime_database"
                )
                    .fallbackToDestructiveMigration() // Cảnh báo: Xóa sạch data khi nâng cấp version nếu không có Migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
