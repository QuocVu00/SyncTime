package com.example.synctime.data.repository

import com.example.synctime.data.local.dao.ShiftDao
import com.example.synctime.data.local.entity.ShiftEntity
import kotlinx.coroutines.flow.Flow

class ShiftRepository(
    private val shiftDao: ShiftDao
) {
    val allShifts: Flow<List<ShiftEntity>> = shiftDao.getAllShifts()

    suspend fun insert(shift: ShiftEntity) = shiftDao.insert(shift)

    suspend fun update(shift: ShiftEntity) = shiftDao.update(shift)

    suspend fun delete(shift: ShiftEntity) = shiftDao.delete(shift)

    suspend fun getShiftById(id: Int) = shiftDao.getShiftById(id)
}
