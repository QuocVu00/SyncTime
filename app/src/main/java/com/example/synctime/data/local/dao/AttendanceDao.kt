package com.example.synctime.data.local.dao

import androidx.room.*
import com.example.synctime.data.local.entity.AttendanceEntity
import com.example.synctime.data.model.SalaryDto
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity)

    @Update
    suspend fun update(attendance: AttendanceEntity)

    @Delete
    suspend fun delete(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance_logs")
    fun getAllAttendances(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_logs WHERE id = :id")
    suspend fun getAttendanceById(id: Int): AttendanceEntity?

    @Query("SELECT * FROM attendance_logs WHERE user_id = :userId")
    fun getAttendancesByUserId(userId: Int): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_logs WHERE schedule_id = :scheduleId")
    suspend fun getAttendanceByScheduleId(scheduleId: Int): AttendanceEntity?

    @Query("""
        SELECT 
            u.id AS userId, 
            u.full_name AS fullName, 
            u.position AS position, 
            ps.position_name AS positionName, 
            ps.hourly_rate AS hourlyRate, 
            IFNULL(SUM(al.total_hours), 0.0) AS totalHours, 
            IFNULL(SUM(al.late_minutes), 0) AS lateMinutes, 
            IFNULL(SUM(al.overtime_minutes), 0) AS overtimeMinutes,
            IFNULL(SUM(al.total_hours), 0.0) * ps.hourly_rate AS salary
        FROM users u
        JOIN position_salaries ps ON u.position = ps.position
        LEFT JOIN attendance_logs al ON u.id = al.user_id
        GROUP BY u.id
    """)
    fun getSalaryReport(): Flow<List<SalaryDto>>

    @Query("DELETE FROM attendance_logs")
    suspend fun deleteAll()
}
