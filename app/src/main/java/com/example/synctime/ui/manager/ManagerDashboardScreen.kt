package com.example.synctime.ui.manager

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
fun ManagerDashboardScreen(navController: NavController) {
    AppScreen {
        AppHeader(
            title = "Xin chào, Manager",
            subtitle = "Quản lý lịch làm, đơn yêu cầu và chấm công nhân viên"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MenuActionCard(
            title = "Duyệt đơn nhân viên",
            description = "Xem và xử lý đơn xin nghỉ, đổi ca của nhân viên",
            tag = "Đơn",
            onClick = {
                navController.navigate("request_approval")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuActionCard(
            title = "Tạo lịch làm",
            description = "Chọn ngày, ca và nhiều nhân viên cho một ca làm",
            tag = "Lịch",
            onClick = {
                navController.navigate("schedule_management")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuActionCard(
            title = "Lịch sử chấm công",
            description = "Theo dõi giờ vào, giờ ra, trễ và tăng ca",
            tag = "Công",
            onClick = {
                navController.navigate("branch_attendance")
            }
        )
    }
}