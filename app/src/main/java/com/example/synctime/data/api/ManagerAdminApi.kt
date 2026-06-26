package com.example.synctime.data.api

import com.example.synctime.data.model.ApiMessage
import com.example.synctime.data.model.AttendanceDto
import com.example.synctime.data.model.BranchDto
import com.example.synctime.data.model.BranchRequest
import com.example.synctime.data.model.CreateScheduleRequest
import com.example.synctime.data.model.RequestDto
import com.example.synctime.data.model.SalaryDto
import com.example.synctime.data.model.StaffDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ManagerAdminApi {

    // ================= MANAGER =================

    @GET("api/manager/staff")
    suspend fun getManagerStaff(): Response<List<StaffDto>>

    @GET("api/manager/requests")
    suspend fun getRequests(): Response<List<RequestDto>>

    @POST("api/manager/requests/{id}/approve")
    suspend fun approveRequest(
        @Path("id") id: Int
    ): Response<ApiMessage>

    @POST("api/manager/requests/{id}/reject")
    suspend fun rejectRequest(
        @Path("id") id: Int
    ): Response<ApiMessage>

    @GET("api/manager/attendance")
    suspend fun getManagerAttendance(): Response<List<AttendanceDto>>

    @POST("api/manager/schedules")
    suspend fun createSchedule(
        @Body body: CreateScheduleRequest
    ): Response<ApiMessage>


    // ================= ADMIN =================

    @GET("api/admin/branches")
    suspend fun getBranches(): Response<List<BranchDto>>

    @POST("api/admin/branches")
    suspend fun createBranch(
        @Body body: BranchRequest
    ): Response<ApiMessage>

    @PUT("api/admin/branches/{id}")
    suspend fun updateBranch(
        @Path("id") id: Int,
        @Body body: BranchRequest
    ): Response<ApiMessage>

    @GET("api/admin/salary-report")
    suspend fun getSalaryReport(): Response<List<SalaryDto>>

    @GET("api/admin/attendance-report")
    suspend fun getAttendanceReport(): Response<List<AttendanceDto>>
}