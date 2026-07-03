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
//import com.example.synctime.ui.auth.LoginScreen
//import com.example.synctime.ui.staff.StaffHomeScreen
//import com.example.synctime.ui.staff.AttendanceScreen
//import com.example.synctime.ui.staff.MyScheduleScreen
//import com.example.synctime.ui.staff.CreateRequestScreen
//import com.example.synctime.ui.staff.RequestHistoryScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: ManagerAdminViewModel
) {
    NavHost(
        navController = navController,

        // Khi ghép chính thức thì nên để login
        // Nếu muốn test Manager/Admin tạm thời thì đổi lại thành "role_select"
        startDestination = "login"
    ) {

        // ================= LOGIN =================

//        composable("login") {
//            LoginScreen(navController)
//        }

        // Màn hình test tạm, có thể giữ lại để demo riêng phần 4
        composable("role_select") {
            RoleSelectScreen(navController)
        }

        // ================= STAFF =================

//        composable("staff_home") {
//            StaffHomeScreen(navController)
//        }
//
//        composable("attendance") {
//            AttendanceScreen(navController)
//        }
//
//        composable("my_schedule") {
//            MyScheduleScreen(navController)
//        }
//
//        composable("create_request") {
//            CreateRequestScreen(navController)
//        }
//
//        composable("request_history") {
//            RequestHistoryScreen(navController)
//        }

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
            description = "Chấm công, xem lịch làm, tạo đơn và xem lịch sử đơn",
            tag = "Staff",
            onClick = {
                navController.navigate("staff_home")
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