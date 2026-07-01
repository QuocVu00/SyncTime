package com.example.synctime.ui.manager

import androidx.compose.foundation.layout.Box
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
import com.example.synctime.ui.components.SectionTitle

@Composable
fun ManagerDashboardScreen(navController: NavController) {
    AppScreen {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppHeader(
                        title = "Xin chào, Manager",
                        subtitle = "Quản lý nhân viên, lịch làm và chấm công"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionTitle(
                        title = "Chức năng quản lý",
                    )

                    MenuActionCard(
                        title = "Tạo nhân viên mới",
                        description = "Thêm nhân viên",
                        tag = "Nhân viên",
                        onClick = {
                            navController.navigate("create_staff")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MenuActionCard(
                        title = "Duyệt đơn nhân viên",
                        description = "Xem và xử lý đơn xin nghỉ, đổi ca của nhân viên",
                        tag = "Xem",
                        onClick = {
                            navController.navigate("request_approval")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MenuActionCard(
                        title = "Tạo lịch làm",
                        description = "Chọn ngày, ca và nhiều nhân viên cho một ca làm",
                        tag = "Xếp Lịch",
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
        }
    }
}