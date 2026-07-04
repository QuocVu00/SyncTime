package com.example.synctime.data.repository

import com.example.synctime.data.api.ManagerAdminApi
import com.example.synctime.data.local.dao.RequestDao
import com.example.synctime.data.local.entity.RequestEntity
import com.example.synctime.data.model.RequestDto
import kotlinx.coroutines.flow.Flow

class RequestRepository(
    private val requestDao: RequestDao,
    private val managerAdminApi: ManagerAdminApi
) {
    // --- LOCAL DATABASE (Room) ---
    // Luồng dữ liệu các yêu cầu từ database cục bộ
    val allRequests: Flow<List<RequestEntity>> = requestDao.getAllRequests()

    suspend fun insertLocal(request: RequestEntity) = requestDao.insert(request)

    suspend fun updateLocal(request: RequestEntity) = requestDao.update(request)

    fun getRequestsByUserId(userId: Int) = requestDao.getRequestsByUserId(userId)

    fun getRequestsByStatus(status: String) = requestDao.getRequestsByStatus(status)


    // --- REMOTE API (Chuẩn hóa Phần 2) ---

    /**
     * Lấy danh sách yêu cầu (nghỉ phép, đổi ca...) từ Server (Dành cho Manager)
     */
    suspend fun fetchRequestsRemote(): Result<List<RequestDto>?> {
        return try {
            val response = managerAdminApi.getRequests()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                // Kiểm tra success: true/false từ Server
                if (apiResponse?.success == true) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Lỗi lấy danh sách yêu cầu"))
                }
            } else {
                Result.failure(Exception("Lỗi kết nối: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Chấp nhận yêu cầu (Approve)
     */
    suspend fun approveRequestRemote(id: Int): Result<String?> {
        return try {
            val response = managerAdminApi.approveRequest(id)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    Result.success(apiResponse.message)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Duyệt yêu cầu thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi hệ thống: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Từ chối yêu cầu (Reject)
     */
    suspend fun rejectRequestRemote(id: Int): Result<String?> {
        return try {
            val response = managerAdminApi.rejectRequest(id)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    Result.success(apiResponse.message)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Từ chối yêu cầu thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi hệ thống: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}