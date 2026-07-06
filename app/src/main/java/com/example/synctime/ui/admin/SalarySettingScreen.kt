package com.example.synctime.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.data.model.PositionSalaryDto
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarySettingScreen(
    navController: NavController
) {
    val salarySettings = remember {
        mutableStateListOf(
            PositionSalaryDto(
                id = 1,
                position = "SERVER",
                positionName = "Phục vụ",
                hourlyRate = 25000.0,
                baseSalary = 8000000.0,
                overtimeRate = 1.5,
                latePenaltyPerMinute = 1000.0
            ),
            PositionSalaryDto(
                id = 2,
                position = "BARISTA",
                positionName = "Pha chế",
                hourlyRate = 30000.0,
                baseSalary = 9000000.0,
                overtimeRate = 1.5,
                latePenaltyPerMinute = 1000.0
            ),
            PositionSalaryDto(
                id = 3,
                position = "KITCHEN",
                positionName = "Bếp",
                hourlyRate = 32000.0,
                baseSalary = 9500000.0,
                overtimeRate = 1.5,
                latePenaltyPerMinute = 1000.0
            ),
            PositionSalaryDto(
                id = 4,
                position = "CASHIER",
                positionName = "Thu ngân",
                hourlyRate = 28000.0,
                baseSalary = 8500000.0,
                overtimeRate = 1.5,
                latePenaltyPerMinute = 1000.0
            ),
            PositionSalaryDto(
                id = 5,
                position = "MANAGER",
                positionName = "Quản lý",
                hourlyRate = 50000.0,
                baseSalary = 12000000.0,
                overtimeRate = 1.5,
                latePenaltyPerMinute = 1500.0
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cài đặt lương",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Thiết lập mức lương theo vị trí",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Các mức lương này dùng để tính báo cáo lương nhân viên.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            items(
                items = salarySettings,
                key = { item -> item.id }
            ) { item ->
                SalarySettingItem(item = item)
            }
        }
    }
}

@Composable
private fun SalarySettingItem(
    item: PositionSalaryDto
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.positionName.ifBlank { item.position },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = item.position,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        // TODO: mở màn hình sửa lương khi cần
                    }
                ) {
                    Text("Sửa")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            SalaryRow(
                label = "Lương giờ",
                value = formatMoney(item.hourlyRate)
            )

            SalaryRow(
                label = "Lương cơ bản",
                value = formatMoney(item.baseSalary)
            )

            SalaryRow(
                label = "Hệ số tăng ca",
                value = "x${item.overtimeRate}"
            )

            SalaryRow(
                label = "Phạt đi trễ / phút",
                value = formatMoney(item.latePenaltyPerMinute)
            )
        }
    }
}

@Composable
private fun SalaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatMoney(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(value)
}