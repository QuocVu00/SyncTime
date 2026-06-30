package com.example.synctime.data.model

data class ApiMessage(
    val message: String? = null
)

/*
    Chức vụ nhân viên trong app SyncTime

    SERVER      = Phục vụ
    BARISTA     = Pha chế
    CASHIER     = Thu ngân
    SUPERVISOR  = Giám sát
    KITCHEN     = Bếp
    RUNNER      = Tiếp thực
*/
enum class PositionType(
    val code: String,
    val displayName: String,
    val calculateLateAndOvertime: Boolean
) {
    SERVER(
        code = "SERVER",
        displayName = "Phục vụ",
        calculateLateAndOvertime = true
    ),

    BARISTA(
        code = "BARISTA",
        displayName = "Pha chế",
        calculateLateAndOvertime = true
    ),

    CASHIER(
        code = "CASHIER",
        displayName = "Thu ngân",
        calculateLateAndOvertime = true
    ),

    SUPERVISOR(
        code = "SUPERVISOR",
        displayName = "Giám sát",
        calculateLateAndOvertime = true
    ),

    KITCHEN(
        code = "KITCHEN",
        displayName = "Bếp",
        calculateLateAndOvertime = false
    ),

    RUNNER(
        code = "RUNNER",
        displayName = "Tiếp thực",
        calculateLateAndOvertime = true
    );

    companion object {
        fun fromCode(code: String?): PositionType {
            return entries.firstOrNull { it.code == code } ?: SERVER
        }
    }
}

data class StaffDto(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val branchId: Int?,

    /*
        Các field mới thêm để phục vụ tạo lịch làm.
        Cho giá trị mặc định để không làm hỏng code cũ.
    */
    val position: String = "SERVER",
    val positionName: String = "Phục vụ",
    val branchName: String = "Chi nhánh chính"
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
    val checkOutBssid: String?,

    /*
        Các field mới để sau này hiển thị trễ/tăng ca/lương.
        Hiện tại có default nên không ảnh hưởng code cũ.
    */
    val position: String = "SERVER",
    val positionName: String = "Phục vụ",
    val lateMinutes: Int = 0,
    val overtimeMinutes: Int = 0,
    val totalHours: Double = 0.0
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
    val salary: Double,

    /*
        Field mới để bảng lương rõ hơn.
    */
    val position: String = "SERVER",
    val positionName: String = "Phục vụ",
    val hourlyRate: Double = 0.0,
    val lateMinutes: Int = 0,
    val overtimeMinutes: Int = 0
)

data class BranchRequest(
    val name: String,
    val address: String,
    val wifiBssid: String,
    val rewardRate: Double?
)

/*
    Model cũ giữ lại để không lỗi API cũ.
*/
data class CreateScheduleRequest(
    val userId: Int,
    val shiftId: Int,
    val workDate: String
)

/*
    Model mới: tạo lịch cho nhiều nhân viên cùng lúc.
    Manager chọn ngày, ca, danh sách nhân viên.
*/
data class CreateMultiScheduleRequest(
    val userIds: List<Int>,
    val shiftId: Int,
    val workDate: String
)

/*
    Ca làm mẫu.
    Ví dụ:
    Ca sáng 08:00 - 12:00
*/
data class ShiftOption(
    val id: Int,
    val name: String,
    val startTime: String,
    val endTime: String
)

/*
    Lương theo chức vụ.
    Admin có thể sửa hourlyRate.
*/
data class PositionSalaryDto(
    val position: String,
    val positionName: String,
    val hourlyRate: Double
)

data class UpdatePositionSalaryRequest(
    val hourlyRate: Double
)