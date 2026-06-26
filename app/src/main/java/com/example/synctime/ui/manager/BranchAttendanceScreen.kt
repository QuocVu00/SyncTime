package com.example.synctime.ui.manager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.viewmodel.ManagerAdminViewModel

@Composable
fun BranchAttendanceScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    val attendance by viewModel.attendance.collectAsState()
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadManagerAttendance()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("← Quay lại")
        }

        Text(
            text = "Lịch sử chấm công",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (message.isNotBlank()) {
            Text(text = message)
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn {
            items(attendance) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Nhân viên: ${item.fullName ?: item.userId}")
                        Text("Giờ vào: ${item.checkInTime ?: "Chưa có"}")
                        Text("Giờ ra: ${item.checkOutTime ?: "Chưa có"}")
                        Text("Trạng thái: ${convertAttendanceStatus(item.status)}")
                        Text("BSSID vào: ${item.checkInBssid ?: "Không có"}")
                        Text("BSSID ra: ${item.checkOutBssid ?: "Không có"}")
                    }
                }
            }
        }
    }
}

private fun convertAttendanceStatus(status: String): String {
    return when (status) {
        "VALID" -> "Hợp lệ"
        "LATE" -> "Đi trễ"
        "INVALID_DEVICE" -> "Sai thiết bị"
        "INVALID_WIFI" -> "Sai Wi-Fi/BSSID"
        "MISSING_CHECKOUT" -> "Chưa ra ca"
        else -> status
    }
}