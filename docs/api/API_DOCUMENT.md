# SyncTime API Documentation

Tài liệu hướng dẫn kết nối API cho ứng dụng quản lý chấm công SyncTime.

**Base URL:** `http://your-api-domain.com/` (Hoặc Localhost IP cho phát triển)

---

## 1. Authentication (Xác thực)

Tất cả các API (ngoại trừ Login) đều yêu cầu Header xác thực:
`Authorization: Bearer <your_token>`

### Đăng nhập
- **Endpoint:** `POST /api/auth/login`
- **Body:** `LoginRequest`
- **Response:** `ApiResponse<LoginResponse>`

---

## 2. Staff APIs (Dành cho Nhân viên)

### Lấy thông tin cá nhân
- **Endpoint:** `GET /api/staff/profile`
- **Response:** `ApiResponse<StaffDto>`

### Chấm công Check-in
- **Endpoint:** `POST /api/staff/check-in`
- **Body:** `CheckInRequest` (Gửi kèm `bssid` và `android_id`)
- **Response:** `ApiResponse<String>`

### Chấm công Check-out
- **Endpoint:** `POST /api/staff/check-out`
- **Body:** `CheckOutRequest`
- **Response:** `ApiResponse<String>`

### Gửi yêu cầu (Nghỉ phép/Đổi ca)
- **Endpoint:** `POST /api/staff/requests`
- **Body:** `StaffRequest`
- **Response:** `ApiResponse<String>`

---

## 3. Manager APIs (Dành cho Quản lý)

### Xem danh sách nhân viên chi nhánh
- **Endpoint:** `GET /api/manager/staff`
- **Response:** `ApiResponse<List<StaffDto>>`

### Phê duyệt yêu cầu
- **Endpoint:** `POST /api/manager/requests/{id}/approve`
- **Response:** `ApiResponse<String>`

### Tạo lịch làm việc (Nhiều nhân viên)
- **Endpoint:** `POST /api/manager/schedules/multiple`
- **Body:** `CreateMultiScheduleRequest`
- **Response:** `ApiResponse<String>`

---

## 4. Admin APIs (Dành cho Quản trị viên)

### Quản lý chi nhánh
- **Endpoint:** `GET /api/admin/branches` | `POST /api/admin/branches` | `PUT /api/admin/branches/{id}`
- **Body:** `BranchRequest`

### Báo cáo lương tổng hợp
- **Endpoint:** `GET /api/admin/salary-report`
- **Response:** `ApiResponse<List<SalaryDto>>`

### Thiết lập lương theo chức vụ
- **Endpoint:** `GET /api/admin/position-salaries` | `PUT /api/admin/position-salaries/{position}`

---

## 5. Quy định mã lỗi (Error Codes)

| Mã lỗi | Mô tả |
| :--- | :--- |
| `SUCCESS` | Thao tác thành công |
| `AUTH_FAILED` | Sai email hoặc mật khẩu |
| `INVALID_TOKEN` | Token hết hạn hoặc không hợp lệ |
| `INVALID_LOCATION` | Sai địa chỉ Wifi BSSID |
| `INVALID_DEVICE` | Thiết bị không đúng với đăng ký |
| `SERVER_ERROR` | Lỗi hệ thống |
