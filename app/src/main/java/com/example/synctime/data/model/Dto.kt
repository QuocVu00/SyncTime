package com.example.synctime.data.model

import com.google.gson.annotations.SerializedName

data class ApiMessage(
    val message: String? = null
)

// --- AUTH ---

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserAuthDto
)

data class UserAuthDto(
    @SerializedName("id") val id: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("role") val role: UserRole,
    @SerializedName("position") val position: PositionType,
    @SerializedName("branch_id") val branchId: Int?
)

// --- COMMON & STAFF ---

data class StaffDto(
    @SerializedName("id") val id: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: UserRole,
    @SerializedName("branch_id") val branchId: Int?,
    @SerializedName("position") val position: PositionType = PositionType.SERVER,
    @SerializedName("position_name") val positionName: String = "Phục vụ",
    @SerializedName("branch_name") val branchName: String = "Chi nhánh chính"
)

data class RequestDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("type") val type: RequestType,
    @SerializedName("reason") val reason: String,
    @SerializedName("target_date") val targetDate: String,
    @SerializedName("status") val status: RequestStatus,
    @SerializedName("response_note") val responseNote: String? = null
)

data class AttendanceDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("check_in_time") val checkInTime: String?,
    @SerializedName("check_out_time") val checkOutTime: String?,
    @SerializedName("status") val status: AttendanceStatus,
    @SerializedName("check_in_bssid") val checkInBssid: String?,
    @SerializedName("check_out_bssid") val checkOutBssid: String?,
    @SerializedName("position") val position: PositionType = PositionType.SERVER,
    @SerializedName("position_name") val positionName: String = "Phục vụ",
    @SerializedName("late_minutes") val lateMinutes: Int = 0,
    @SerializedName("overtime_minutes") val overtimeMinutes: Int = 0,
    @SerializedName("total_hours") val totalHours: Double = 0.0
)

data class NotificationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("created_at") val createdAt: String
)

// --- REQUEST BODIES ---

data class CreateStaffRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: UserRole = UserRole.STAFF,
    @SerializedName("position") val position: PositionType,
    @SerializedName("branch_id") val branchId: Int?
)

data class CreateMultiScheduleRequest(
    @SerializedName("user_ids") val userIds: List<Int>,
    @SerializedName("shift_id") val shiftId: Int,
    @SerializedName("work_date") val workDate: String
)

data class CheckInRequest(
    @SerializedName("schedule_id") val scheduleId: Int,
    @SerializedName("bssid") val bssid: String,
    @SerializedName("android_id") val androidId: String
)

data class CheckOutRequest(
    @SerializedName("schedule_id") val scheduleId: Int,
    @SerializedName("bssid") val bssid: String,
    @SerializedName("android_id") val androidId: String
)

// --- ADMIN ---

data class BranchDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("wifi_bssid") val wifiBssid: String,
    @SerializedName("reward_rate") val rewardRate: Double?
)

data class SalaryDto(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("total_hours") val totalHours: Double,
    @SerializedName("salary") val salary: Double,
    @SerializedName("position") val position: PositionType = PositionType.SERVER,
    @SerializedName("position_name") val positionName: String = "Phục vụ",
    @SerializedName("hourly_rate") val hourlyRate: Double = 0.0,
    @SerializedName("late_minutes") val lateMinutes: Int = 0,
    @SerializedName("overtime_minutes") val overtimeMinutes: Int = 0
)

data class PositionSalaryDto(
    @SerializedName("position") val position: PositionType,
    @SerializedName("position_name") val positionName: String,
    @SerializedName("hourly_rate") val hourlyRate: Double
)

// --- WRAPPER ---

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T? = null,
    @SerializedName("error_code") val errorCode: String? = null
)
