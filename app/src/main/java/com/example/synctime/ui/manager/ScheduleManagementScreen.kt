package com.example.synctime.ui.manager

import android.app.DatePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.synctime.data.model.StaffDto
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
import java.util.Calendar

@Composable
fun ScheduleManagementScreen(
    navController: NavController,
    viewModel: ManagerAdminViewModel
) {
    val context = LocalContext.current

    val staffList by viewModel.staffList.collectAsState()
    val message by viewModel.message.collectAsState()

    var localMessage by remember {
        mutableStateOf("")
    }

    val displayMessage = localMessage.ifBlank {
        message
    }

    var weekTitle by remember {
        mutableStateOf("Ngày 6 - 12")
    }

    var selectedTeam by remember {
        mutableStateOf(TeamType.SERVICE)
    }

    var selectedDay by remember {
        mutableStateOf(WeekDay.MONDAY)
    }

    var selectedStaff by remember {
        mutableStateOf<StaffDto?>(null)
    }

    var startTime by remember {
        mutableStateOf("")
    }

    var endTime by remember {
        mutableStateOf("")
    }

    val scheduleEntries = remember {
        mutableStateListOf<ScheduleEntry>()
    }

    LaunchedEffect(Unit) {
        viewModel.loadStaff()
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
            title = "Tạo Lịch Làm",
            subtitle = ""
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            item {
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1000.dp)
                ) {
                    SectionTitle(
                        title = "Lịch Làm Trong Tuần"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ScheduleExcelTable(
                        weekTitle = weekTitle,
                        entries = scheduleEntries
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 700.dp)
                ) {
                    SectionTitle(
                        title = "Thêm nhân viên vào lịch",
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tuần làm việc",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val calendar = Calendar.getInstance()

                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    weekTitle = getWeekTitleFromSelectedDate(
                                        year = year,
                                        month = month,
                                        dayOfMonth = dayOfMonth
                                    )
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    ) {
                        Text(
                            text = weekTitle,
                            color = AppColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Team",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TeamDropdown(
                        selectedTeam = selectedTeam,
                        onTeamSelected = {
                            selectedTeam = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Thứ / ngày làm",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DayDropdown(
                        selectedDay = selectedDay,
                        onDaySelected = {
                            selectedDay = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Nhân viên",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StaffDropdown(
                        staffList = staffList,
                        selectedStaff = selectedStaff,
                        onStaffSelected = {
                            selectedStaff = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            AppTextField(
                                value = startTime,
                                onValueChange = {
                                    startTime = it
                                },
                                label = "Giờ bắt đầu",
                                placeholder = "Ví dụ: 14"
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            AppTextField(
                                value = endTime,
                                onValueChange = {
                                    endTime = it
                                },
                                label = "Giờ kết thúc",
                                placeholder = "Ví dụ: 23"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    PrimaryButton(
                        text = "Thêm vào bảng lịch",
                        onClick = {
                            val staff = selectedStaff

                            if (staff == null) {
                                localMessage = "Vui lòng chọn nhân viên"
                                return@PrimaryButton
                            }

                            if (startTime.isBlank() || endTime.isBlank()) {
                                localMessage = "Vui lòng nhập giờ bắt đầu và giờ kết thúc"
                                return@PrimaryButton
                            }

                            scheduleEntries.add(
                                ScheduleEntry(
                                    id = scheduleEntries.size + 1,
                                    team = selectedTeam,
                                    day = selectedDay,
                                    staffId = staff.id,
                                    staffName = staff.fullName,
                                    startTime = startTime.trim(),
                                    endTime = endTime.trim()
                                )
                            )

                            localMessage = "Đã thêm ${staff.fullName} vào ${selectedTeam.displayName}, ${selectedDay.displayName}"

                            startTime = ""
                            endTime = ""
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            scheduleEntries.clear()
                            localMessage = "Đã xóa toàn bộ lịch đang nhập"
                        }
                    ) {
                        Text("Xóa bảng lịch")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (displayMessage.isNotBlank()) {
                    StatusBadge(
                        text = displayMessage,
                        type = getMessageBadgeType(displayMessage)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                PrimaryButton(
                    text = "Lưu lịch làm",
                    onClick = {
                        if (scheduleEntries.isEmpty()) {
                            localMessage = "Vui lòng thêm ít nhất 1 lịch làm"
                        } else {
                            localMessage = "Đã có ${scheduleEntries.size} lịch làm. Bước tiếp theo cần nối API để lưu vào database."
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ScheduleExcelTable(
    weekTitle: String,
    entries: List<ScheduleEntry>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .border(
                width = 1.dp,
                color = AppColors.Border
            )
    ) {
        Row {
            TableHeaderCell(
                text = weekTitle,
                width = 120
            )

            WeekDay.values().forEach { day ->
                TableHeaderCell(
                    text = day.displayName,
                    width = 130
                )
            }
        }

        TeamType.values().forEach { team ->
            Row {
                TeamCell(
                    text = team.displayName,
                    width = 120
                )

                WeekDay.values().forEach { day ->
                    val cellEntries = entries.filter {
                        it.team == team && it.day == day
                    }

                    ScheduleCell(
                        entries = cellEntries,
                        width = 130
                    )
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(
    text: String,
    width: Int
) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .height(36.dp)
            .border(
                width = 0.5.dp,
                color = AppColors.Border
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
    }
}

@Composable
private fun TeamCell(
    text: String,
    width: Int
) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .heightIn(min = 72.dp)
            .border(
                width = 0.5.dp,
                color = AppColors.Border
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )
    }
}

@Composable
private fun ScheduleCell(
    entries: List<ScheduleEntry>,
    width: Int
) {
    Column(
        modifier = Modifier
            .width(width.dp)
            .heightIn(min = 72.dp)
            .border(
                width = 0.5.dp,
                color = AppColors.Border
            )
    ) {
        if (entries.isEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            entries.forEach { entry ->
                Text(
                    text = "${shortName(entry.staffName)} ca ${entry.startTime} - ${entry.endTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun TeamDropdown(
    selectedTeam: TeamType,
    onTeamSelected: (TeamType) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

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
                text = selectedTeam.displayName,
                color = AppColors.TextPrimary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            TeamType.values().forEach { team ->
                DropdownMenuItem(
                    text = {
                        Text(team.displayName)
                    },
                    onClick = {
                        onTeamSelected(team)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DayDropdown(
    selectedDay: WeekDay,
    onDaySelected: (WeekDay) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

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
                text = selectedDay.displayName,
                color = AppColors.TextPrimary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            WeekDay.values().forEach { day ->
                DropdownMenuItem(
                    text = {
                        Text(day.displayName)
                    },
                    onClick = {
                        onDaySelected(day)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun StaffDropdown(
    staffList: List<StaffDto>,
    selectedStaff: StaffDto?,
    onStaffSelected: (StaffDto) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

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
                text = selectedStaff?.fullName ?: "Chọn nhân viên",
                color = AppColors.TextPrimary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            if (staffList.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text("Chưa có nhân viên")
                    },
                    onClick = {
                        expanded = false
                    }
                )
            } else {
                staffList.forEach { staff ->
                    DropdownMenuItem(
                        text = {
                            Text("${staff.fullName} - ${staff.positionName}")
                        },
                        onClick = {
                            onStaffSelected(staff)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun shortName(fullName: String): String {
    val parts = fullName.trim().split(" ").filter {
        it.isNotBlank()
    }

    if (parts.isEmpty()) {
        return fullName
    }

    if (parts.size == 1) {
        return parts.first()
    }

    val lastName = parts.last()
    val initials = parts.dropLast(1).mapNotNull {
        it.firstOrNull()?.toString()
    }.joinToString(".")

    return "$initials.$lastName"
}

private fun getWeekTitleFromSelectedDate(
    year: Int,
    month: Int,
    dayOfMonth: Int
): String {
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

    calendar.firstDayOfWeek = Calendar.MONDAY

    val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysFromMonday = when (currentDayOfWeek) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }

    calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday)
    val monday = calendar.get(Calendar.DAY_OF_MONTH)

    calendar.add(Calendar.DAY_OF_MONTH, 6)
    val sunday = calendar.get(Calendar.DAY_OF_MONTH)

    return "Ngày $monday - $sunday"
}

private enum class TeamType(
    val displayName: String
) {
    SERVICE("Team Phục Vụ"),
    BARISTA("Team Pha Chế"),
    RUNNER("Team Tiếp Thực"),
    CASHIER("Thu Ngân")
}

private enum class WeekDay(
    val displayName: String
) {
    MONDAY("Thứ 2"),
    TUESDAY("Thứ 3"),
    WEDNESDAY("Thứ 4"),
    THURSDAY("Thứ 5"),
    FRIDAY("Thứ 6"),
    SATURDAY("Thứ 7"),
    SUNDAY("Chủ Nhật")
}

private data class ScheduleEntry(
    val id: Int,
    val team: TeamType,
    val day: WeekDay,
    val staffId: Int,
    val staffName: String,
    val startTime: String,
    val endTime: String
)

private fun getMessageBadgeType(message: String): BadgeType {
    return when {
        message.contains("thành công", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("Đã thêm", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("Đã có", ignoreCase = true) -> BadgeType.SUCCESS
        message.contains("Đã xóa", ignoreCase = true) -> BadgeType.WARNING
        message.contains("lỗi", ignoreCase = true) -> BadgeType.ERROR
        message.contains("không", ignoreCase = true) -> BadgeType.ERROR
        message.contains("Vui lòng", ignoreCase = true) -> BadgeType.WARNING
        else -> BadgeType.INFO
    }
}