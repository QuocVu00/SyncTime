package com.example.synctime.data.repository

import com.example.synctime.data.local.dao.RequestDao
import com.example.synctime.data.local.entity.RequestEntity
import kotlinx.coroutines.flow.Flow

class RequestRepository(
    private val requestDao: RequestDao
) {
    val allRequests: Flow<List<RequestEntity>> = requestDao.getAllRequests()

    suspend fun insert(request: RequestEntity) = requestDao.insert(request)

    suspend fun update(request: RequestEntity) = requestDao.update(request)

    fun getRequestsByUserId(userId: Int) = requestDao.getRequestsByUserId(userId)

    fun getRequestsByStatus(status: String) = requestDao.getRequestsByStatus(status)
}
