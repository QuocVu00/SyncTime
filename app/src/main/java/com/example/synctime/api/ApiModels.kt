package com.example.synctime.api

data class StaffCreateRequest(
    val type: String = "",
    val reason: String = "",
    val targetDate: String = ""
)

data class StaffCreateRequestResponse(
    val success: Boolean = false,
    val message: String = ""
)

/*
    Dùng cho MyScheduleActivity.kt
*/
data class ScheduleResponse(
    val success: Boolean = true,
    val message: String = "",
    val schedules: List<ScheduleItem> = emptyList(),
    val data: List<ScheduleItem> = emptyList()
)

data class ScheduleItem(
    val id: Int = 0,
    val date: String = "",
    val workDate: String = "",
    val shift: String = "",
    val shiftName: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val note: String = "",
    val status: String = ""
)

/*
    Dùng cho ViewRequestsActivity.kt
*/
data class StaffRequestsResponse(
    val success: Boolean = true,
    val message: String = "",
    val requests: List<StaffRequestItem> = emptyList(),
    val data: List<StaffRequestItem> = emptyList()
)

data class StaffRequestItem(
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
    val status: String = "",
    val createdAt: String = "",
    val note: String = ""
)

data class ViewRequestsResponse(
    val success: Boolean = true,
    val message: String = "",
    val requests: List<StaffRequestItem> = emptyList(),
    val data: List<StaffRequestItem> = emptyList()
)

data class RequestListResponse(
    val success: Boolean = true,
    val message: String = "",
    val requests: List<StaffRequestItem> = emptyList(),
    val data: List<StaffRequestItem> = emptyList()
)

data class RequestActionResponse(
    val success: Boolean = false,
    val message: String = ""
)