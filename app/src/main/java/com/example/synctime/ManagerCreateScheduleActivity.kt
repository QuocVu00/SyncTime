package com.example.synctime

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.synctime.api.ApiClient
import com.example.synctime.data.model.CreateMultiScheduleRequest
import com.example.synctime.data.model.CreateScheduleRequest
import com.example.synctime.data.model.MessageResponse
import com.example.synctime.data.model.ShiftDto
import com.example.synctime.data.model.ShiftRequest
import com.example.synctime.data.model.StaffDto
import com.example.synctime.utils.AuthManager
import com.example.synctime.utils.RoleGuard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ManagerCreateScheduleActivity : AppCompatActivity() {

    private lateinit var staffSpinner: Spinner
    private lateinit var dateText: TextView
    private lateinit var startTimeText: TextView
    private lateinit var endTimeText: TextView
    private lateinit var submitButton: Button

    private val staffList = mutableListOf<StaffDto>()

    private val calendar = Calendar.getInstance()
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val viewDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))

    private var startHour = 8
    private var startMinute = 0
    private var endHour = 12
    private var endMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!RoleGuard.requireRole(this, "MANAGER")) return

        title = "Tạo lịch làm"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(40, 40, 40, 40)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val titleText = TextView(this).apply {
            text = "Tạo lịch làm cho nhân viên"
            textSize = 23f
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 28)
        }

        val staffLabel = TextView(this).apply {
            text = "Chọn nhân viên"
            textSize = 15f
            setPadding(0, 8, 0, 8)
        }

        staffSpinner = Spinner(this)

        val dateLabel = TextView(this).apply {
            text = "Ngày làm"
            textSize = 15f
            setPadding(0, 20, 0, 8)
        }

        dateText = TextView(this).apply {
            text = viewDateFormat.format(calendar.time)
            textSize = 18f
            setPadding(0, 12, 0, 20)
            setOnClickListener {
                showDatePicker()
            }
        }

        val startLabel = TextView(this).apply {
            text = "Giờ vào"
            textSize = 15f
            setPadding(0, 20, 0, 8)
        }

        startTimeText = TextView(this).apply {
            text = formatTime(startHour, startMinute)
            textSize = 18f
            setPadding(0, 12, 0, 20)
            setOnClickListener {
                showTimePicker(isStartTime = true)
            }
        }

        val endLabel = TextView(this).apply {
            text = "Giờ ra"
            textSize = 15f
            setPadding(0, 20, 0, 8)
        }

        endTimeText = TextView(this).apply {
            text = formatTime(endHour, endMinute)
            textSize = 18f
            setPadding(0, 12, 0, 20)
            setOnClickListener {
                showTimePicker(isStartTime = false)
            }
        }

        submitButton = Button(this).apply {
            text = "Tạo lịch"
            setOnClickListener {
                createShiftThenSchedule()
            }
        }

        val backButton = Button(this).apply {
            text = "Quay lại"
            setOnClickListener { finish() }
        }

        root.addView(titleText)
        root.addView(staffLabel)
        root.addView(staffSpinner)
        root.addView(dateLabel)
        root.addView(dateText)
        root.addView(startLabel)
        root.addView(startTimeText)
        root.addView(endLabel)
        root.addView(endTimeText)
        root.addView(submitButton)
        root.addView(backButton)

        setContentView(root)

        loadStaff()
    }

    private fun loadStaff() {
        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            RoleGuard.goLogin(this)
            return
        }

        ApiClient.token = token

        ApiClient.create(token).getStaffCall()
            .enqueue(object : Callback<List<StaffDto>> {
                override fun onResponse(
                    call: Call<List<StaffDto>>,
                    response: Response<List<StaffDto>>
                ) {
                    if (response.code() == 401) {
                        RoleGuard.goLogin(this@ManagerCreateScheduleActivity)
                        return
                    }

                    if (!response.isSuccessful) {
                        Toast.makeText(
                            this@ManagerCreateScheduleActivity,
                            "Không tải được nhân viên. Mã lỗi: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    staffList.clear()
                    staffList.addAll(
                        response.body()
                            .orEmpty()
                            .filter { it.role.uppercase() == "STAFF" }
                    )

                    bindStaffSpinner()
                }

                override fun onFailure(call: Call<List<StaffDto>>, t: Throwable) {
                    Toast.makeText(
                        this@ManagerCreateScheduleActivity,
                        "Lỗi tải nhân viên: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun bindStaffSpinner() {
        val names = if (staffList.isEmpty()) {
            listOf("Chưa có nhân viên")
        } else {
            staffList.map { "${it.fullName} - ${it.positionName}" }
        }

        staffSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            names
        )
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                dateText.text = viewDateFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val currentHour = if (isStartTime) startHour else endHour
        val currentMinute = if (isStartTime) startMinute else endMinute

        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                if (isStartTime) {
                    startHour = hourOfDay
                    startMinute = minute
                    startTimeText.text = formatTime(startHour, startMinute)
                } else {
                    endHour = hourOfDay
                    endMinute = minute
                    endTimeText.text = formatTime(endHour, endMinute)
                }
            },
            currentHour,
            currentMinute,
            true
        ).show()
    }

    private fun createShiftThenSchedule() {
        if (staffList.isEmpty()) {
            Toast.makeText(this, "Chưa có nhân viên để tạo lịch", Toast.LENGTH_LONG).show()
            return
        }

        val startTime = formatTime(startHour, startMinute)
        val endTime = formatTime(endHour, endMinute)

        if (!isEndTimeAfterStartTime()) {
            Toast.makeText(this, "Giờ ra phải lớn hơn giờ vào", Toast.LENGTH_LONG).show()
            return
        }

        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            RoleGuard.goLogin(this)
            return
        }

        submitButton.isEnabled = false
        submitButton.text = "Đang tạo ca..."

        val workDateView = viewDateFormat.format(calendar.time)

        val shiftRequest = ShiftRequest(
            name = "Ca $startTime - $endTime ($workDateView)",
            startTime = startTime,
            endTime = endTime
        )

        ApiClient.create(token).createShiftCall(shiftRequest)
            .enqueue(object : Callback<ShiftDto> {
                override fun onResponse(
                    call: Call<ShiftDto>,
                    response: Response<ShiftDto>
                ) {
                    if (response.code() == 401) {
                        submitButton.isEnabled = true
                        submitButton.text = "Tạo lịch"
                        RoleGuard.goLogin(this@ManagerCreateScheduleActivity)
                        return
                    }

                    if (!response.isSuccessful || response.body() == null) {
                        submitButton.isEnabled = true
                        submitButton.text = "Tạo lịch"

                        val errorText = response.errorBody()?.string().orEmpty()

                        Toast.makeText(
                            this@ManagerCreateScheduleActivity,
                            "Tạo ca thất bại ${response.code()}: $errorText",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    createSchedule(response.body()!!)
                }

                override fun onFailure(call: Call<ShiftDto>, t: Throwable) {
                    submitButton.isEnabled = true
                    submitButton.text = "Tạo lịch"

                    Toast.makeText(
                        this@ManagerCreateScheduleActivity,
                        "Lỗi tạo ca: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun createSchedule(shift: ShiftDto) {
        val token = AuthManager.getToken(this)
        val staff = staffList[staffSpinner.selectedItemPosition]
        val workDate = apiDateFormat.format(calendar.time)

        val body = CreateMultiScheduleRequest(
            items = listOf(
                CreateScheduleRequest(
                    userId = staff.id,
                    shiftId = shift.id,
                    workDate = workDate
                )
            )
        )

        submitButton.text = "Đang tạo lịch..."

        ApiClient.create(token)
            .createMultiScheduleCall(body)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    submitButton.isEnabled = true
                    submitButton.text = "Tạo lịch"

                    if (response.code() == 401) {
                        RoleGuard.goLogin(this@ManagerCreateScheduleActivity)
                        return
                    }

                    val errorText = response.errorBody()?.string().orEmpty()

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ManagerCreateScheduleActivity,
                            response.body()?.message ?: "Đã tạo lịch làm",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ManagerCreateScheduleActivity,
                            "Tạo lịch thất bại ${response.code()}: $errorText",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    submitButton.isEnabled = true
                    submitButton.text = "Tạo lịch"

                    Toast.makeText(
                        this@ManagerCreateScheduleActivity,
                        "Lỗi kết nối backend: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun formatTime(hour: Int, minute: Int): String {
        return "%02d:%02d".format(hour, minute)
    }

    private fun isEndTimeAfterStartTime(): Boolean {
        val startTotal = startHour * 60 + startMinute
        val endTotal = endHour * 60 + endMinute
        return endTotal > startTotal
    }
}