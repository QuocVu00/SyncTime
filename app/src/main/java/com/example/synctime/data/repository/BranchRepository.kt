package com.example.synctime.data.repository

import com.example.synctime.data.local.dao.BranchDao
import com.example.synctime.data.local.entity.BranchEntity
import kotlinx.coroutines.flow.Flow

class BranchRepository(
    private val branchDao: BranchDao
) {
    val allBranches: Flow<List<BranchEntity>> = branchDao.getAllBranches()

    suspend fun insert(branch: BranchEntity) = branchDao.insert(branch)

    suspend fun update(branch: BranchEntity) = branchDao.update(branch)

    suspend fun delete(branch: BranchEntity) = branchDao.delete(branch)

    suspend fun getBranchById(id: Int) = branchDao.getBranchById(id)
}
