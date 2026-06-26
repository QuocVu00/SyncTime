package com.example.synctime.data.model

data class ApiMessage(
    val message: String? = null
)

data class StaffDto(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val branchId: Int?
)

data class RequestDto(
    val id: Int,
    val userId: Int,
    val fullName: String?,
    val type: String,
    val reason: String,
    val targetDate: String,
    val status: String
)

data class AttendanceDto(
    val id: Int,
    val userId: Int,
    val fullName: String?,
    val checkInTime: String?,
    val checkOutTime: String?,
    val status: String,
    val checkInBssid: String?,
    val checkOutBssid: String?
)

data class BranchDto(
    val id: Int,
    val name: String,
    val address: String,
    val wifiBssid: String,
    val rewardRate: Double?
)

data class SalaryDto(
    val userId: Int,
    val fullName: String,
    val totalHours: Double,
    val salary: Double
)

data class BranchRequest(
    val name: String,
    val address: String,
    val wifiBssid: String,
    val rewardRate: Double?
)

data class CreateScheduleRequest(
    val userId: Int,
    val shiftId: Int,
    val workDate: String
)