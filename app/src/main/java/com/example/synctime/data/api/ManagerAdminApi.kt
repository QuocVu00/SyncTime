package com.example.synctime.data.api

import com.example.synctime.data.model.ApiMessage
import com.example.synctime.data.model.ApiResponse
import com.example.synctime.data.model.AttendanceDto
import com.example.synctime.data.model.BranchDto
import com.example.synctime.data.model.BranchRequest
import com.example.synctime.data.model.CreateMultiScheduleRequest
import com.example.synctime.data.model.CreateScheduleRequest
import com.example.synctime.data.model.CreateStaffRequest
import com.example.synctime.data.model.PositionSalaryDto
import com.example.synctime.data.model.RequestDto
import com.example.synctime.data.model.SalaryDto
import com.example.synctime.data.model.StaffDto
import com.example.synctime.data.model.UpdatePositionSalaryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ManagerAdminApi {

    // ================= MANAGER =================

    @GET("api/manager/staff")
    suspend fun getManagerStaff(): Response<ApiResponse<List<StaffDto>>>

    /*
        API mới: Manager tạo nhân viên.
        Backend cần có API:
        POST /api/manager/staff
    */
    @POST("api/manager/staff")
    suspend fun createStaff(
        @Body body: CreateStaffRequest
    ): Response<ApiMessage>

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

    /*
        API cũ: tạo lịch cho 1 nhân viên.
        Giữ lại để không phá code cũ.
    */
    @POST("api/manager/schedules")
    suspend fun createSchedule(
        @Body body: CreateScheduleRequest
    ): Response<ApiMessage>

    /*
        API mới: tạo lịch cho nhiều nhân viên.
        Nếu backend chưa có API này thì tạm thời dùng mock data trước.
    */
    @POST("api/manager/schedules/multiple")
    suspend fun createMultiSchedule(
        @Body body: CreateMultiScheduleRequest
    ): Response<ApiMessage>


    // ================= ADMIN =================

    @GET("api/admin/branches")
    suspend fun getBranches(): Response<List<BranchDto>>

    /*
        API Admin tạo chi nhánh mới.
        Backend cần có API:
        POST /api/admin/branches
    */
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

    /*
        API mới: Admin xem lương theo chức vụ.
    */
    @GET("api/admin/position-salaries")
    suspend fun getPositionSalaries(): Response<List<PositionSalaryDto>>

    /*
        API mới: Admin cập nhật lương theo chức vụ.
        Ví dụ:
        PUT /api/admin/position-salaries/SERVER
    */
    @PUT("api/admin/position-salaries/{position}")
    suspend fun updatePositionSalary(
        @Path("position") position: String,
        @Body body: UpdatePositionSalaryRequest
    ): Response<ApiMessage>
}