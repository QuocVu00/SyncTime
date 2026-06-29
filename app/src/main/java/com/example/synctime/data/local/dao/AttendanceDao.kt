package com.example.synctime.data.local.dao

import androidx.room.*
import com.example.synctime.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity)

    @Update
    suspend fun update(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendances")
    fun getAllAttendances(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE user_id = :userId")
    fun getAttendancesByUserId(userId: Int): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE schedule_id = :scheduleId")
    suspend fun getAttendanceByScheduleId(scheduleId: Int): AttendanceEntity?
}
