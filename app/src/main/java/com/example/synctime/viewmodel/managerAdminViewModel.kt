package com.example.synctime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synctime.data.api.ApiClient
import com.example.synctime.data.model.AttendanceDto
import com.example.synctime.data.model.BranchDto
import com.example.synctime.data.model.BranchRequest
import com.example.synctime.data.model.CreateScheduleRequest
import com.example.synctime.data.model.RequestDto
import com.example.synctime.data.model.SalaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManagerAdminViewModel(
    token: String
) : ViewModel() {

    /*
        Để true thì app chạy bằng dữ liệu mẫu, không cần backend.
        Khi backend nhóm bạn xong, đổi thành false để gọi API thật.
    */
    private val useMockData = true

    private val api = ApiClient.create(token)

    private val _requests = MutableStateFlow<List<RequestDto>>(emptyList())
    val requests: StateFlow<List<RequestDto>> = _requests

    private val _branches = MutableStateFlow<List<BranchDto>>(emptyList())
    val branches: StateFlow<List<BranchDto>> = _branches

    private val _attendance = MutableStateFlow<List<AttendanceDto>>(emptyList())
    val attendance: StateFlow<List<AttendanceDto>> = _attendance

    private val _salary = MutableStateFlow<List<SalaryDto>>(emptyList())
    val salary: StateFlow<List<SalaryDto>> = _salary

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun loadRequests() {
        if (useMockData) {
            _requests.value = listOf(
                RequestDto(
                    id = 1,
                    userId = 3,
                    fullName = "Nguyễn Văn An",
                    type = "LEAVE",
                    reason = "Em xin nghỉ vì có việc gia đình",
                    targetDate = "2026-06-27",
                    status = "PENDING"
                ),
                RequestDto(
                    id = 2,
                    userId = 4,
                    fullName = "Trần Thị Bình",
                    type = "CHANGE_SHIFT",
                    reason = "Em muốn đổi sang ca chiều",
                    targetDate = "2026-06-28",
                    status = "PENDING"
                ),
                RequestDto(
                    id = 3,
                    userId = 5,
                    fullName = "Lê Minh Khoa",
                    type = "LEAVE",
                    reason = "Em bị bệnh nên xin nghỉ",
                    targetDate = "2026-06-29",
                    status = "APPROVED"
                )
            )
            _message.value = "Đang dùng dữ liệu mẫu"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.getRequests()
                if (response.isSuccessful) {
                    _requests.value = response.body() ?: emptyList()
                    _message.value = ""
                } else {
                    _message.value = "Không tải được danh sách đơn"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi kết nối server: ${e.message}"
            }
        }
    }

    fun approveRequest(id: Int) {
        if (useMockData) {
            _requests.value = _requests.value.map {
                if (it.id == id) it.copy(status = "APPROVED") else it
            }
            _message.value = "Đã duyệt đơn"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.approveRequest(id)
                if (response.isSuccessful) {
                    _message.value = "Đã duyệt đơn"
                    loadRequests()
                } else {
                    _message.value = "Duyệt đơn thất bại"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi: ${e.message}"
            }
        }
    }

    fun rejectRequest(id: Int) {
        if (useMockData) {
            _requests.value = _requests.value.map {
                if (it.id == id) it.copy(status = "REJECTED") else it
            }
            _message.value = "Đã từ chối đơn"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.rejectRequest(id)
                if (response.isSuccessful) {
                    _message.value = "Đã từ chối đơn"
                    loadRequests()
                } else {
                    _message.value = "Từ chối đơn thất bại"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi: ${e.message}"
            }
        }
    }

    fun loadManagerAttendance() {
        if (useMockData) {
            _attendance.value = listOf(
                AttendanceDto(
                    id = 1,
                    userId = 3,
                    fullName = "Nguyễn Văn An",
                    checkInTime = "2026-06-26 08:02",
                    checkOutTime = "2026-06-26 17:05",
                    status = "VALID",
                    checkInBssid = "A1:B2:C3:D4:E5:F6",
                    checkOutBssid = "A1:B2:C3:D4:E5:F6"
                ),
                AttendanceDto(
                    id = 2,
                    userId = 4,
                    fullName = "Trần Thị Bình",
                    checkInTime = "2026-06-26 08:30",
                    checkOutTime = null,
                    status = "LATE",
                    checkInBssid = "A1:B2:C3:D4:E5:F6",
                    checkOutBssid = null
                ),
                AttendanceDto(
                    id = 3,
                    userId = 5,
                    fullName = "Lê Minh Khoa",
                    checkInTime = "2026-06-26 08:05",
                    checkOutTime = "2026-06-26 12:00",
                    status = "INVALID_WIFI",
                    checkInBssid = "11:22:33:44:55:66",
                    checkOutBssid = "11:22:33:44:55:66"
                )
            )
            _message.value = "Đang dùng dữ liệu mẫu"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.getManagerAttendance()
                if (response.isSuccessful) {
                    _attendance.value = response.body() ?: emptyList()
                    _message.value = ""
                } else {
                    _message.value = "Không tải được lịch sử chấm công"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi kết nối server: ${e.message}"
            }
        }
    }

    fun loadBranches() {
        if (useMockData) {
            _branches.value = listOf(
                BranchDto(
                    id = 1,
                    name = "Chi nhánh Quận 12",
                    address = "Quận 12, TP.HCM",
                    wifiBssid = "A1:B2:C3:D4:E5:F6",
                    rewardRate = 1.0
                ),
                BranchDto(
                    id = 2,
                    name = "Chi nhánh Gò Vấp",
                    address = "Gò Vấp, TP.HCM",
                    wifiBssid = "AA:BB:CC:DD:EE:FF",
                    rewardRate = 1.0
                )
            )
            _message.value = "Đang dùng dữ liệu mẫu"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.getBranches()
                if (response.isSuccessful) {
                    _branches.value = response.body() ?: emptyList()
                    _message.value = ""
                } else {
                    _message.value = "Không tải được danh sách chi nhánh"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi kết nối server: ${e.message}"
            }
        }
    }

    fun updateBranchBssid(branch: BranchDto, newBssid: String) {
        if (newBssid.isBlank()) {
            _message.value = "BSSID không được để trống"
            return
        }

        if (useMockData) {
            _branches.value = _branches.value.map {
                if (it.id == branch.id) it.copy(wifiBssid = newBssid) else it
            }
            _message.value = "Đã cập nhật BSSID"
            return
        }

        viewModelScope.launch {
            try {
                val body = BranchRequest(
                    name = branch.name,
                    address = branch.address,
                    wifiBssid = newBssid,
                    rewardRate = branch.rewardRate
                )

                val response = api.updateBranch(branch.id, body)

                if (response.isSuccessful) {
                    _message.value = "Đã cập nhật BSSID"
                    loadBranches()
                } else {
                    _message.value = "Cập nhật BSSID thất bại"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi: ${e.message}"
            }
        }
    }

    fun loadSalaryReport() {
        if (useMockData) {
            _salary.value = listOf(
                SalaryDto(
                    userId = 3,
                    fullName = "Nguyễn Văn An",
                    totalHours = 42.0,
                    salary = 2100000.0
                ),
                SalaryDto(
                    userId = 4,
                    fullName = "Trần Thị Bình",
                    totalHours = 38.5,
                    salary = 1925000.0
                ),
                SalaryDto(
                    userId = 5,
                    fullName = "Lê Minh Khoa",
                    totalHours = 40.0,
                    salary = 2000000.0
                )
            )
            _message.value = "Đang dùng dữ liệu mẫu"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.getSalaryReport()
                if (response.isSuccessful) {
                    _salary.value = response.body() ?: emptyList()
                    _message.value = ""
                } else {
                    _message.value = "Không tải được bảng lương"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi kết nối server: ${e.message}"
            }
        }
    }

    fun createSchedule(userId: String, shiftId: String, workDate: String) {
        if (userId.isBlank() || shiftId.isBlank() || workDate.isBlank()) {
            _message.value = "Vui lòng nhập đủ User ID, Shift ID và ngày làm"
            return
        }

        val userIdNumber = userId.toIntOrNull()
        val shiftIdNumber = shiftId.toIntOrNull()

        if (userIdNumber == null || shiftIdNumber == null) {
            _message.value = "User ID và Shift ID phải là số"
            return
        }

        if (useMockData) {
            _message.value = "Đã tạo lịch làm mẫu cho user $userId ngày $workDate"
            return
        }

        viewModelScope.launch {
            try {
                val body = CreateScheduleRequest(
                    userId = userIdNumber,
                    shiftId = shiftIdNumber,
                    workDate = workDate
                )

                val response = api.createSchedule(body)

                if (response.isSuccessful) {
                    _message.value = "Tạo lịch làm thành công"
                } else {
                    _message.value = "Tạo lịch làm thất bại"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi: ${e.message}"
            }
        }
    }
}