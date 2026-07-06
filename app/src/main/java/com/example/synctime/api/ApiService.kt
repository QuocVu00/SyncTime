package com.example.synctime.api

import com.example.synctime.data.model.AttendanceDto
import com.example.synctime.data.model.AttendanceRequest
import com.example.synctime.data.model.AttendanceStatusResponse
import com.example.synctime.data.model.BranchDto
import com.example.synctime.data.model.BranchRequest
import com.example.synctime.data.model.CreateMultiScheduleRequest
import com.example.synctime.data.model.CreateRequestBody
import com.example.synctime.data.model.CreateScheduleRequest
import com.example.synctime.data.model.CreateStaffRequest
import com.example.synctime.data.model.LoginRequest
import com.example.synctime.data.model.LoginResponse
import com.example.synctime.data.model.MessageResponse
import com.example.synctime.data.model.PositionSalaryDto
import com.example.synctime.data.model.RequestDto
import com.example.synctime.data.model.SalaryDto
import com.example.synctime.data.model.ScheduleDto
import com.example.synctime.data.model.ShiftDto
import com.example.synctime.data.model.ShiftRequest
import com.example.synctime.data.model.StaffDto
import com.example.synctime.data.model.UpdatePositionSalaryRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    companion object {
        fun create(token: String? = null): ApiService {
            return ApiClient.create(token)
        }
    }

    // =========================================================
    // LOGIN - DẠNG CALL DÙNG CHO ACTIVITY CŨ
    // =========================================================

    @POST("auth/login")
    fun loginCall(
        @Body body: LoginRequest
    ): Call<LoginResponse>

    // =========================================================
    // LOGIN - DẠNG SUSPEND DÙNG CHO COMPOSE / VIEWMODEL
    // =========================================================

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    // =========================================================
    // STAFF REQUEST - DẠNG CALL CŨ
    // =========================================================

    @POST("requests")
    fun createRequest(
        @Body body: StaffCreateRequest
    ): Call<StaffCreateRequestResponse>

    @GET("schedules/my")
    fun getMySchedule(
        @Query("userId") userId: Any? = null
    ): Call<ScheduleResponse>

    @GET("requests")
    fun getMyRequests(
        @Query("userId") userId: Any? = null
    ): Call<StaffRequestsResponse>

    @GET("requests")
    fun getStaffRequests(
        @Query("userId") userId: Any? = null
    ): Call<StaffRequestsResponse>

    @GET("requests")
    fun getViewRequests(
        @Query("managerId") managerId: Any? = null
    ): Call<ViewRequestsResponse>

    @GET("requests")
    fun getRequestsOld(
        @Query("managerId") managerId: Any? = null
    ): Call<RequestListResponse>

    @POST("requests/{id}/approve")
    fun approveRequestOld(
        @Path("id") id: Int
    ): Call<RequestActionResponse>

    @POST("requests/{id}/reject")
    fun rejectRequestOld(
        @Path("id") id: Int
    ): Call<RequestActionResponse>

    // =========================================================
    // CÁC HÀM CALL<> CHO MÀN HÌNH ADMIN / MANAGER / STAFF CŨ
    // =========================================================

    @GET("branches")
    fun getBranchesCall(): Call<List<BranchDto>>

    @GET("staff")
    fun getStaffCall(): Call<List<StaffDto>>

    @POST("staff")
    fun createStaffCall(
        @Body body: CreateStaffRequest
    ): Call<StaffDto>

    @GET("shifts")
    fun getShiftsCall(): Call<List<ShiftDto>>

    @POST("shifts")
    fun createShiftCall(
        @Body body: ShiftRequest
    ): Call<ShiftDto>

    @GET("salary")
    fun getSalaryCall(): Call<List<SalaryDto>>

    @GET("requests")
    fun getRequestsCall(): Call<List<RequestDto>>

    @GET("requests")
    fun getMyRequestsCall(): Call<List<RequestDto>>

    @GET("schedules/my")
    fun getMySchedulesCall(): Call<List<ScheduleDto>>

    @POST("schedules/multi")
    fun createMultiScheduleCall(
        @Body body: CreateMultiScheduleRequest
    ): Call<MessageResponse>

    @POST("requests/{id}/approve")
    fun approveRequestCall(
        @Path("id") id: Int
    ): Call<MessageResponse>

    @POST("requests/{id}/reject")
    fun rejectRequestCall(
        @Path("id") id: Int
    ): Call<MessageResponse>

    // =========================================================
    // ATTENDANCE - DẠNG CALL<> CHO ACTIVITY CŨ
    // =========================================================

    @GET("attendance/status")
    fun getAttendanceStatusCall(): Call<AttendanceStatusResponse>

    @POST("attendance/check-in")
    fun checkInCall(
        @Body body: AttendanceRequest
    ): Call<MessageResponse>

    @POST("attendance/check-out")
    fun checkOutCall(
        @Body body: AttendanceRequest
    ): Call<MessageResponse>

    // =========================================================
    // ATTENDANCE - DẠNG SUSPEND CHO COMPOSE / VIEWMODEL
    // =========================================================

    @POST("attendance/check-in")
    suspend fun checkIn(
        @Body body: AttendanceRequest
    ): Response<MessageResponse>

    @POST("attendance/check-out")
    suspend fun checkOut(
        @Body body: AttendanceRequest
    ): Response<MessageResponse>

    @GET("attendance/status")
    suspend fun getAttendanceStatus(): Response<AttendanceStatusResponse>

    @GET("attendance/manager")
    suspend fun getManagerAttendance(
        @Query("branchId") branchId: Int? = null,
        @Query("date") date: String? = null
    ): Response<List<AttendanceDto>>

    // =========================================================
    // BRANCHES
    // =========================================================

    @GET("branches")
    suspend fun getBranches(): Response<List<BranchDto>>

    @POST("branches")
    suspend fun createBranch(
        @Body body: BranchRequest
    ): Response<BranchDto>

    @PUT("branches/{id}")
    suspend fun updateBranch(
        @Path("id") id: Int,
        @Body body: BranchRequest
    ): Response<BranchDto>

    // =========================================================
    // SHIFTS
    // =========================================================

    @GET("shifts")
    suspend fun getShifts(): Response<List<ShiftDto>>

    @POST("shifts")
    suspend fun createShift(
        @Body body: ShiftRequest
    ): Response<ShiftDto>

    // =========================================================
    // STAFF
    // =========================================================

    @GET("staff")
    suspend fun getStaff(): Response<List<StaffDto>>

    @GET("staff")
    suspend fun getManagerStaff(): Response<List<StaffDto>>

    @POST("staff")
    suspend fun createStaff(
        @Body body: CreateStaffRequest
    ): Response<StaffDto>

    // =========================================================
    // SCHEDULES
    // =========================================================

    @GET("schedules/my")
    suspend fun getMySchedules(): Response<List<ScheduleDto>>

    @POST("schedules/multi")
    suspend fun createMultiSchedule(
        @Body body: CreateMultiScheduleRequest
    ): Response<MessageResponse>

    @POST("schedules/multi")
    suspend fun createSchedules(
        @Body body: CreateMultiScheduleRequest
    ): Response<MessageResponse>

    @POST("schedules/multi")
    suspend fun createScheduleMulti(
        @Body body: CreateMultiScheduleRequest
    ): Response<MessageResponse>

    @POST("schedules/multi")
    suspend fun createScheduleSingle(
        @Body body: CreateScheduleRequest
    ): Response<MessageResponse>

    // =========================================================
    // REQUESTS
    // =========================================================

    @POST("requests")
    suspend fun createRequestNew(
        @Body body: CreateRequestBody
    ): Response<MessageResponse>

    @GET("requests")
    suspend fun getRequests(): Response<List<RequestDto>>

    @GET("requests")
    suspend fun getMyRequestList(): Response<List<RequestDto>>

    @POST("requests/{id}/approve")
    suspend fun approveRequest(
        @Path("id") id: Int
    ): Response<MessageResponse>

    @POST("requests/{id}/reject")
    suspend fun rejectRequest(
        @Path("id") id: Int
    ): Response<MessageResponse>

    // =========================================================
    // SALARY
    // =========================================================

    @GET("salary")
    suspend fun getSalary(): Response<List<SalaryDto>>

    @GET("salary")
    suspend fun getSalaryReport(): Response<List<SalaryDto>>

    @GET("salary/positions")
    suspend fun getPositionSalaries(): Response<List<PositionSalaryDto>>

    @PUT("salary/positions/{position}")
    suspend fun updatePositionSalary(
        @Path("position") position: String,
        @Body body: UpdatePositionSalaryRequest
    ): Response<MessageResponse>

    @PUT("salary/positions/{id}")
    suspend fun updatePositionSalaryById(
        @Path("id") id: Int,
        @Body body: UpdatePositionSalaryRequest
    ): Response<MessageResponse>
}