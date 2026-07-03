package com.example.synctime.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.synctime.ui.admin.AdminDashboardScreen
import com.example.synctime.ui.admin.BranchListScreen
import com.example.synctime.ui.admin.CreateBranchScreen
import com.example.synctime.ui.admin.SalaryReportScreen
import com.example.synctime.ui.admin.SalarySettingScreen

import com.example.synctime.ui.components.AppCard
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.BadgeType
import com.example.synctime.ui.components.MenuActionCard
import com.example.synctime.ui.components.StatusBadge

import com.example.synctime.ui.manager.BranchAttendanceScreen
import com.example.synctime.ui.manager.CreateStaffScreen
import com.example.synctime.ui.manager.ManagerDashboardScreen
import com.example.synctime.ui.manager.RequestApprovalScreen
import com.example.synctime.ui.manager.ScheduleManagementScreen

import com.example.synctime.viewmodel.ManagerAdminViewModel

// ===== IMPORT PHẦN 3 STAFF / LOGIN =====
// Tạm thời comment vì project hiện tại chưa có LoginScreen / StaffHomeScreen dạng Compose
// Khi ghép chính thức thì mở lại các import này và khai báo route tương ứng
// import com.example.synctime.ui.auth.LoginScreen
// import com.example.synctime.ui.staff.StaffHomeScreen
// import com.example.synctime.ui.staff.AttendanceScreen
// import com.example.synctime.ui.staff.MyScheduleScreen
// import com.example.synctime.ui.staff.CreateRequestScreen
// import com.example.synctime.ui.staff.RequestHistoryScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: ManagerAdminViewModel
) {
    NavHost(
        navController = navController,

        /*
            TẠM THỜI ĐỂ TEST APP:
            - Dùng "role_select" để app mở lên không bị crash.
            - Lý do: route "login" chưa được khai báo vì LoginScreen đang bị comment.

            KHI GHÉP CHÍNH THỨC:
            - Tạo hoặc mở lại LoginScreen.
            - Thêm composable("login") bên dưới.
            - Sau đó đổi lại:
              startDestination = "login"
        */
        startDestination = "role_select"
    ) {

        // ================= LOGIN =================

        /*
        KHI CÓ LoginScreen HOÀN CHỈNH THÌ MỞ LẠI ĐOẠN NÀY:

        composable("login") {
            LoginScreen(navController)
        }
        */

        // Màn hình chọn role tạm thời để test phần Manager/Admin
        composable("role_select") {
            RoleSelectScreen(navController)
        }

        // ================= STAFF =================

        /*
            PHẦN STAFF HIỆN TẠI:
            - Code của Khoa đã được thêm vào project.
            - Nhưng code đó đang là Activity XML:
              CreateRequestActivity.kt
              MyScheduleActivity.kt
              ViewRequestsActivity.kt

            Project hiện tại lại đang dùng Compose Navigation.
            Vì vậy chưa thể navigate("staff_home") nếu chưa có route staff_home.

            KHI GHÉP CHÍNH THỨC CÓ 2 CÁCH:

            Cách 1:
            - Chuyển các màn staff sang Compose.
            - Sau đó mở lại các route dưới đây.

            composable("staff_home") {
                StaffHomeScreen(navController)
            }

            composable("attendance") {
                AttendanceScreen(navController)
            }

            composable("my_schedule") {
                MyScheduleScreen(navController)
            }

            composable("create_request") {
                CreateRequestScreen(navController)
            }

            composable("request_history") {
                RequestHistoryScreen(navController)
            }

            Cách 2:
            - Giữ Activity XML của Khoa.
            - Khai báo Activity trong AndroidManifest.xml.
            - Mở màn staff bằng Intent thay vì navController.navigate().
        */

        // ================= MANAGER =================

        composable("manager_dashboard") {
            ManagerDashboardScreen(navController)
        }

        composable("create_staff") {
            CreateStaffScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("request_approval") {
            RequestApprovalScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("schedule_management") {
            ScheduleManagementScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("branch_attendance") {
            BranchAttendanceScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // ================= ADMIN =================

        composable("admin_dashboard") {
            AdminDashboardScreen(navController)
        }

        composable("branch_list") {
            BranchListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("create_branch") {
            CreateBranchScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("salary_setting") {
            SalarySettingScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("salary_report") {
            SalaryReportScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun RoleSelectScreen(navController: NavHostController) {
    AppScreen {
        Spacer(modifier = Modifier.height(32.dp))

        AppHeader(
            title = "SyncTime",
            subtitle = "Ứng dụng chấm công nhân viên"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppCard {
            StatusBadge(
                text = "Modern Young Office",
                type = BadgeType.INFO
            )

            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.material3.Text(
                text = "Màn hình demo tạm thời để kiểm tra phần Manager/Admin. Khi ghép với Login, app sẽ tự điều hướng theo role.",
                color = com.example.synctime.ui.theme.AppColors.TextSecondary,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MenuActionCard(
            title = "Vào màn hình Staff",
            description = "Phần Staff của Khoa đã thêm vào project nhưng chưa gắn vào Compose Navigation.",
            tag = "Staff",
            onClick = {
                /*
                    TẠM THỜI ĐỂ TRỐNG ĐỂ APP KHÔNG CRASH.

                    Lý do:
                    - Trước đó gọi navController.navigate("staff_home")
                    - Nhưng route "staff_home" chưa được khai báo trong NavHost
                    - Nên bấm vào sẽ crash giống lỗi:
                      navigation destination staff_home is not a direct child of this NavGraph

                    Khi ghép chính thức:
                    - Tạo StaffHomeScreen dạng Compose rồi thêm composable("staff_home")
                    - Hoặc mở Activity staff của Khoa bằng Intent
                */
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuActionCard(
            title = "Vào màn hình Manager",
            description = "Tạo nhân viên, tạo lịch làm, duyệt đơn và xem lịch sử chấm công",
            tag = "Manager",
            onClick = {
                navController.navigate("manager_dashboard")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuActionCard(
            title = "Vào màn hình Admin",
            description = "Quản lý chi nhánh, BSSID, cài đặt lương và xem bảng lương",
            tag = "Admin",
            onClick = {
                navController.navigate("admin_dashboard")
            }
        )
    }
}