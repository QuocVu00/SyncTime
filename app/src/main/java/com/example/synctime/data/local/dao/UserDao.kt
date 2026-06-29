package com.example.synctime.data.local.dao

import androidx.room.*
import com.example.synctime.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Thêm hoặc cập nhật người dùng (nếu trùng ID sẽ ghi đè)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    // Cập nhật thông tin người dùng
    @Update
    suspend fun update(user: UserEntity)

    // Xóa người dùng
    @Delete
    suspend fun delete(user: UserEntity)

    // Lấy toàn bộ danh sách người dùng dưới dạng luồng dữ liệu (Flow)
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    // Tìm người dùng theo ID
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?

    // Tìm người dùng theo Email (dùng khi Đăng nhập)
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Xóa sạch dữ liệu bảng users
    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
