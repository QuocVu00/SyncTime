package com.example.synctime.ui.manager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.data.model.PositionType
import com.example.synctime.data.model.ShiftOption
import com.example.synctime.data.model.StaffDto
import com.example.synctime.ui.components.AppCard
import com.example.synctime.ui.components.AppHeader
import com.example.synctime.ui.components.AppScreen
import com.example.synctime.ui.components.AppTextField
import com.example.synctime.ui.components.BadgeType
import com.example.synctime.ui.components.EmployeeSelectCard
import com.example.synctime.ui.components.PrimaryButton
import com.example.synctime.ui.components.SectionTitle
import com.example.synctime.ui.components.ShiftSelectCard
import com.example.synctime.ui.components.StatusBadge
import com.example.synctime.ui.theme.AppColors
import com.example.synctime.viewmodel.ManagerAdminViewModel

@Composable
fun ScheduleManagementScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    val staffList by viewModel.staffList.collectAsState()
    val shiftList by viewModel.shiftList.collectAsState()
    val message by viewModel.message.collectAsState()

    var workDate by remember {
        mutableStateOf("2026-06-30")
    }

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedShift by remember {
        mutableStateOf<ShiftOption?>(null)
    }

    val selectedStaffIds = remember {
        mutableStateListOf<Int>()
    }

    LaunchedEffect(Unit) {
        viewModel.loadStaff()
        viewModel.loadShifts()
    }

    val selectedStaff = staffList.filter {
        selectedStaffIds.contains(it.id)
    }

    val filteredStaff = staffList.filter {
        it.fullName.contains(searchText, ignoreCase = true) ||
                it.positionName.contains(searchText, ignoreCase = true)
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
            title = "Tạo lịch làm",
            subtitle = "Chọn ngày, ca và nhân viên cho một ca làm"
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            item {
                AppCard {
                    Text(
                        text = "Quy định ca làm",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Một ca cần ít nhất 2 nhân viên, bắt buộc có 1 Phục vụ và 1 Pha chế.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bếp chỉ ghi nhận giờ vào/ra, không tính trễ hoặc tăng ca.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                SectionTitle(
                    title = "Ngày làm việc"
                )

                AppTextField(
                    value = workDate,
                    onValueChange = {
                        workDate = it
                    },
                    label = "Ngày làm",
                    placeholder = "Ví dụ: 2026-06-30"
                )

                Spacer(modifier = Modifier.height(12.dp))

                SectionTitle(
                    title = "Chọn ca làm",
                    subtitle = "Chọn một ca để áp dụng cho các nhân viên"
                )
            }

            items(shiftList) { shift ->
                ShiftSelectCard(
                    title = shift.name,
                    time = "${shift.startTime} - ${shift.endTime}",
                    selected = selectedShift?.id == shift.id,
                    onClick = {
                        selectedShift = shift
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                SectionTitle(
                    title = "Chọn nhân viên",
                    subtitle = "Có thể chọn nhiều nhân viên trong một lần tạo lịch"
                )

                AppTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    label = "Tìm kiếm nhân viên",
                    placeholder = "Nhập tên hoặc chức vụ"
                )

                Spacer(modifier = Modifier.height(10.dp))

                SelectAllStaffRow(
                    staffList = filteredStaff,
                    selectedStaffIds = selectedStaffIds
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            items(filteredStaff) { staff ->
                val checked = selectedStaffIds.contains(staff.id)

                EmployeeSelectCard(
                    fullName = staff.fullName,
                    positionName = staff.positionName,
                    branchName = staff.branchName,
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            if (!selectedStaffIds.contains(staff.id)) {
                                selectedStaffIds.add(staff.id)
                            }
                        } else {
                            selectedStaffIds.remove(staff.id)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                ScheduleSummaryCard(
                    selectedStaff = selectedStaff
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (message.isNotBlank()) {
                    StatusBadge(
                        text = message,
                        type = getMessageBadgeType(message)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                PrimaryButton(
                    text = "Tạo lịch làm",
                    onClick = {
                        viewModel.createScheduleForEmployees(
                            workDate = workDate,
                            shift = selectedShift,
                            selectedStaff = selectedStaff
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SelectAllStaffRow(
    staffList: List<StaffDto>,
    selectedStaffIds: SnapshotStateList<Int>
) {
    val allSelected = staffList.isNotEmpty() && staffList.all {
        selectedStaffIds.contains(it.id)
    }

    AppCard {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Chọn tất cả nhân viên đang hiển thị",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary
                )

                Text(
                    text = "${staffList.size} nhân viên",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }

            Checkbox(
                checked = allSelected,
                onCheckedChange = { checked ->
                    if (checked) {
                        staffList.forEach { staff ->
                            if (!selectedStaffIds.contains(staff.id)) {
                                selectedStaffIds.add(staff.id)
                            }
                        }
                    } else {
                        staffList.forEach { staff ->
                            selectedStaffIds.remove(staff.id)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun ScheduleSummaryCard(
    selectedStaff: List<StaffDto>
) {
    val serverCount = selectedStaff.count {
        it.position == PositionType.SERVER.code
    }

    val baristaCount = selectedStaff.count {
        it.position == PositionType.BARISTA.code
    }

    val cashierCount = selectedStaff.count {
        it.position == PositionType.CASHIER.code
    }

    val supervisorCount = selectedStaff.count {
        it.position == PositionType.SUPERVISOR.code
    }

    val kitchenCount = selectedStaff.count {
        it.position == PositionType.KITCHEN.code
    }

    val runnerCount = selectedStaff.count {
        it.position == PositionType.RUNNER.code
    }

    val enoughServer = serverCount >= 1
    val enoughBarista = baristaCount >= 1
    val enoughTotal = selectedStaff.size >= 2

    AppCard {
        Text(
            text = "Tóm tắt ca làm",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        SummaryLine(
            label = "Tổng nhân viên",
            value = "${selectedStaff.size}/2",
            isOk = enoughTotal
        )

        SummaryLine(
            label = "Phục vụ",
            value = "$serverCount/1",
            isOk = enoughServer
        )

        SummaryLine(
            label = "Pha chế",
            value = "$baristaCount/1",
            isOk = enoughBarista
        )

        SummaryLine(
            label = "Thu ngân",
            value = "$cashierCount",
            isOk = true
        )

        SummaryLine(
            label = "Giám sát",
            value = "$supervisorCount",
            isOk = true
        )

        SummaryLine(
            label = "Bếp",
            value = "$kitchenCount",
            isOk = true,
            note = "Không tính trễ/tăng ca"
        )

        SummaryLine(
            label = "Tiếp thực",
            value = "$runnerCount",
            isOk = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!enoughTotal || !enoughServer || !enoughBarista) {
            StatusBadge(
                text = "Ca làm cần ít nhất 1 Phục vụ và 1 Pha chế",
                type = BadgeType.ERROR
            )
        } else {
            StatusBadge(
                text = "Ca làm hợp lệ",
                type = BadgeType.SUCCESS
            )
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
    isOk: Boolean,
    note: String? = null
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextPrimary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isOk) AppColors.Secondary else AppColors.Error
        )
    }

    if (!note.isNullOrBlank()) {
        Text(
            text = note,
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextSecondary
        )
    }
}

private fun getMessageBadgeType(message: String): BadgeType {
    return when {
        message.contains("thành công", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("Đã tạo", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("lỗi", ignoreCase = true) -> BadgeType.ERROR
        message.contains("không", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Vui lòng", ignoreCase = true) -> BadgeType.WARNING
        else -> BadgeType.INFO
    }
}