# Hệ thống Chấm công Nhân viên (Staff Attendance System)

Đây là ứng dụng Android dành cho nhân viên để thực hiện chấm công, xem lịch làm việc và quản lý các yêu cầu nghỉ phép/đổi ca.

## Các tính năng chính (Phía Staff)

1. **Đăng nhập**: Hệ thống xác thực nhân viên.
   - Tài khoản demo: `demo` / `123456`
2. **Chấm công (Vào ca/Ra ca)**:
   - Tự động nhận diện thiết bị qua `Android ID`.
   - Kiểm tra vị trí làm việc qua địa chỉ `BSSID` của WiFi cửa hàng.
   - Yêu cầu bật WiFi và GPS để đảm bảo tính chính xác.
3. **Xem lịch làm việc**:
   - Hiển thị danh sách các ca làm việc trong tuần/tháng.
   - Giao diện trực quan với các thẻ màu phân loại ca.
4. **Quản lý yêu cầu (Requests)**:
   - Tạo đơn xin nghỉ hoặc đổi ca làm việc.
   - Theo dõi trạng thái đơn (Đang chờ, Đã duyệt, Bị từ chối) theo thời gian thực.
5. **Tính năng bổ trợ**:
   - **Swipe-to-refresh**: Vuốt để cập nhật trạng thái mới nhất.
   - **Logout**: Đăng xuất an toàn.
   - **Demo Mode**: Tích hợp dữ liệu giả lập để thuyết trình không cần server thật.

## Hướng dẫn cài đặt & Chạy Demo

1. Mở project bằng Android Studio.
2. Thực hiện **Sync Project with Gradle Files**.
3. Chạy ứng dụng trên Emulator hoặc thiết bị thật (Android 7.0+).
4. Sử dụng tài khoản `demo` để trải nghiệm toàn bộ luồng công việc.

## Công nghệ sử dụng

- **Ngôn ngữ**: Kotlin
- **Kiến trúc**: Material Design 3
- **Thư viện**:
  - `Retrofit` & `Gson`: Giao tiếp API.
  - `SwipeRefreshLayout`: Tối ưu trải nghiệm làm mới dữ liệu.
  - `Material Components`: Giao diện hiện đại.
  - `Retrofit Mock`: Phục vụ chế độ Demo.

---
*Sản phẩm hoàn thiện cho bài tập lớn môn Phát triển ứng dụng di động.*
