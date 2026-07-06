package com.example.synctime

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.synctime.api.ApiClient
import com.example.synctime.api.StaffCreateRequest
import com.example.synctime.api.StaffCreateRequestResponse
import com.example.synctime.utils.AuthManager
import com.example.synctime.utils.RoleGuard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateRequestActivity : AppCompatActivity() {

    private lateinit var typeGroup: RadioGroup
    private lateinit var leaveRadio: RadioButton
    private lateinit var changeShiftRadio: RadioButton
    private lateinit var dateInput: EditText
    private lateinit var reasonInput: EditText
    private lateinit var submitButton: Button

    private val calendar: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!RoleGuard.requireRole(this, "STAFF")) return

        title = "Tạo đơn mới"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 36, 36, 24)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val labelType = TextView(this).apply {
            text = "Loại đơn:"
            textSize = 15f
            setPadding(0, 0, 0, 12)
        }

        typeGroup = RadioGroup(this).apply {
            orientation = RadioGroup.HORIZONTAL
        }

        leaveRadio = RadioButton(this).apply {
            text = "Xin nghỉ"
            id = 1001
            isChecked = true
        }

        changeShiftRadio = RadioButton(this).apply {
            text = "Đổi ca"
            id = 1002
        }

        typeGroup.addView(leaveRadio)
        typeGroup.addView(changeShiftRadio)

        dateInput = EditText(this).apply {
            hint = "Ngày"
            isFocusable = false
            isClickable = true
            setText(dateFormat.format(calendar.time))
            setOnClickListener {
                showDatePicker()
            }
        }

        reasonInput = EditText(this).apply {
            hint = "Nhập lý do"
            minLines = 5
            gravity = Gravity.TOP
        }

        submitButton = Button(this).apply {
            text = "Gửi đơn"
            setOnClickListener {
                submitRequest()
            }
        }

        root.addView(labelType)
        root.addView(typeGroup)
        root.addView(dateInput)
        root.addView(reasonInput, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))
        root.addView(submitButton)

        setContentView(root)
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                dateInput.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun submitRequest() {
        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show()
            RoleGuard.goLogin(this)
            return
        }

        val reason = reasonInput.text.toString().trim()

        if (reason.isBlank()) {
            reasonInput.error = "Nhập lý do"
            return
        }

        val type = if (leaveRadio.isChecked) {
            "LEAVE"
        } else {
            "CHANGE_SHIFT"
        }

        val request = StaffCreateRequest(
            type = type,
            reason = reason,
            targetDate = apiDateFormat.format(calendar.time)
        )

        submitButton.isEnabled = false
        submitButton.text = "Đang gửi..."

        ApiClient.token = token

        ApiClient.create(token).createRequest(request)
            .enqueue(object : Callback<StaffCreateRequestResponse> {
                override fun onResponse(
                    call: Call<StaffCreateRequestResponse>,
                    response: Response<StaffCreateRequestResponse>
                ) {
                    submitButton.isEnabled = true
                    submitButton.text = "Gửi đơn"

                    if (response.code() == 401) {
                        Toast.makeText(
                            this@CreateRequestActivity,
                            "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.",
                            Toast.LENGTH_LONG
                        ).show()

                        RoleGuard.goLogin(this@CreateRequestActivity)
                        return
                    }

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@CreateRequestActivity,
                            "Gửi đơn thành công",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    } else {
                        val errorText = response.errorBody()?.string().orEmpty()

                        Toast.makeText(
                            this@CreateRequestActivity,
                            "Gửi đơn thất bại. Mã lỗi: ${response.code()} $errorText",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StaffCreateRequestResponse>, t: Throwable) {
                    submitButton.isEnabled = true
                    submitButton.text = "Gửi đơn"

                    Toast.makeText(
                        this@CreateRequestActivity,
                        "Lỗi kết nối backend: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}