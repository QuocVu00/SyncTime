package com.example.synctime.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.data.model.PositionType
import com.example.synctime.data.model.SalaryDto
import com.example.synctime.ui.components.AppCard
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.BadgeType
import com.example.synctime.ui.components.SectionTitle
import com.example.synctime.ui.components.StatusBadge
import com.example.synctime.ui.theme.AppColors
import com.example.synctime.viewmodel.ManagerAdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SalaryReportScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    val salaryList by viewModel.salary.collectAsState()
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSalaryReport()
    }

    val totalSalary = salaryList.sumOf { it.salary }
    val totalHours = salaryList.sumOf { it.totalHours }

    AppScreen {
        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("← Quay lại")
        }

        AppHeader(
            title = "Bảng lương nhân viên",
        )

        Spacer(modifier = Modifier.height(16.dp))

        SalaryOverviewCard(
            totalStaff = salaryList.size,
            totalHours = totalHours,
            totalSalary = totalSalary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Spacer(modifier = Modifier.height(12.dp))

        if (message.isNotBlank()) {
            StatusBadge(
                text = message,
                type = getSalaryReportMessageType(message)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        SectionTitle(
            title = "Danh sách lương",
        )

        LazyColumn {
            items(salaryList) { item ->
                SalaryEmployeeCard(item = item)

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SalaryOverviewCard(
    totalStaff: Int,
    totalHours: Double,
    totalSalary: Double
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    AppCard {
        Text(
            text = "Tổng quan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        OverviewLine(
            label = "Số nhân viên",
            value = "$totalStaff"
        )

        OverviewLine(
            label = "Tổng giờ làm",
            value = "${formatHour(totalHours)} giờ"
        )

        OverviewLine(
            label = "Tổng lương",
            value = currencyFormat.format(totalSalary)
        )
    }
}

@Composable
private fun OverviewLine(
    label: String,
    value: String
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextSecondary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )
    }
}

@Composable
private fun SalaryEmployeeCard(
    item: SalaryDto
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val positionType = PositionType.fromCode(item.position)
    val isKitchen = positionType == PositionType.KITCHEN

    AppCard {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.positionName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
            }

            StatusBadge(
                text = if (isKitchen) "Bếp" else "Có trễ/TC",
                type = if (isKitchen) BadgeType.NEUTRAL else BadgeType.INFO
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        SalaryLine(
            label = "Lương theo giờ",
            value = "${currencyFormat.format(item.hourlyRate)}/giờ"
        )

        SalaryLine(
            label = "Tổng giờ làm",
            value = "${formatHour(item.totalHours)} giờ"
        )

        if (isKitchen) {
            SalaryLine(
                label = "Trễ / tăng ca",
                value = "Không áp dụng"
            )
        } else {
            SalaryLine(
                label = "Đi trễ",
                value = "${item.lateMinutes} phút"
            )

            SalaryLine(
                label = "Tăng ca",
                value = "${item.overtimeMinutes} phút"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "Tổng lương",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Text(
                text = currencyFormat.format(item.salary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary
            )
        }
    }
}

@Composable
private fun SalaryLine(
    label: String,
    value: String
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextSecondary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )
    }
}

private fun formatHour(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", value)
    }
}

private fun getSalaryReportMessageType(message: String): BadgeType {
    return when {
        message.contains("lỗi", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Không tải", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Đang dùng", ignoreCase = true) -> BadgeType.INFO
        else -> BadgeType.INFO
    }
}