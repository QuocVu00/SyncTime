package com.example.synctime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synctime.data.api.ApiClient
import com.example.synctime.data.model.AttendanceDto
import com.example.synctime.data.model.BranchDto
import com.example.synctime.data.model.BranchRequest
import com.example.synctime.data.model.CreateMultiScheduleRequest
import com.example.synctime.data.model.CreateScheduleRequest
import com.example.synctime.data.model.CreateStaffRequest
import com.example.synctime.data.model.PositionSalaryDto
import com.example.synctime.data.model.PositionType
import com.example.synctime.data.model.RequestDto
import com.example.synctime.data.model.SalaryDto
import com.example.synctime.data.model.ShiftOption
import com.example.synctime.data.model.StaffDto
import com.example.synctime.data.model.UpdatePositionSalaryRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManagerAdminViewModel(
    token: String
) : ViewModel() {

    /*
        true: dùng dữ liệu mẫu để demo UI, chưa cần backend.
        false: gọi API thật.
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

    private val _staffList = MutableStateFlow<List<StaffDto>>(emptyList())
    val staffList: StateFlow<List<StaffDto>> = _staffList

    private val _shiftList = MutableStateFlow<List<ShiftOption>>(emptyList())
    val shiftList: StateFlow<List<ShiftOption>> = _shiftList

    private val _positionSalaries = MutableStateFlow<List<PositionSalaryDto>>(emptyList())
    val positionSalaries: StateFlow<List<PositionSalaryDto>> = _positionSalaries

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    // ================= MOCK DATA =================

    private fun getMockStaff(): List<StaffDto> {
        return listOf(
            StaffDto(
                id = 1,
                fullName = "Nguyễn Văn An",
                email = "an@gmail.com",
                role = "STAFF",
                branchId = 1,
                position = "SERVER",
                positionName = "Phục vụ",
                branchName = "Chi nhánh Quận 12"
            ),
            StaffDto(
                id = 2,
                fullName = "Trần Thị Bình",
                email = "binh@gmail.com",
                role = "STAFF",
                branchId = 1,
                position = "BARISTA",
                positionName = "Pha chế",
                branchName = "Chi nhánh Quận 12"
            ),
            StaffDto(
                id = 3,
                fullName = "Lê Minh Khoa",
                email = "khoa@gmail.com",
                role = "STAFF",
                branchId = 1,
                position = "CASHIER",
                positionName = "Thu ngân",
                branchName = "Chi nhánh Quận 12"
            ),
            StaffDto(
                id = 4,
                fullName = "Phạm Quốc Huy",
                email = "huy@gmail.com",
                role = "STAFF",
                branchId = 1,
                position = "KITCHEN",
                positionName = "Bếp",
                branchName = "Chi nhánh Quận 12"
            ),
            StaffDto(
                id = 5,
                fullName = "Võ Minh Tuấn",
                email = "tuan@gmail.com",
                role = "STAFF",
                branchId = 1,
                position = "RUNNER",
                positionName = "Tiếp thực",
                branchName = "Chi nhánh Quận 12"
            ),
            StaffDto(
                id = 6,
                fullName = "Đặng Thanh Mai",
                email = "mai@gmail.com",
                role = "STAFF",
                branchId = 1,
                position = "SUPERVISOR",
                positionName = "Giám sát",
                branchName = "Chi nhánh Quận 12"
            )
        )
    }

    private fun getMockShifts(): List<ShiftOption> {
        return listOf(
            ShiftOption(
                id = 1,
                name = "Ca sáng",
                startTime = "08:00",
                endTime = "12:00"
            ),
            ShiftOption(
                id = 2,
                name = "Ca chiều",
                startTime = "13:00",
                endTime = "17:00"
            ),
            ShiftOption(
                id = 3,
                name = "Ca tối",
                startTime = "18:00",
                endTime = "22:00"
            ),
            ShiftOption(
                id = 4,
                name = "Ca nguyên ngày",
                startTime = "08:00",
                endTime = "17:00"
            )
        )
    }

    private fun getMockPositionSalaries(): List<PositionSalaryDto> {
        return listOf(
            PositionSalaryDto(
                position = "SERVER",
                positionName = "Phục vụ",
                hourlyRate = 25000.0
            ),
            PositionSalaryDto(
                position = "BARISTA",
                positionName = "Pha chế",
                hourlyRate = 30000.0
            ),
            PositionSalaryDto(
                position = "CASHIER",
                positionName = "Thu ngân",
                hourlyRate = 28000.0
            ),
            PositionSalaryDto(
                position = "SUPERVISOR",
                positionName = "Giám sát",
                hourlyRate = 35000.0
            ),
            PositionSalaryDto(
                position = "KITCHEN",
                positionName = "Bếp",
                hourlyRate = 32000.0
            ),
            PositionSalaryDto(
                position = "RUNNER",
                positionName = "Tiếp thực",
                hourlyRate = 24000.0
            )
        )
    }

    // ================= MANAGER =================

    fun loadStaff() {
        if (useMockData) {
            _staffList.value = getMockStaff()
            _message.value = "Đang dùng dữ liệu mẫu"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.getManagerStaff()

                if (response.isSuccessful) {
                    _staffList.value = response.body() ?: emptyList()
                    _message.value = ""
                } else {
                    _message.value = "Không tải được danh sách nhân viên"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi kết nối server: ${e.message}"
            }
        }
    }

    fun loadShifts() {
        /*
            Hiện tại dùng ca mẫu ở Android.
            Sau này nếu backend có API ca làm thì có thể đổi sang gọi API.
        */
        _shiftList.value = getMockShifts()
    }

    fun loadRequests() {
        if (useMockData) {
            _requests.value = listOf(
                RequestDto(
                    id = 1,
                    userId = 1,
                    fullName = "Nguyễn Văn An",
                    type = "LEAVE",
                    reason = "Em xin nghỉ vì có việc gia đình",
                    targetDate = "2026-06-27",
                    status = "PENDING"
                ),
                RequestDto(
                    id = 2,
                    userId = 2,
                    fullName = "Trần Thị Bình",
                    type = "CHANGE_SHIFT",
                    reason = "Em muốn đổi sang ca chiều",
                    targetDate = "2026-06-28",
                    status = "PENDING"
                ),
                RequestDto(
                    id = 3,
                    userId = 5,
                    fullName = "Võ Minh Tuấn",
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
                    userId = 1,
                    fullName = "Nguyễn Văn An",
                    checkInTime = "2026-06-26 08:10",
                    checkOutTime = "2026-06-26 17:30",
                    status = "LATE",
                    checkInBssid = "A1:B2:C3:D4:E5:F6",
                    checkOutBssid = "A1:B2:C3:D4:E5:F6",
                    position = "SERVER",
                    positionName = "Phục vụ",
                    lateMinutes = 10,
                    overtimeMinutes = 30,
                    totalHours = 9.33
                ),
                AttendanceDto(
                    id = 2,
                    userId = 2,
                    fullName = "Trần Thị Bình",
                    checkInTime = "2026-06-26 08:00",
                    checkOutTime = "2026-06-26 17:00",
                    status = "VALID",
                    checkInBssid = "A1:B2:C3:D4:E5:F6",
                    checkOutBssid = "A1:B2:C3:D4:E5:F6",
                    position = "BARISTA",
                    positionName = "Pha chế",
                    lateMinutes = 0,
                    overtimeMinutes = 0,
                    totalHours = 9.0
                ),
                AttendanceDto(
                    id = 3,
                    userId = 4,
                    fullName = "Phạm Quốc Huy",
                    checkInTime = "2026-06-26 08:40",
                    checkOutTime = "2026-06-26 18:00",
                    status = "KITCHEN_ONLY_TIME",
                    checkInBssid = "A1:B2:C3:D4:E5:F6",
                    checkOutBssid = "A1:B2:C3:D4:E5:F6",
                    position = "KITCHEN",
                    positionName = "Bếp",
                    lateMinutes = 0,
                    overtimeMinutes = 0,
                    totalHours = 9.33
                ),
                AttendanceDto(
                    id = 4,
                    userId = 5,
                    fullName = "Võ Minh Tuấn",
                    checkInTime = "2026-06-26 08:05",
                    checkOutTime = "2026-06-26 17:20",
                    status = "VALID",
                    checkInBssid = "A1:B2:C3:D4:E5:F6",
                    checkOutBssid = "A1:B2:C3:D4:E5:F6",
                    position = "RUNNER",
                    positionName = "Tiếp thực",
                    lateMinutes = 5,
                    overtimeMinutes = 20,
                    totalHours = 9.25
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

    fun createScheduleForEmployees(
        workDate: String,
        shift: ShiftOption?,
        selectedStaff: List<StaffDto>
    ) {
        if (workDate.isBlank()) {
            _message.value = "Vui lòng nhập ngày làm"
            return
        }

        if (shift == null) {
            _message.value = "Vui lòng chọn ca làm"
            return
        }

        if (selectedStaff.size < 2) {
            _message.value = "Một ca làm cần ít nhất 2 nhân viên"
            return
        }

        val hasServer = selectedStaff.any { it.position == PositionType.SERVER.code }
        val hasBarista = selectedStaff.any { it.position == PositionType.BARISTA.code }

        if (!hasServer || !hasBarista) {
            _message.value = "Ca làm cần ít nhất 1 Phục vụ và 1 Pha chế"
            return
        }

        if (useMockData) {
            _message.value =
                "Đã tạo lịch ${shift.name} ngày $workDate cho ${selectedStaff.size} nhân viên"
            return
        }

        viewModelScope.launch {
            try {
                val body = CreateMultiScheduleRequest(
                    userIds = selectedStaff.map { it.id },
                    shiftId = shift.id,
                    workDate = workDate
                )

                val response = api.createMultiSchedule(body)

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

    fun createStaff(
        fullName: String,
        email: String,
        password: String,
        position: String,
        branchIdText: String
    ) {
        if (fullName.isBlank()) {
            _message.value = "Vui lòng nhập họ tên nhân viên"
            return
        }

        if (email.isBlank()) {
            _message.value = "Vui lòng nhập email nhân viên"
            return
        }

        if (password.isBlank()) {
            _message.value = "Vui lòng nhập mật khẩu tạm"
            return
        }

        if (position.isBlank()) {
            _message.value = "Vui lòng chọn chức vụ"
            return
        }

        val branchId = branchIdText.toIntOrNull()

        if (useMockData) {
            val positionType = PositionType.fromCode(position)

            val newStaff = StaffDto(
                id = (_staffList.value.maxOfOrNull { it.id } ?: 0) + 1,
                fullName = fullName,
                email = email,
                role = "STAFF",
                branchId = branchId,
                position = positionType.code,
                positionName = positionType.displayName,
                branchName = if (branchId != null) "Chi nhánh $branchId" else "Chi nhánh chính"
            )

            _staffList.value = _staffList.value + newStaff
            _message.value = "Đã tạo nhân viên mới"
            return
        }

        viewModelScope.launch {
            try {
                val body = CreateStaffRequest(
                    fullName = fullName,
                    email = email,
                    password = password,
                    role = "STAFF",
                    position = position,
                    branchId = branchId
                )

                val response = api.createStaff(body)

                if (response.isSuccessful) {
                    _message.value = "Tạo nhân viên thành công"
                    loadStaff()
                } else {
                    _message.value = "Tạo nhân viên thất bại"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi: ${e.message}"
            }
        }
    }

    // ================= ADMIN =================

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

    fun createBranch(
        name: String,
        address: String,
        wifiBssid: String,
        rewardRateText: String
    ) {
        if (name.isBlank()) {
            _message.value = "Vui lòng nhập tên chi nhánh"
            return
        }

        if (address.isBlank()) {
            _message.value = "Vui lòng nhập địa chỉ chi nhánh"
            return
        }

        if (wifiBssid.isBlank()) {
            _message.value = "Vui lòng nhập BSSID Wi-Fi"
            return
        }

        val rewardRate = rewardRateText.toDoubleOrNull() ?: 1.0

        if (useMockData) {
            val newBranch = BranchDto(
                id = (_branches.value.maxOfOrNull { it.id } ?: 0) + 1,
                name = name,
                address = address,
                wifiBssid = wifiBssid,
                rewardRate = rewardRate
            )

            _branches.value = _branches.value + newBranch
            _message.value = "Đã tạo chi nhánh mới"
            return
        }

        viewModelScope.launch {
            try {
                val body = BranchRequest(
                    name = name,
                    address = address,
                    wifiBssid = wifiBssid,
                    rewardRate = rewardRate
                )

                val response = api.createBranch(body)

                if (response.isSuccessful) {
                    _message.value = "Tạo chi nhánh thành công"
                    loadBranches()
                } else {
                    _message.value = "Tạo chi nhánh thất bại"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi: ${e.message}"
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

    fun loadPositionSalaries() {
        if (useMockData) {
            _positionSalaries.value = getMockPositionSalaries()
            _message.value = "Đang dùng dữ liệu mẫu"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.getPositionSalaries()

                if (response.isSuccessful) {
                    _positionSalaries.value = response.body() ?: emptyList()
                    _message.value = ""
                } else {
                    _message.value = "Không tải được lương chức vụ"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi kết nối server: ${e.message}"
            }
        }
    }

    fun updatePositionSalary(position: String, newRateText: String) {
        val newRate = newRateText.toDoubleOrNull()

        if (newRate == null || newRate <= 0) {
            _message.value = "Lương theo giờ không hợp lệ"
            return
        }

        if (useMockData) {
            _positionSalaries.value = _positionSalaries.value.map {
                if (it.position == position) {
                    it.copy(hourlyRate = newRate)
                } else {
                    it
                }
            }

            _message.value = "Đã cập nhật lương chức vụ"
            return
        }

        viewModelScope.launch {
            try {
                val body = UpdatePositionSalaryRequest(
                    hourlyRate = newRate
                )

                val response = api.updatePositionSalary(
                    position = position,
                    body = body
                )

                if (response.isSuccessful) {
                    _message.value = "Đã cập nhật lương chức vụ"
                    loadPositionSalaries()
                } else {
                    _message.value = "Cập nhật lương thất bại"
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
                    userId = 1,
                    fullName = "Nguyễn Văn An",
                    totalHours = 42.0,
                    salary = 1050000.0,
                    position = "SERVER",
                    positionName = "Phục vụ",
                    hourlyRate = 25000.0,
                    lateMinutes = 25,
                    overtimeMinutes = 120
                ),
                SalaryDto(
                    userId = 2,
                    fullName = "Trần Thị Bình",
                    totalHours = 40.0,
                    salary = 1200000.0,
                    position = "BARISTA",
                    positionName = "Pha chế",
                    hourlyRate = 30000.0,
                    lateMinutes = 0,
                    overtimeMinutes = 60
                ),
                SalaryDto(
                    userId = 4,
                    fullName = "Phạm Quốc Huy",
                    totalHours = 45.5,
                    salary = 1456000.0,
                    position = "KITCHEN",
                    positionName = "Bếp",
                    hourlyRate = 32000.0,
                    lateMinutes = 0,
                    overtimeMinutes = 0
                ),
                SalaryDto(
                    userId = 5,
                    fullName = "Võ Minh Tuấn",
                    totalHours = 38.0,
                    salary = 912000.0,
                    position = "RUNNER",
                    positionName = "Tiếp thực",
                    hourlyRate = 24000.0,
                    lateMinutes = 10,
                    overtimeMinutes = 30
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
}