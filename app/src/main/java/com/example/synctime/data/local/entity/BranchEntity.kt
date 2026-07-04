package com.example.synctime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "branches",
    indices = [Index(value = ["wifi_bssid"], unique = true)]
)
data class BranchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val name: String,
    
    val address: String,
    
    @ColumnInfo(name = "wifi_bssid")
    val wifiBssid: String,

    @ColumnInfo(name = "reward_rate")
    val rewardRate: Double = 1.0,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
