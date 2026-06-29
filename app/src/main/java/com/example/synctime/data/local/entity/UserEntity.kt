package com.example.synctime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users", // Tên bảng trong database
    foreignKeys = [
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.CASCADE // Nếu chi nhánh bị xóa, người dùng thuộc chi nhánh đó cũng bị xóa
        )
    ],
    indices = [
        Index(value = ["email"], unique = true), // Email không được trùng
        Index(value = ["device_id"], unique = true), // Mỗi thiết bị chỉ cho 1 tài khoản
        Index(value = ["branch_id"]) // Tăng tốc độ tìm kiếm theo chi nhánh
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    val email: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    val role: String, // Vai trò: ADMIN, MANAGER, STAFF

    @ColumnInfo(name = "branch_id")
    val branchId: Int, // ID chi nhánh làm việc

    @ColumnInfo(name = "base_salary")
    val baseSalary: Double, // Lương cơ bản

    @ColumnInfo(name = "device_id")
    val deviceId: String?, // ID thiết bị dùng để chấm công

    val status: String, // Trạng thái: ACTIVE (đang làm), INACTIVE (đã nghỉ)

    @ColumnInfo(name = "created_at")
    val createdAt: Long // Ngày tạo tài khoản
)
