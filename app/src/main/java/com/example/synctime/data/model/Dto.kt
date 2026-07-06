package com.example.synctime.data.model

data class MessageResponse(
    val success: Boolean = false,
    val message: String = ""
)

data class LoginRequest(
    val email: String,
    val password: String,

    // Backend mới dùng androidId
    val androidId: String,

    // Một số code/backend cũ có thể dùng deviceId
    val deviceId: String = androidId,

    val fcmToken: String? = null
)

data class LoginResponse(
    val success: Boolean = false,
    val message: String = "",
    val token: String = "",
    val user: AuthUser? = null
)

data class AuthUser(
    val id: Int = 0,
    val fullName: String = "",
    val email: String = "",
    val role: String = "",
    val branchId: Int? = null,
    val position: String? = null
)

data class AttendanceRequest(
    val deviceId: String,
    val currentBssid: String
)

data class AttendanceStatusResponse(
    val checkedIn: Boolean = false,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val status: String = ""
)

data class BranchDto(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val wifiBssid: String = "",
    val rewardRate: Double = 1.0
)

data class BranchRequest(
    val name: String,
    val address: String,
    val wifiBssid: String,
    val rewardRate: Double = 1.0
)

data class ShiftDto(
    val id: Int = 0,
    val name: String = "",
    val startTime: String = "",
    val endTime: String = ""
)

typealias ShiftOption = ShiftDto

data class ShiftRequest(
    val name: String,
    val startTime: String,
    val endTime: String
)

data class StaffDto(
    val id: Int = 0,
    val fullName: String = "",
    val email: String = "",
    val role: String = "",
    val branchId: Int? = null,
    val position: String = "",
    val positionName: String = "",
    val branchName: String = ""
)

data class CreateStaffRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String = "STAFF",
    val branchId: Int? = null,
    val position: String,
    val baseSalary: Double = 0.0
)

data class ScheduleDto(
    val id: Int = 0,
    val userId: Int = 0,
    val fullName: String = "",
    val shiftId: Int = 0,
    val shiftName: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val workDate: String = "",
    val status: String = ""
)

data class CreateScheduleRequest(
    val userId: Int,
    val shiftId: Int,
    val workDate: String
)

data class CreateMultiScheduleRequest(
    val userIds: List<Int> = emptyList(),
    val shiftId: Int = 0,
    val workDate: String = "",
    val items: List<CreateScheduleRequest> =
        if (userIds.isNotEmpty() && shiftId > 0 && workDate.isNotBlank()) {
            userIds.map { userId ->
                CreateScheduleRequest(
                    userId = userId,
                    shiftId = shiftId,
                    workDate = workDate
                )
            }
        } else {
            emptyList()
        }
)

data class RequestDto(
    val id: Int = 0,
    val userId: Int = 0,
    val staffId: Int = 0,

    val fullName: String = "",
    val staffName: String = "",
    val employeeName: String = "",

    val type: String = "",
    val reason: String = "",

    val targetDate: String = "",
    val date: String = "",
    val workDate: String = "",

    val status: String = "",
    val createdAt: String = "",
    val note: String = ""
)

data class CreateRequestBody(
    val type: String,
    val reason: String,
    val targetDate: String = ""
)

data class SalaryDto(
    val userId: Int = 0,
    val staffId: Int = userId,

    val fullName: String = "",
    val staffName: String = fullName,
    val employeeName: String = fullName,

    val position: String = "",
    val positionName: String = if (position.isNotBlank()) position else "Nhân viên",

    val attendanceCount: Int = 0,

    val hourlyRate: Double = 0.0,
    val totalHours: Double = attendanceCount * 8.0,

    val lateMinutes: Int = 0,
    val overtimeMinutes: Int = 0,

    val baseSalary: Double = 0.0,
    val estimatedSalary: Double = 0.0,
    val salary: Double = if (estimatedSalary > 0.0) estimatedSalary else baseSalary
)

/*
    Các class dưới đây dùng để tương thích với các Activity cũ:
    CreateRequestActivity.kt, MyScheduleActivity.kt, ViewRequestsActivity.kt
*/

data class StaffCreateRequest(
    val type: String,
    val reason: String
)

data class StaffCreateRequestResponse(
    val success: Boolean = false,
    val message: String = ""
)

data class MyScheduleResponse(
    val success: Boolean = false,
    val message: String = "",
    val data: List<ScheduleDto> = emptyList()
)

data class ViewRequestsResponse(
    val success: Boolean = false,
    val message: String = "",
    val data: List<RequestDto> = emptyList()
)

enum class PositionType(
    val code: String,
    val displayName: String
) {
    SERVER("SERVER", "Phục vụ"),
    BARISTA("BARISTA", "Pha chế"),
    KITCHEN("KITCHEN", "Bếp"),
    CASHIER("CASHIER", "Thu ngân"),
    MANAGER("MANAGER", "Quản lý");

    companion object {
        fun fromCode(code: String): PositionType {
            return values().firstOrNull { it.code == code } ?: SERVER
        }
    }
}

data class PositionSalaryDto(
    val id: Int = 0,
    val position: String = "",
    val positionName: String = "",
    val hourlyRate: Double = 0.0,
    val baseSalary: Double = 0.0,
    val overtimeRate: Double = 1.5,
    val latePenaltyPerMinute: Double = 0.0
)

data class AttendanceDto(
    val id: Int = 0,

    val userId: Int = 0,
    val staffId: Int = 0,

    val fullName: String = "",
    val staffName: String = "",
    val employeeName: String = "",

    val position: String = "",
    val positionName: String = "",

    val branchId: Int? = null,
    val branchName: String = "",

    val workDate: String = "",
    val date: String = "",

    val shiftId: Int = 0,
    val shiftName: String = "",

    val checkInTime: String = "",
    val checkOutTime: String = "",

    val checkInBssid: String = "",
    val checkOutBssid: String = "",

    val totalHours: Double = 0.0,
    val lateMinutes: Int = 0,
    val overtimeMinutes: Int = 0,

    val status: String = "",
    val note: String = ""
)

data class UpdatePositionSalaryRequest(
    val position: String = "",
    val positionName: String = "",
    val hourlyRate: Double = 0.0,
    val baseSalary: Double = 0.0,
    val overtimeRate: Double = 1.5,
    val latePenaltyPerMinute: Double = 0.0
)