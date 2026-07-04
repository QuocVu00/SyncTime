package com.example.synctime.data.model

import com.google.gson.annotations.SerializedName

/*
    Chức vụ nhân viên và logic tính toán đi kèm
*/
enum class PositionType(
    val code: String,
    val displayName: String,
    val calculateLateAndOvertime: Boolean
) {
    @SerializedName("SERVER") SERVER("SERVER", "Phục vụ", true),
    @SerializedName("BARISTA") BARISTA("BARISTA", "Pha chế", true),
    @SerializedName("CASHIER") CASHIER("CASHIER", "Thu ngân", true),
    @SerializedName("SUPERVISOR") SUPERVISOR("SUPERVISOR", "Giám sát", true),
    @SerializedName("KITCHEN") KITCHEN("KITCHEN", "Bếp", false),
    @SerializedName("RUNNER") RUNNER("RUNNER", "Tiếp thực", true);

    companion object {
        fun fromCode(code: String?): PositionType = entries.firstOrNull { it.code == code } ?: SERVER
    }
}

enum class UserRole {
    @SerializedName("ADMIN") ADMIN,
    @SerializedName("MANAGER") MANAGER,
    @SerializedName("STAFF") STAFF
}

enum class ScheduleStatus {
    @SerializedName("SCHEDULED") SCHEDULED,
    @SerializedName("PENDING") PENDING,
    @SerializedName("COMPLETED") COMPLETED,
    @SerializedName("ABSENT") ABSENT,
    @SerializedName("CANCELLED") CANCELLED
}

enum class AttendanceStatus {
    @SerializedName("VALID") VALID,
    @SerializedName("LATE") LATE,
    @SerializedName("INVALID_WIFI") INVALID_WIFI,
    @SerializedName("INVALID_DEVICE") INVALID_DEVICE,
    @SerializedName("MISSING_CHECKOUT") MISSING_CHECKOUT,
    @SerializedName("NO_SCHEDULE") NO_SCHEDULE
}

enum class RequestType {
    @SerializedName("LEAVE") LEAVE,
    @SerializedName("CHANGE_SHIFT") CHANGE_SHIFT,
    @SerializedName("UPDATE_BSSID") UPDATE_BSSID
}

enum class RequestStatus {
    @SerializedName("PENDING") PENDING,
    @SerializedName("APPROVED") APPROVED,
    @SerializedName("REJECTED") REJECTED
}
