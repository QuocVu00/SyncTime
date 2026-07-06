package com.example.synctime

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.synctime.api.ApiClient
import com.example.synctime.data.model.CreateStaffRequest
import com.example.synctime.data.model.StaffDto
import com.example.synctime.utils.AuthManager
import com.example.synctime.utils.RoleGuard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerCreateStaffActivity : AppCompatActivity() {

    private lateinit var fullNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var positionSpinner: Spinner
    private lateinit var salaryInput: EditText
    private lateinit var submitButton: Button

    private val positions = listOf(
        PositionOption("SERVER", "Phục vụ"),
        PositionOption("BARISTA", "Pha chế"),
        PositionOption("KITCHEN", "Bếp"),
        PositionOption("CASHIER", "Thu ngân")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!RoleGuard.requireRole(this, "MANAGER")) return

        title = "Tạo nhân viên"

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
            text = "Tạo nhân viên chi nhánh"
            textSize = 24f
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 28)
        }

        fullNameInput = EditText(this).apply {
            hint = "Họ tên nhân viên"
            setSingleLine(true)
        }

        emailInput = EditText(this).apply {
            hint = "Email đăng nhập"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setSingleLine(true)
        }

        passwordInput = EditText(this).apply {
            hint = "Mật khẩu"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSingleLine(true)
            setText("123456")
        }

        val positionLabel = TextView(this).apply {
            text = "Vị trí"
            textSize = 15f
            setPadding(0, 18, 0, 8)
        }

        positionSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@ManagerCreateStaffActivity,
                android.R.layout.simple_spinner_dropdown_item,
                positions.map { it.name }
            )
        }

        salaryInput = EditText(this).apply {
            hint = "Lương cơ bản"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine(true)
            setText("8000000")
        }

        submitButton = Button(this).apply {
            text = "Tạo nhân viên"
            setOnClickListener {
                createStaff()
            }
        }

        val backButton = Button(this).apply {
            text = "Quay lại"
            setOnClickListener { finish() }
        }

        root.addView(titleText)
        root.addView(fullNameInput)
        root.addView(emailInput)
        root.addView(passwordInput)
        root.addView(positionLabel)
        root.addView(positionSpinner)
        root.addView(salaryInput)
        root.addView(submitButton)
        root.addView(backButton)

        setContentView(root)
    }

    private fun createStaff() {
        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            RoleGuard.goLogin(this)
            return
        }

        val fullName = fullNameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val salary = salaryInput.text.toString().trim().toDoubleOrNull() ?: 0.0
        val position = positions[positionSpinner.selectedItemPosition].code

        if (fullName.isBlank()) {
            fullNameInput.error = "Nhập họ tên"
            return
        }

        if (email.isBlank()) {
            emailInput.error = "Nhập email"
            return
        }

        if (password.isBlank()) {
            passwordInput.error = "Nhập mật khẩu"
            return
        }

        val body = CreateStaffRequest(
            fullName = fullName,
            email = email,
            password = password,
            role = "STAFF",
            branchId = null,
            position = position,
            baseSalary = salary
        )

        submitButton.isEnabled = false
        submitButton.text = "Đang tạo..."

        ApiClient.create(token)
            .createStaffCall(body)
            .enqueue(object : Callback<StaffDto> {
                override fun onResponse(
                    call: Call<StaffDto>,
                    response: Response<StaffDto>
                ) {
                    submitButton.isEnabled = true
                    submitButton.text = "Tạo nhân viên"

                    if (response.code() == 401) {
                        RoleGuard.goLogin(this@ManagerCreateStaffActivity)
                        return
                    }

                    val errorText = response.errorBody()?.string().orEmpty()

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ManagerCreateStaffActivity,
                            "Tạo nhân viên thành công",
                            Toast.LENGTH_LONG
                        ).show()

                        clearForm()
                    } else {
                        Toast.makeText(
                            this@ManagerCreateStaffActivity,
                            "Tạo nhân viên thất bại ${response.code()}: $errorText",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StaffDto>, t: Throwable) {
                    submitButton.isEnabled = true
                    submitButton.text = "Tạo nhân viên"

                    Toast.makeText(
                        this@ManagerCreateStaffActivity,
                        "Lỗi kết nối backend: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun clearForm() {
        fullNameInput.setText("")
        emailInput.setText("")
        passwordInput.setText("123456")
        salaryInput.setText("8000000")
        positionSpinner.setSelection(0)
    }

    private data class PositionOption(
        val code: String,
        val name: String
    )
}