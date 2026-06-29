package com.example.synctime.data.repository

import com.example.synctime.data.local.dao.UserDao
import com.example.synctime.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Lớp Repository đóng vai trò là nguồn dữ liệu duy nhất cho UI/ViewModel.
 * Nó quản lý việc truy cập dữ liệu từ local database thông qua DAO.
 */
class UserRepository(
    private val userDao: UserDao
) {
    // Luồng dữ liệu tự động cập nhật khi DB thay đổi
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    // Chèn người dùng mới vào DB
    suspend fun insert(user: UserEntity) = userDao.insert(user)

    // Cập nhật thông tin người dùng
    suspend fun update(user: UserEntity) = userDao.update(user)

    // Xóa người dùng
    suspend fun delete(user: UserEntity) = userDao.delete(user)

    // Tìm kiếm theo ID
    suspend fun getUserById(id: Int) = userDao.getUserById(id)

    // Tìm kiếm theo Email
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)

    // Xóa toàn bộ dữ liệu
    suspend fun deleteAll() = userDao.deleteAll()
}
