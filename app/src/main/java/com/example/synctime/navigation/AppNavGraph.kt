package com.example.synctime.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.synctime.ui.admin.AdminDashboardScreen
import com.example.synctime.ui.admin.BranchListScreen
import com.example.synctime.ui.admin.SalaryReportScreen
import com.example.synctime.ui.admin.SalarySettingScreen
import com.example.synctime.ui.manager.BranchAttendanceScreen
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
        startDestination = "role_select"
    ) {
        composable("role_select") {
            RoleSelectScreen(navController)
        }

        // ================= MANAGER =================

        composable("manager_dashboard") {
            ManagerDashboardScreen(navController)
        }

        composable("request_approval") {
            RequestApprovalScreen(
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

        composable("schedule_management") {
            ScheduleManagementScreen(
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SyncTime Demo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Màn hình tạm để test phần Manager/Admin của thành viên 4"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("manager_dashboard")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Vào màn hình Manager")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("admin_dashboard")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Vào màn hình Admin")
        }
    }
}