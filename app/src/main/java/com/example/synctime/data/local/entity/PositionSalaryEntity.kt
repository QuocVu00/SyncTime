package com.example.synctime.data.local.entity

import androidx.room.*

import com.example.synctime.data.model.PositionType

@Entity(
    tableName = "position_salaries",
    indices = [Index(value = ["position"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["updated_by"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class PositionSalaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val position: PositionType,

    @ColumnInfo(name = "position_name")
    val positionName: String,

    @ColumnInfo(name = "hourly_rate")
    val hourlyRate: Double,

    @ColumnInfo(name = "updated_by")
    val updatedBy: Int?,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
