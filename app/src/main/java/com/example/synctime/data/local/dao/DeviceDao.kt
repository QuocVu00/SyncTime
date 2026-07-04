package com.example.synctime.data.local.dao

import androidx.room.*
import com.example.synctime.data.local.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices WHERE user_id = :userId")
    fun getDevicesByUserId(userId: Int): Flow<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: DeviceEntity)

    @Update
    suspend fun update(device: DeviceEntity)

    @Delete
    suspend fun delete(device: DeviceEntity)

    @Query("SELECT * FROM devices WHERE id = :id")
    suspend fun getDeviceById(id: Int): DeviceEntity?

    @Query("SELECT * FROM devices WHERE android_id = :androidId")
    suspend fun getDeviceByAndroidId(androidId: String): DeviceEntity?
}
