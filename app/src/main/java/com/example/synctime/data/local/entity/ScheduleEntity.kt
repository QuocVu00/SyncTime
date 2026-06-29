package com.example.synctime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedules", // Bảng phân lịch làm việc
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ShiftEntity::class,
            parentColumns = ["id"],
            childColumns = ["shift_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id", "work_date"], unique = true), // Một người không thể có 2 lịch trong 1 ngày
        Index(value = ["user_id"]),
        Index(value = ["work_date"])
    ]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int, // Nhân viên được phân lịch

    @ColumnInfo(name = "shift_id")
    val shiftId: Int, // Ca làm việc (Sáng/Chiều/Tối)

    @ColumnInfo(name = "work_date")
    val workDate: String, // Ngày làm việc (Định dạng YYYY-MM-DD)

    val status: String, // Trạng thái: PENDING (Chờ), COMPLETED (Xong), CANCELLED (Hủy)

    @ColumnInfo(name = "created_by")
    val createdBy: Int // ID người phân lịch (thường là Manager)
)
