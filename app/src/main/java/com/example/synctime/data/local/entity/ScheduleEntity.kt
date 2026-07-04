package com.example.synctime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

import com.example.synctime.data.model.PositionType

@Entity(
    tableName = "schedules",
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
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branch_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["shift_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["work_date"])
    ]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "shift_id")
    val shiftId: Int,

    @ColumnInfo(name = "branch_id")
    val branchId: Int,

    @ColumnInfo(name = "work_date")
    val workDate: String, // YYYY-MM-DD

    val position: PositionType,

    @ColumnInfo(name = "start_time")
    val startTime: String, // HH:mm

    @ColumnInfo(name = "end_time")
    val endTime: String, // HH:mm

    val status: ScheduleStatus = ScheduleStatus.SCHEDULED,

    @ColumnInfo(name = "created_by")
    val createdBy: Int?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
