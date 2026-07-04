package com.example.synctime.data.repository

import com.example.synctime.data.api.ManagerAdminApi
import com.example.synctime.data.local.dao.BranchDao
import com.example.synctime.data.local.entity.BranchEntity
import com.example.synctime.data.model.BranchDto
import com.example.synctime.data.model.BranchRequest
import kotlinx.coroutines.flow.Flow

class BranchRepository(
    private val branchDao: BranchDao,
    private val managerAdminApi: ManagerAdminApi
) {
    // --- LOCAL DATABASE (Room) ---
    // Luồng dữ liệu chi nhánh từ DB cục bộ
    val allBranches: Flow<List<BranchEntity>> = branchDao.getAllBranches()

    suspend fun insertLocal(branch: BranchEntity) = branchDao.insert(branch)

    suspend fun updateLocal(branch: BranchEntity) = branchDao.update(branch)

    suspend fun deleteLocal(branch: BranchEntity) = branchDao.delete(branch)

    suspend fun getBranchById(id: Int) = branchDao.getBranchById(id)


    // --- REMOTE API (Chuẩn hóa Phần 2) ---

    /**
     * Lấy danh sách chi nhánh từ Server (Dành cho Admin)
     */
    suspend fun fetchBranchesRemote(): Result<List<BranchDto>?> {
        return try {
            val response = managerAdminApi.getBranches()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                // Kiểm tra field success theo chuẩn của nhóm
                if (apiResponse?.success == true) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Lỗi lấy danh sách chi nhánh"))
                }
            } else {
                Result.failure(Exception("Lỗi HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Tạo chi nhánh mới lên Server
     */
    suspend fun createBranchRemote(branchRequest: BranchRequest): Result<String?> {
        return try {
            val response = managerAdminApi.createBranch(branchRequest)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    Result.success(apiResponse.message)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Tạo chi nhánh thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi hệ thống: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cập nhật chi nhánh trên Server
     */
    suspend fun updateBranchRemote(id: Int, branchRequest: BranchRequest): Result<String?> {
        return try {
            val response = managerAdminApi.updateBranch(id, branchRequest)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    Result.success(apiResponse.message)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Cập nhật thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi hệ thống: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}