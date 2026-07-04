package com.example.synctime.data.api

import com.example.synctime.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StaffApi {

    @GET("api/staff/profile")
    suspend fun getProfile(): Response<ApiResponse<StaffDto>>

    @GET("api/staff/schedules")
    suspend fun getMySchedules(): Response<ApiResponse<List<AttendanceDto>>>

    @POST("api/staff/check-in")
    suspend fun checkIn(
        @Body request: CheckInRequest
    ): Response<ApiResponse<String>>

    @POST("api/staff/check-out")
    suspend fun checkOut(
        @Body request: CheckOutRequest
    ): Response<ApiResponse<String>>

    @POST("api/staff/requests")
    suspend fun createRequest(
        @Body request: StaffRequest
    ): Response<ApiResponse<String>>

    @GET("api/staff/requests")
    suspend fun getMyRequests(): Response<ApiResponse<List<RequestDto>>>

    @GET("api/staff/attendance-history")
    suspend fun getAttendanceHistory(): Response<ApiResponse<List<AttendanceDto>>>

    @GET("api/staff/notifications")
    suspend fun getNotifications(): Response<ApiResponse<List<NotificationDto>>>
}
