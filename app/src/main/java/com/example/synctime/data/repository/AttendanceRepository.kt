package com.example.synctime.data.repository

import com.example.synctime.data.local.dao.AttendanceDao
import com.example.synctime.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(
    private val attendanceDao: AttendanceDao
) {
    val allAttendances: Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendances()

    suspend fun insert(attendance: AttendanceEntity) = attendanceDao.insert(attendance)

    suspend fun update(attendance: AttendanceEntity) = attendanceDao.update(attendance)

    fun getAttendancesByUserId(userId: Int) = attendanceDao.getAttendancesByUserId(userId)

    suspend fun getAttendanceByScheduleId(scheduleId: Int) = attendanceDao.getAttendanceByScheduleId(scheduleId)
}
