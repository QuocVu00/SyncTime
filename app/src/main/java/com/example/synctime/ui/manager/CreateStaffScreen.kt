package com.example.synctime.ui.manager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.data.model.PositionType
import com.example.synctime.ui.components.AppCard
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.AppTextField
import com.example.synctime.ui.components.BadgeType
import com.example.synctime.ui.components.PrimaryButton
import com.example.synctime.ui.components.StatusBadge
import com.example.synctime.ui.theme.AppColors
import com.example.synctime.viewmodel.ManagerAdminViewModel

@Composable
fun CreateStaffScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("123456") }
    var branchId by remember { mutableStateOf("1") }

    var selectedPosition by remember { mutableStateOf(PositionType.SERVER) }
    var expanded by remember { mutableStateOf(false) }

    val message by viewModel.message.collectAsState()

    AppScreen {
        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("← Quay lại")
        }

        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
        ) {
            AppHeader(
                title = "Tạo nhân viên mới",
                subtitle = "Manager thêm nhân viên vào chi nhánh"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Họ tên nhân viên",
                placeholder = "Ví dụ: Nguyễn Văn An"
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email đăng nhập",
                placeholder = "Ví dụ: an@gmail.com"
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = "Mật khẩu tạm",
                placeholder = "Ví dụ: 123456"
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = branchId,
                onValueChange = { branchId = it.filter { char -> char.isDigit() } },
                label = "Mã chi nhánh",
                placeholder = "Ví dụ: 1"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Chức vụ",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        expanded = true
                    }
                ) {
                    Text(
                        text = selectedPosition.displayName,
                        color = AppColors.TextPrimary
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PositionType.values().forEach { position ->
                        DropdownMenuItem(
                            text = {
                                Text(text = position.displayName)
                            },
                            onClick = {
                                selectedPosition = position
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotBlank()) {
                StatusBadge(
                    text = message,
                    type = getCreateStaffMessageType(message)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            PrimaryButton(
                text = "Tạo nhân viên",
                onClick = {
                    viewModel.createStaff(
                        fullName = fullName,
                        email = email,
                        password = password,
                        position = selectedPosition.code,
                        branchIdText = branchId
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun getCreateStaffMessageType(message: String): BadgeType {
    return when {
        message.contains("Đã tạo", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("thành công", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("Vui lòng", ignoreCase = true) -> BadgeType.WARNING
        message.contains("lỗi", ignoreCase = true) -> BadgeType.ERROR
        message.contains("thất bại", ignoreCase = true) -> BadgeType.ERROR
        else -> BadgeType.INFO
    }
}