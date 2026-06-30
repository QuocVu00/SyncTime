package com.example.baitaplon.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.mock.Calls

class MockApiService : ApiService {
    override fun login(request: LoginRequest): Call<LoginResponse> {
        return if (request.username == "demo" && request.password == "123456") {
            Calls.response(LoginResponse(true, "Thành công", "mock_token_123"))
        } else {
            Calls.response(LoginResponse(false, "Sai tài khoản hoặc mật khẩu", null))
        }
    }

    override fun checkIn(request: CheckInRequest): Call<CheckInResponse> {
        return Calls.response(CheckInResponse(true, "Vào ca thành công (Demo)"))
    }

    override fun checkOut(request: CheckOutRequest): Call<CheckOutResponse> {
        return Calls.response(CheckOutResponse(true, "Ra ca thành công (Demo)"))
    }

    override fun getAttendanceStatus(request: Map<String, String>): Call<AttendanceStatusResponse> {
        return Calls.response(AttendanceStatusResponse(
            success = true,
            status = "In progress",
            checkInTime = "08:05:22",
            checkOutTime = null
        ))
    }

    override fun getMySchedule(request: Map<String, String>): Call<ScheduleResponse> {
        val items = listOf(
            ScheduleItem("28/06/2026", "Ca sáng", "08:00", "12:00", "Khai trương cửa hàng"),
            ScheduleItem("29/06/2026", "Ca chiều", "13:00", "17:00", null),
            ScheduleItem("30/06/2026", "Ca tối", "18:00", "22:00", "Kiểm kho định kỳ")
        )
        return Calls.response(ScheduleResponse(true, items))
    }

    override fun createRequest(request: LeaveRequest): Call<LeaveResponse> {
        return Calls.response(LeaveResponse(true, "Gửi đơn thành công (Demo)"))
    }

    override fun getMyRequests(request: Map<String, String>): Call<StaffRequestsResponse> {
        val items = listOf(
            StaffRequestItem("1", "Leave", "05/07/2026", "Đi khám bệnh", "Approved", System.currentTimeMillis()),
            StaffRequestItem("2", "Shift Change", "02/07/2026", "Bận việc gia đình", "Pending", System.currentTimeMillis())
        )
        return Calls.response(StaffRequestsResponse(true, items))
    }
}
