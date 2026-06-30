package com.example.synctime.data.repository

import com.example.synctime.data.api.ManagerAdminApi
import com.example.synctime.data.local.dao.AttendanceDao
import com.example.synctime.data.local.entity.AttendanceEntity
import com.example.synctime.data.model.AttendanceDto
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(
    private val attendanceDao: AttendanceDao,
    private val managerAdminApi: ManagerAdminApi // Thêm API vào để lấy dữ liệu từ Server
) {
    // --- LOCAL DATABASE (Room) ---
    val allAttendances: Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendances()

    suspend fun insertLocal(attendance: AttendanceEntity) = attendanceDao.insert(attendance)

    suspend fun updateLocal(attendance: AttendanceEntity) = attendanceDao.update(attendance)

    fun getAttendancesByUserId(userId: Int) = attendanceDao.getAttendancesByUserId(userId)


    // --- REMOTE API (Chuẩn hóa theo Phần 2) ---

    /**
     * Lấy danh sách chấm công từ Server cho Manager
     */
    suspend fun fetchManagerAttendance(): Result<List<AttendanceDto>?> {
        return try {
            val response = managerAdminApi.getManagerAttendance()

            if (response.isSuccessful) {
                val apiResponse = response.body()
                // Kiểm tra field 'success' theo chuẩn chung của nhóm
                if (apiResponse?.success == true) {
                    // Trả về data (List<AttendanceDto>) nếu thành công
                    Result.success(apiResponse.data)
                } else {
                    // Trả về message lỗi từ server (ví dụ: "Không có quyền")
                    Result.failure(Exception(apiResponse?.message ?: "Lỗi logic từ Server"))
                }
            } else {
                // Lỗi HTTP (404, 500...)
                Result.failure(Exception("Lỗi kết nối: ${response.code()}"))
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
                Result.failure(Exception(response.body()?.message ?: "Lỗi lấy báo cáo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}