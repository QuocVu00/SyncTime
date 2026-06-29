package com.example.synctime.data.local.dao

import androidx.room.*
import com.example.synctime.data.local.entity.RequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: RequestEntity)

    @Update
    suspend fun update(request: RequestEntity)

    @Query("SELECT * FROM requests")
    fun getAllRequests(): Flow<List<RequestEntity>>

    @Query("SELECT * FROM requests WHERE user_id = :userId")
    fun getRequestsByUserId(userId: Int): Flow<List<RequestEntity>>

    @Query("SELECT * FROM requests WHERE status = :status")
    fun getRequestsByStatus(status: String): Flow<List<RequestEntity>>
}
