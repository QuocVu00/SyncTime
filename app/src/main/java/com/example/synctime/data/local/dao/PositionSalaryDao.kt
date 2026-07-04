package com.example.synctime.data.local.dao

import androidx.room.*
import com.example.synctime.data.local.entity.PositionSalaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PositionSalaryDao {
    @Query("SELECT * FROM position_salaries")
    fun getAllPositionSalaries(): Flow<List<PositionSalaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(positionSalary: PositionSalaryEntity)

    @Update
    suspend fun update(positionSalary: PositionSalaryEntity)

    @Delete
    suspend fun delete(positionSalary: PositionSalaryEntity)

    @Query("SELECT * FROM position_salaries WHERE position = :position")
    suspend fun getPositionSalary(position: String): PositionSalaryEntity?
}
