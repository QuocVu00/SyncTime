package com.example.synctime.ui.manager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.synctime.data.model.AttendanceDto
import com.example.synctime.data.model.PositionType
import com.example.synctime.ui.components.AppCard
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.BadgeType
import com.example.synctime.ui.components.SectionTitle
import com.example.synctime.ui.components.StatusBadge
import com.example.synctime.ui.theme.AppColors
import com.example.synctime.viewmodel.ManagerAdminViewModel
import java.util.Locale

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

    val totalStaff = attendance.size
    val lateCount = attendance.count { it.lateMinutes > 0 }
    val overtimeCount = attendance.count { it.overtimeMinutes > 0 }

    AppScreen {
        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("← Quay lại")
        }

        AppHeader(
            title = "Lịch sử chấm công",
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppCard {
            Text(
                text = "Tổng quan hôm nay",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            AttendanceOverviewLine(
                label = "Số lượt chấm công",
                value = "$totalStaff"
            )

            AttendanceOverviewLine(
                label = "Có đi trễ",
                value = "$lateCount"
            )

            AttendanceOverviewLine(
                label = "Có tăng ca",
                value = "$overtimeCount"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (message.isNotBlank()) {
            StatusBadge(
                text = message,
                type = getAttendanceMessageType(message)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        SectionTitle(
            title = "Danh sách chấm công",
        )

        LazyColumn {
            items(attendance) { item ->
                AttendanceCard(item = item)

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AttendanceOverviewLine(
    label: String,
    value: String
) {
    Row(
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
private fun AttendanceCard(
    item: AttendanceDto
) {
    val positionType = PositionType.fromCode(item.position)
    val isKitchen = positionType == PositionType.KITCHEN

    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.fullName ?: "Nhân viên #${item.userId}",
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
                text = getAttendanceStatusText(item, isKitchen),
                type = getAttendanceBadgeType(item, isKitchen)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AttendanceLine(
            label = "Giờ vào",
            value = item.checkInTime ?: "Chưa có"
        )

        AttendanceLine(
            label = "Giờ ra",
            value = item.checkOutTime ?: "Chưa có"
        )

        AttendanceLine(
            label = "Tổng giờ",
            value = "${formatHour(item.totalHours)} giờ"
        )

        if (isKitchen) {
            AttendanceLine(
                label = "Trễ / tăng ca",
                value = "Không áp dụng"
            )
        } else {
            AttendanceLine(
                label = "Đi trễ",
                value = "${item.lateMinutes} phút"
            )

            AttendanceLine(
                label = "Tăng ca",
                value = "${item.overtimeMinutes} phút"
            )
        }

        AttendanceLine(
            label = "BSSID vào",
            value = item.checkInBssid ?: "Không có"
        )

        AttendanceLine(
            label = "BSSID ra",
            value = item.checkOutBssid ?: "Không có"
        )
    }
}

@Composable
private fun AttendanceLine(
    label: String,
    value: String
) {
    Row(
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

private fun getAttendanceStatusText(
    item: AttendanceDto,
    isKitchen: Boolean
): String {
    if (isKitchen) {
        return "Ghi nhận giờ"
    }

    return when {
        item.lateMinutes > 0 -> "Đi trễ"
        item.overtimeMinutes > 0 -> "Tăng ca"
        item.status == "VALID" -> "Hợp lệ"
        item.status == "INVALID_WIFI" -> "Sai Wi-Fi"
        else -> convertAttendanceStatus(item.status)
    }
}

private fun getAttendanceBadgeType(
    item: AttendanceDto,
    isKitchen: Boolean
): BadgeType {
    if (isKitchen) {
        return BadgeType.NEUTRAL
    }

    return when {
        item.status == "INVALID_WIFI" -> BadgeType.ERROR
        item.lateMinutes > 0 -> BadgeType.WARNING
        item.overtimeMinutes > 0 -> BadgeType.INFO
        item.status == "VALID" -> BadgeType.SUCCESS
        else -> BadgeType.NEUTRAL
    }
}

private fun convertAttendanceStatus(status: String): String {
    return when (status) {
        "VALID" -> "Hợp lệ"
        "LATE" -> "Đi trễ"
        "INVALID_DEVICE" -> "Sai thiết bị"
        "INVALID_WIFI" -> "Sai Wi-Fi/BSSID"
        "MISSING_CHECKOUT" -> "Chưa ra ca"
        "KITCHEN_ONLY_TIME" -> "Ghi nhận giờ"
        else -> status
    }
}

private fun formatHour(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", value)
    }
}

private fun getAttendanceMessageType(message: String): BadgeType {
    return when {
        message.contains("lỗi", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Không tải", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Đang dùng", ignoreCase = true) -> BadgeType.INFO
        else -> BadgeType.INFO
    }
}