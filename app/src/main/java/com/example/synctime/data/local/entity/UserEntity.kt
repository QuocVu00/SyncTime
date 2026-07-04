package com.example.synctime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

import com.example.synctime.data.model.PositionType

@Entity(
    tableName = "users",
    foreignKeys = [
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["branch_id"]),
        Index(value = ["created_by"])
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

    val role: UserRole,

    val position: PositionType,

    @ColumnInfo(name = "branch_id")
    val branchId: Int?,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_by")
    val createdBy: Int?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
