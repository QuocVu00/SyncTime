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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.data.model.BranchDto
import com.example.synctime.ui.components.AppCard
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.AppTextField
import com.example.synctime.ui.components.BadgeType
import com.example.synctime.ui.components.PrimaryButton
import com.example.synctime.ui.components.SectionTitle
import com.example.synctime.ui.components.StatusBadge
import com.example.synctime.ui.theme.AppColors
import com.example.synctime.viewmodel.ManagerAdminViewModel

@Composable
fun BranchListScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    val branches by viewModel.branches.collectAsState()
    val message by viewModel.message.collectAsState()

    val bssidInputs = remember {
        mutableStateMapOf<Int, String>()
    }

    LaunchedEffect(Unit) {
        viewModel.loadBranches()
    }

    AppScreen {
        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("← Quay lại")
        }

        AppHeader(
            title = "Quản lý chi nhánh / BSSID",
            subtitle = "Tạo chi nhánh mới và cập nhật Wi-Fi hợp lệ"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Tạo chi nhánh mới",
            onClick = {
                navController.navigate("create_branch")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppCard {
            Text(
                text = "Lưu ý",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Nhân viên chỉ chấm công hợp lệ khi thiết bị đang kết nối đúng Wi-Fi/BSSID của chi nhánh.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (message.isNotBlank()) {
            StatusBadge(
                text = message,
                type = getBranchMessageType(message)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        SectionTitle(
            title = "Danh sách chi nhánh",
            subtitle = "Admin có thể sửa BSSID khi Wi-Fi công ty thay đổi"
        )

        LazyColumn {
            items(branches) { branch ->
                val currentText = bssidInputs[branch.id] ?: branch.wifiBssid

                BranchCard(
                    branch = branch,
                    bssidValue = currentText,
                    onBssidChange = { newValue ->
                        bssidInputs[branch.id] = newValue
                    },
                    onUpdateClick = {
                        viewModel.updateBranchBssid(
                            branch = branch,
                            newBssid = currentText
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun BranchCard(
    branch: BranchDto,
    bssidValue: String,
    onBssidChange: (String) -> Unit,
    onUpdateClick: () -> Unit
) {
    AppCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = branch.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = branch.address,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            StatusBadge(
                text = "BSSID hiện tại",
                type = BadgeType.INFO
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = branch.wifiBssid,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = bssidValue,
                onValueChange = onBssidChange,
                label = "BSSID Wi-Fi mới",
                placeholder = "Ví dụ: A1:B2:C3:D4:E5:F6"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "Cập nhật BSSID",
                onClick = onUpdateClick
            )
        }
    }
}

private fun getBranchMessageType(message: String): BadgeType {
    return when {
        message.contains("Đã tạo", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("Đã cập nhật", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("không được", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Vui lòng", ignoreCase = true) -> BadgeType.WARNING
        message.contains("thất bại", ignoreCase = true) -> BadgeType.ERROR
        message.contains("lỗi", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Không tải", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Đang dùng", ignoreCase = true) -> BadgeType.INFO
        else -> BadgeType.INFO
    }
}