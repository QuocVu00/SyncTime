package com.example.synctime.navigation

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.synctime.CreateRequestActivity
import com.example.synctime.MyScheduleActivity
import com.example.synctime.ViewRequestsActivity

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

        // Màn hình chọn role tạm thời để test phần Manager/Admin/Staff
        composable("role_select") {
            RoleSelectScreen(navController)
        }

        // ================= STAFF =================

        /*
            PHẦN STAFF HIỆN TẠI:
            - Code của Khoa đã được thêm vào project.
            - Code đó đang là Activity XML:
              CreateRequestActivity.kt
              MyScheduleActivity.kt
              ViewRequestsActivity.kt

            Hiện tại mình mở Staff bằng Intent trong RoleSelectScreen.
            Sau này nếu làm chuẩn thì nên chuyển Staff sang Compose
            rồi khai báo route trong NavHost.
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
    val context = LocalContext.current

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
                text = "Màn hình demo tạm thời để kiểm tra phần Manager/Admin/Staff. Khi ghép với Login, app sẽ tự điều hướng theo role.",
                color = com.example.synctime.ui.theme.AppColors.TextSecondary,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MenuActionCard(
            title = "Staff - Xem lịch làm",
            description = "Mở màn hình lịch làm Staff của Khoa",
            tag = "Staff",
            onClick = {
                context.startActivity(
                    Intent(context, MyScheduleActivity::class.java)
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuActionCard(
            title = "Staff - Tạo yêu cầu",
            description = "Mở màn hình tạo đơn/yêu cầu của Staff",
            tag = "Staff",
            onClick = {
                context.startActivity(
                    Intent(context, CreateRequestActivity::class.java)
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuActionCard(
            title = "Staff - Xem yêu cầu",
            description = "Mở màn hình danh sách yêu cầu đã gửi",
            tag = "Staff",
            onClick = {
                context.startActivity(
                    Intent(context, ViewRequestsActivity::class.java)
                )
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