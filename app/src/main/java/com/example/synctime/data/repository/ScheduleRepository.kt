package com.example.synctime.data.repository

import com.example.synctime.data.api.ManagerAdminApi
import com.example.synctime.data.local.dao.ScheduleDao
import com.example.synctime.data.local.entity.ScheduleEntity
import com.example.synctime.data.model.CreateScheduleRequest
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(
    private val scheduleDao: ScheduleDao,
    private val managerAdminApi: ManagerAdminApi // Thêm API để làm việc với Server
) {
    // --- LOCAL DATABASE (Room) ---
    val allSchedules: Flow<List<ScheduleEntity>> = scheduleDao.getAllSchedules()

    suspend fun insertLocal(schedule: ScheduleEntity) = scheduleDao.insert(schedule)

    suspend fun updateLocal(schedule: ScheduleEntity) = scheduleDao.update(schedule)

    suspend fun deleteLocal(schedule: ScheduleEntity) = scheduleDao.delete(schedule)

    fun getSchedulesByUserId(userId: Int) = scheduleDao.getSchedulesByUserId(userId)

    fun getSchedulesByDate(date: String) = scheduleDao.getSchedulesByDate(date)


    // --- REMOTE API (Chuẩn hóa Phần 2) ---

    /**
     * Tạo lịch làm việc mới trên Server (Dành cho Manager)
     */
    suspend fun createScheduleRemote(request: CreateScheduleRequest): Result<String> {
        return try {
            val response = managerAdminApi.createSchedule(request)

            if (response.isSuccessful) {
                val apiResponse = response.body()

                // Kiểm tra field 'success' theo chuẩn của nhóm
                if (apiResponse?.success == true) {
                    // Trả về message thành công (ví dụ: "Schedule created successfully")
                    Result.success(apiResponse.message)
                } else {
                    // Trả về message lỗi từ server (ví dụ: "Staff already has a schedule for this day")
                    Result.failure(Exception(apiResponse?.message ?: "Lỗi tạo lịch làm việc"))
                }
            } else {
                // Lỗi HTTP (400, 401, 500...)
                Result.failure(Exception("Lỗi kết nối Server: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Lỗi mạng hoặc lỗi code
            Result.failure(e)
        }
    }
}