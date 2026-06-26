package com.example.synctime.ui.manager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.viewmodel.ManagerAdminViewModel

@Composable
fun ScheduleManagementScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    var userId by remember { mutableStateOf("") }
    var shiftId by remember { mutableStateOf("") }
    var workDate by remember { mutableStateOf("") }

    val message by viewModel.message.collectAsState()

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
            text = "Tạo lịch làm",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Màn hình đơn giản để Manager tạo lịch làm cho Staff"
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userId,
            onValueChange = {
                userId = it
            },
            label = {
                Text("User ID nhân viên")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = shiftId,
            onValueChange = {
                shiftId = it
            },
            label = {
                Text("Shift ID")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = workDate,
            onValueChange = {
                workDate = it
            },
            label = {
                Text("Ngày làm, ví dụ 2026-06-26")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.createSchedule(
                    userId = userId,
                    shiftId = shiftId,
                    workDate = workDate
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tạo lịch")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotBlank()) {
            Text(text = message)
        }
    }
}