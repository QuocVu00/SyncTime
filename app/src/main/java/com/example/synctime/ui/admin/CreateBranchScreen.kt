package com.example.synctime.ui.admin

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.example.synctime.ui.components.AppCard
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.AppTextField
import com.example.synctime.ui.components.BadgeType
import com.example.synctime.ui.components.PrimaryButton
import com.example.synctime.ui.components.StatusBadge
import com.example.synctime.viewmodel.ManagerAdminViewModel

@Composable
fun CreateBranchScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var wifiBssid by remember { mutableStateOf("") }
    var rewardRate by remember { mutableStateOf("1.0") }

    val message by viewModel.message.collectAsState()

    AppScreen {
        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("← Quay lại")
        }

        AppHeader(
            title = "Tạo chi nhánh mới",
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppCard {
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Tên chi nhánh",
                placeholder = "Ví dụ: Chi nhánh Quận 12"
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = address,
                onValueChange = { address = it },
                label = "Địa chỉ",
                placeholder = "Ví dụ: Quận 12, TP.HCM"
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = wifiBssid,
                onValueChange = { wifiBssid = it },
                label = "BSSID Wi-Fi",
                placeholder = "Ví dụ: A1:B2:C3:D4:E5:F6"
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = rewardRate,
                onValueChange = { rewardRate = it },
                label = "Reward rate",
                placeholder = "Ví dụ: 1.0"
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Tạo chi nhánh",
                onClick = {
                    viewModel.createBranch(
                        name = name,
                        address = address,
                        wifiBssid = wifiBssid,
                        rewardRateText = rewardRate
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (message.isNotBlank()) {
            StatusBadge(
                text = message,
                type = getCreateBranchMessageType(message)
            )
        }
    }
}

private fun getCreateBranchMessageType(message: String): BadgeType {
    return when {
        message.contains("Đã tạo", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("thành công", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("Vui lòng", ignoreCase = true) -> BadgeType.WARNING
        message.contains("lỗi", ignoreCase = true) -> BadgeType.ERROR
        message.contains("thất bại", ignoreCase = true) -> BadgeType.ERROR
        else -> BadgeType.INFO
    }
}