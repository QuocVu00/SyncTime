package com.example.synctime.data.repository

import com.example.synctime.data.api.ManagerAdminApi
import com.example.synctime.data.local.dao.AttendanceDao
import com.example.synctime.data.local.entity.AttendanceEntity
import com.example.synctime.data.model.AttendanceDto
import com.example.synctime.data.model.SalaryDto
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(
    private val attendanceDao: AttendanceDao,
    private val managerAdminApi: ManagerAdminApi
) {
    // --- LOCAL DATABASE (Room) ---
    val allAttendances: Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendances()

    suspend fun insertLocal(attendance: AttendanceEntity) = attendanceDao.insert(attendance)

    suspend fun updateLocal(attendance: AttendanceEntity) = attendanceDao.update(attendance)

    fun getAttendancesByUserId(userId: Int) = attendanceDao.getAttendancesByUserId(userId)

    /**
     * Lấy báo cáo lương từ database cục bộ
     */
    fun getLocalSalaryReport(): Flow<List<SalaryDto>> = attendanceDao.getSalaryReport()

    // --- REMOTE API ---

    /**
     * Lấy danh sách chấm công từ Server cho Manager
     */
    suspend fun fetchManagerAttendance(): Result<List<AttendanceDto>?> {
        return try {
            val response = managerAdminApi.getManagerAttendance()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Lỗi lấy danh sách chấm công"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy báo cáo chấm công (cho Admin)
     */
    suspend fun fetchAttendanceReport(): Result<List<AttendanceDto>?> {
        return try {
            val response = managerAdminApi.getAttendanceReport()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Lỗi lấy báo cáo chấm công"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy báo cáo lương (cho Admin) từ Server
     */
    suspend fun fetchSalaryReport(): Result<List<SalaryDto>?> {
        return try {
            val response = managerAdminApi.getSalaryReport()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Lỗi lấy báo cáo lương"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
