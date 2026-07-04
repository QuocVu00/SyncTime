# Hướng dẫn đóng góp cho SyncTime

Để đảm bảo mã nguồn luôn sạch và dễ bảo trì, vui lòng tuân thủ các quy tắc sau khi tham gia phát triển dự án.

## 1. Coding Convention (Quy tắc viết code)

### Đặt tên (Naming)
- **Classes/Enums**: PascalCase (ví dụ: `UserEntity`, `PositionType`).
- **Variables/Functions**: camelCase (ví dụ: `getUserInfo()`, `isLoggedIn`).
- **Constants**: UPPER_SNAKE_CASE (ví dụ: `BASE_URL`, `MAX_RETRY`).
- **Layouts/Resources**: snake_case (ví dụ: `ic_home.xml`, `bg_button.png`).

### Cấu trúc file
- Mỗi file chỉ nên chứa một lớp chính.
- Các hàm trong ViewModel phải sử dụng `viewModelScope` và xử lý lỗi qua `Result` hoặc `ApiResponse`.

## 2. Quy trình làm việc (Git Workflow)
1. **Branch**: Tạo nhánh mới từ `main` cho mỗi tính năng.
   - Định dạng: `feature/ten-tinh-nang` hoặc `fix/ten-loi`.
2. **Commit Message**: Viết tiếng Anh hoặc tiếng Việt rõ ràng.
   - Ví dụ: `feat: add check-in logic with wifi verification`.
3. **Pull Request**: Mô tả ngắn gọn những gì bạn đã thay đổi.

## 3. Kiến trúc khuyến nghị
- Luôn ưu tiên sử dụng **Mappers** để chuyển đổi dữ liệu khi di chuyển giữa các lớp (Entity -> DTO -> UI Model).
- Không để logic xử lý dữ liệu nặng trong UI (Compose). Hãy đẩy vào ViewModel hoặc UseCase.

---
Cảm ơn bạn đã đóng góp cho SyncTime!
