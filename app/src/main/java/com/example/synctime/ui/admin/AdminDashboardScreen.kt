package com.example.synctime.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.ui.components.AppCard
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.MenuActionCard

@Composable
fun AdminDashboardScreen(navController: NavController) {
    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
            ) {
                AppHeader(
                    title = "Xin chào, Admin"
                )

                Spacer(modifier = Modifier.height(16.dp))

                MenuActionCard(
                    title = "Tạo chi nhánh mới",
                    description = "Thêm chi nhánh mới và nhập BSSID Wi-Fi để chấm công",
                    tag = "New",
                    onClick = {
                        navController.navigate("create_branch")
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                MenuActionCard(
                    title = "Quản lý chi nhánh / BSSID",
                    description = "Xem danh sách chi nhánh và cập nhật BSSID Wi-Fi",
                    tag = "BSSID",
                    onClick = {
                        navController.navigate("branch_list")
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                MenuActionCard(
                    title = "Cài đặt lương chức vụ",
                    description = "Sửa lương theo giờ cho nhân viên",
                    tag = "Lương",
                    onClick = {
                        navController.navigate("salary_setting")
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                MenuActionCard(
                    title = "Xem bảng lương",
                    description = "Xem tổng giờ làm",
                    tag = "Report",
                    onClick = {
                        navController.navigate("salary_report")
                    }
                )
            }
        }
    }
}