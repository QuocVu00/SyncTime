package com.example.synctime.data.local.dao

import androidx.room.*
import com.example.synctime.data.local.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity)

    @Update
    suspend fun update(schedule: ScheduleEntity)

    @Delete
    suspend fun delete(schedule: ScheduleEntity)

    @Query("SELECT * FROM schedules")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE user_id = :userId")
    fun getSchedulesByUserId(userId: Int): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE work_date = :date")
    fun getSchedulesByDate(date: String): Flow<List<ScheduleEntity>>
}
