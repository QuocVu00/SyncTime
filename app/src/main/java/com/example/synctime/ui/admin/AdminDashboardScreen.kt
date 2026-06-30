package com.example.synctime.ui.admin

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.MenuActionCard

@Composable
fun AdminDashboardScreen(navController: NavController) {
    AppScreen {
        AppHeader(
            title = "Xin chào, Admin",
            subtitle = "Quản lý chi nhánh, BSSID và lương nhân viên"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MenuActionCard(
            title = "Quản lý chi nhánh / BSSID",
            description = "Cập nhật Wi-Fi hợp lệ cho từng chi nhánh",
            tag = "BSSID",
            onClick = {
                navController.navigate("branch_list")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuActionCard(
            title = "Cài đặt lương chức vụ",
            description = "Sửa lương theo giờ cho Phục vụ, Pha chế, Bếp, Tiếp thực...",
            tag = "Lương",
            onClick = {
                navController.navigate("salary_setting")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuActionCard(
            title = "Xem bảng lương",
            description = "Xem tổng giờ làm, lương theo chức vụ và tổng tiền",
            tag = "Report",
            onClick = {
                navController.navigate("salary_report")
            }
        )
    }
}