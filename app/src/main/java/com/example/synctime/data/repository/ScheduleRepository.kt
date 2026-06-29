package com.example.synctime.data.repository

import com.example.synctime.data.local.dao.ScheduleDao
import com.example.synctime.data.local.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(
    private val scheduleDao: ScheduleDao
) {
    val allSchedules: Flow<List<ScheduleEntity>> = scheduleDao.getAllSchedules()

    suspend fun insert(schedule: ScheduleEntity) = scheduleDao.insert(schedule)

    suspend fun update(schedule: ScheduleEntity) = scheduleDao.update(schedule)

    suspend fun delete(schedule: ScheduleEntity) = scheduleDao.delete(schedule)

    fun getSchedulesByUserId(userId: Int) = scheduleDao.getSchedulesByUserId(userId)

    fun getSchedulesByDate(date: String) = scheduleDao.getSchedulesByDate(date)
}
