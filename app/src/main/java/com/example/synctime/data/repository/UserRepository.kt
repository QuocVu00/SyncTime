package com.example.synctime.data.repository

import com.example.synctime.data.api.ManagerAdminApi
import com.example.synctime.data.local.dao.UserDao
import com.example.synctime.data.local.entity.UserEntity
import com.example.synctime.data.model.SalaryDto
import com.example.synctime.data.model.StaffDto
import kotlinx.coroutines.flow.Flow

/**
 * Lớp Repository đóng vai trò là nguồn dữ liệu duy nhất cho UI/ViewModel.
 * Quản lý cả Local Database (Room) và Remote API (Retrofit).
 */
class UserRepository(
    private val userDao: UserDao,
    private val managerAdminApi: ManagerAdminApi // Thêm API để làm việc với Server
) {
    // --- LOCAL DATABASE (Room) ---

    // Luồng dữ liệu tự động cập nhật khi DB thay đổi
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun insertLocal(user: UserEntity) = userDao.insert(user)

    suspend fun updateLocal(user: UserEntity) = userDao.update(user)

    suspend fun deleteLocal(user: UserEntity) = userDao.delete(user)

    suspend fun getUserById(id: Int) = userDao.getUserById(id)

    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)

    suspend fun deleteAllLocal() = userDao.deleteAll()


    // --- REMOTE API (Chuẩn hóa Phần 2: success, message, data) ---

    /**
     * Lấy danh sách nhân viên từ Server (Dành cho Manager)
     */
    suspend fun fetchManagerStaffRemote(): Result<List<StaffDto>?> {
        return try {
            val response = managerAdminApi.getManagerStaff()

            if (response.isSuccessful) {
                val apiResponse = response.body()
                // Kiểm tra field 'success' theo chuẩn chung của nhóm
                if (apiResponse?.success == true) {
                    // Trả về dữ liệu nhân viên nằm trong field 'data'
                    Result.success(apiResponse.data)
                } else {
                    // Trả về thông báo lỗi từ Server (ví dụ: "Hết phiên đăng nhập")
                    Result.failure(Exception(apiResponse?.message ?: "Lỗi lấy danh sách nhân viên"))
                }
            } else {
                // Lỗi HTTP (403 Forbidden, 500...)
                Result.failure(Exception("Lỗi kết nối: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy báo cáo lương từ Server (Dành cho Admin)
     */
    suspend fun fetchSalaryReportRemote(): Result<List<SalaryDto>?> {
        return try {
            val response = managerAdminApi.getSalaryReport()

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Lỗi lấy báo cáo lương"))
                }
            } else {
                Result.failure(Exception("Lỗi hệ thống: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}