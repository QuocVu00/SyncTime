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
import com.example.synctime.data.model.PositionSalaryDto
import com.example.synctime.ui.components.AppCard
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.AppTextField
import com.example.synctime.ui.components.BadgeType
import com.example.synctime.ui.components.PrimaryButton
import com.example.synctime.ui.components.StatusBadge
import com.example.synctime.ui.theme.AppColors
import com.example.synctime.viewmodel.ManagerAdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SalarySettingScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    val positionSalaries by viewModel.positionSalaries.collectAsState()
    val message by viewModel.message.collectAsState()

    val salaryInputs = remember {
        mutableStateMapOf<String, String>()
    }

    LaunchedEffect(Unit) {
        viewModel.loadPositionSalaries()
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
            title = "Cài đặt lương chức vụ",
            subtitle = "Admin chỉnh mức lương theo giờ cho từng chức vụ"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppCard {
            Text(
                text = "Quy định tính lương",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lương = tổng giờ làm thực tế × lương theo giờ của chức vụ.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tăng ca vẫn tính theo lương giờ bình thường, không nhân hệ số 1.5.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Riêng Bếp không tính trễ hoặc tăng ca, chỉ ghi nhận giờ vào/ra thực tế.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (message.isNotBlank()) {
            StatusBadge(
                text = message,
                type = getSalaryMessageType(message)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        LazyColumn {
            items(positionSalaries) { item ->
                val currentInput = salaryInputs[item.position]
                    ?: item.hourlyRate.toInt().toString()

                SalaryPositionCard(
                    item = item,
                    inputValue = currentInput,
                    onInputChange = { newValue ->
                        salaryInputs[item.position] = newValue
                    },
                    onUpdateClick = {
                        viewModel.updatePositionSalary(
                            position = item.position,
                            newRateText = currentInput
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SalaryPositionCard(
    item: PositionSalaryDto,
    inputValue: String,
    onInputChange: (String) -> Unit,
    onUpdateClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    AppCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.positionName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Mã chức vụ: ${item.position}",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lương hiện tại: ${currencyFormat.format(item.hourlyRate)}/giờ",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = inputValue,
                onValueChange = { value ->
                    val onlyNumber = value.filter { it.isDigit() }
                    onInputChange(onlyNumber)
                },
                label = "Lương mới theo giờ",
                placeholder = "Ví dụ: 25000"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "Cập nhật lương",
                onClick = onUpdateClick
            )
        }
    }
}

private fun getSalaryMessageType(message: String): BadgeType {
    return when {
        message.contains("Đã cập nhật", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("không hợp lệ", ignoreCase = true) -> BadgeType.ERROR
        message.contains("lỗi", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Không tải", ignoreCase = true) -> BadgeType.ERROR
        else -> BadgeType.INFO
    }
}