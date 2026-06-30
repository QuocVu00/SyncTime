package com.example.baitaplon.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val success: Boolean, val message: String, val token: String?)

data class CheckInRequest(
    val androidId: String,
    val bssid: String,
    val timestamp: Long = System.currentTimeMillis()
)
data class CheckInResponse(val success: Boolean, val message: String)

data class CheckOutRequest(
    val androidId: String,
    val bssid: String,
    val timestamp: Long = System.currentTimeMillis()
)
data class CheckOutResponse(val success: Boolean, val message: String)

data class AttendanceStatusResponse(
    val success: Boolean,
    val status: String, // e.g., "Not started", "In progress", "Finished"
    val checkInTime: String?,
    val checkOutTime: String?
)

data class ScheduleItem(
    val date: String,
    val shift: String,
    val startTime: String,
    val endTime: String,
    val note: String?
)

data class ScheduleResponse(
    val success: Boolean,
    val schedules: List<ScheduleItem>
)

data class LeaveRequest(
    val androidId: String,
    val type: String, // "Leave" or "Shift Change"
    val date: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class LeaveResponse(
    val success: Boolean,
    val message: String
)

data class StaffRequestItem(
    val id: String,
    val type: String,
    val date: String,
    val reason: String,
    val status: String, // "Pending", "Approved", "Rejected"
    val timestamp: Long
)

data class StaffRequestsResponse(
    val success: Boolean,
    val requests: List<StaffRequestItem>
)

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("checkin")
    fun checkIn(@Body request: CheckInRequest): Call<CheckInResponse>

    @POST("checkout")
    fun checkOut(@Body request: CheckOutRequest): Call<CheckOutResponse>

    @POST("status")
    fun getAttendanceStatus(@Body request: Map<String, String>): Call<AttendanceStatusResponse>

    @POST("schedule")
    fun getMySchedule(@Body request: Map<String, String>): Call<ScheduleResponse>

    @POST("create-request")
    fun createRequest(@Body request: LeaveRequest): Call<LeaveResponse>

    @POST("my-requests")
    fun getMyRequests(@Body request: Map<String, String>): Call<StaffRequestsResponse>

    companion object {
        private const val BASE_URL = "https://your-api-url.com/" // Replace with actual API URL
        private const val IS_DEMO_MODE = true // Set to false when connecting to real server

        fun create(): ApiService {
            if (IS_DEMO_MODE) {
                return MockApiService()
            }
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
