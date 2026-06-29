package com.example.synctime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendances", // Bảng dữ liệu chấm công thực tế
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["schedule_id"], unique = true), // Mỗi lịch làm việc chỉ có tối đa 1 bản ghi chấm công
        Index(value = ["user_id"])
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "schedule_id")
    val scheduleId: Int, // Liên kết với lịch đã phân

    @ColumnInfo(name = "check_in_time")
    val checkInTime: Long?, // Thời gian vào làm (Unix timestamp)

    @ColumnInfo(name = "check_out_time")
    val checkOutTime: Long?, // Thời gian về (Unix timestamp)

    @ColumnInfo(name = "check_in_bssid")
    val checkInBssid: String?, // Địa chỉ Wifi khi check-in

    @ColumnInfo(name = "check_out_bssid")
    val checkOutBssid: String?, // Địa chỉ Wifi khi check-out

    val status: String, // Trạng thái: VALID (Hợp lệ), LATE (Muộn), INVALID_WIFI, v.v.

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
