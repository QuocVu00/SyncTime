package com.example.synctime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.synctime.data.local.dao.*
import com.example.synctime.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        BranchEntity::class,
        ShiftEntity::class,
        ScheduleEntity::class,
        AttendanceEntity::class,
        RequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun branchDao(): BranchDao

    abstract fun shiftDao(): ShiftDao

    abstract fun scheduleDao(): ScheduleDao

    abstract fun attendanceDao(): AttendanceDao

    abstract fun requestDao(): RequestDao
}
