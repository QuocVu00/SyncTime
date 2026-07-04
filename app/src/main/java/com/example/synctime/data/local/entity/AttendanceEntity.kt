package com.example.synctime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendances",
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
        ),
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["schedule_id"], unique = true),
        Index(value = ["branch_id"])
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "schedule_id")
    val scheduleId: Int,

    @ColumnInfo(name = "branch_id")
    val branchId: Int,

    @ColumnInfo(name = "check_in_time")
    val checkInTime: Long?,

    @ColumnInfo(name = "check_out_time")
    val checkOutTime: Long?,

    @ColumnInfo(name = "check_in_bssid")
    val checkInBssid: String?,

    @ColumnInfo(name = "check_out_bssid")
    val checkOutBssid: String?,

    @ColumnInfo(name = "check_in_status")
    val checkInStatus: AttendanceStatus = AttendanceStatus.VALID,

    @ColumnInfo(name = "check_out_status")
    val checkOutStatus: AttendanceStatus = AttendanceStatus.VALID,

    @ColumnInfo(name = "late_minutes")
    val lateMinutes: Int = 0,

    @ColumnInfo(name = "overtime_minutes")
    val overtimeMinutes: Int = 0,

    @ColumnInfo(name = "total_hours")
    val totalHours: Double = 0.0,

    @ColumnInfo(name = "is_kitchen")
    val isKitchen: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
