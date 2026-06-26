package com.example.synctime.ui.manager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun RequestApprovalScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    val requests by viewModel.requests.collectAsState()
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRequests()
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
            text = "Duyệt đơn nhân viên",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (message.isNotBlank()) {
            Text(text = message)
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn {
            items(requests) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Mã đơn: ${item.id}")
                        Text("Nhân viên: ${item.fullName ?: item.userId}")
                        Text("Loại đơn: ${convertRequestType(item.type)}")
                        Text("Ngày áp dụng: ${item.targetDate}")
                        Text("Lý do: ${item.reason}")
                        Text("Trạng thái: ${convertStatus(item.status)}")

                        Spacer(modifier = Modifier.height(12.dp))

                        if (item.status == "PENDING") {
                            Row {
                                Button(
                                    onClick = {
                                        viewModel.approveRequest(item.id)
                                    }
                                ) {
                                    Text("Duyệt")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        viewModel.rejectRequest(item.id)
                                    }
                                ) {
                                    Text("Từ chối")
                                }
                            }
                        } else {
                            Text("Đơn này đã được xử lý")
                        }
                    }
                }
            }
        }
    }
}

private fun convertRequestType(type: String): String {
    return when (type) {
        "LEAVE" -> "Xin nghỉ"
        "CHANGE_SHIFT" -> "Đổi ca"
        "UPDATE_BSSID" -> "Cập nhật BSSID"
        else -> type
    }
}

private fun convertStatus(status: String): String {
    return when (status) {
        "PENDING" -> "Đang chờ duyệt"
        "APPROVED" -> "Đã duyệt"
        "REJECTED" -> "Đã từ chối"
        else -> status
    }
}