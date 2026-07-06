package com.example.synctime

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.synctime.api.ApiClient
import com.example.synctime.data.model.LoginRequest
import com.example.synctime.data.model.LoginResponse
import com.example.synctime.utils.AuthManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var demoText: TextView

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
            Khi app bị tắt rồi mở lại, ApiClient.token sẽ bị mất vì nó chỉ nằm trong RAM.
            Vì vậy phải lấy token đã lưu trong AuthManager rồi gán lại vào ApiClient.token.
        */
        val savedToken = AuthManager.getToken(this)

        if (!savedToken.isNullOrBlank()) {
            ApiClient.token = savedToken
            openHome()
            return
        }

        title = "Đăng nhập SyncTime"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(42, 42, 42, 42)
            setBackgroundColor(Color.rgb(245, 247, 250))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(36, 36, 36, 36)
            setBackgroundColor(Color.WHITE)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val titleText = TextView(this).apply {
            text = "SyncTime"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(33, 33, 33))
            setTypeface(null, Typeface.BOLD)
        }

        val subtitleText = TextView(this).apply {
            text = "Đăng nhập để chấm công và xem lịch làm"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(100, 100, 100))
            setPadding(0, 8, 0, 32)
        }

        emailInput = EditText(this).apply {
            hint = "Email"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setSingleLine(true)
            setText("staff@synctime.vn")
            textSize = 16f
            setPadding(24, 12, 24, 12)
        }

        passwordInput = EditText(this).apply {
            hint = "Mật khẩu"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
            setSingleLine(true)
            setText("123456")
            textSize = 16f

            /*
                Chừa khoảng bên phải để icon con mắt không đè lên chữ.
            */
            setPadding(24, 12, 60, 12)
        }

        setupPasswordEyeIcon()

        loginButton = Button(this).apply {
            text = "Đăng nhập"
            textSize = 16f
            setAllCaps(false)

            setOnClickListener {
                login()
            }
        }

        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }

        demoText = TextView(this).apply {
            text = """
                Xin Mời Đăng Nhập 
            """.trimIndent()
            textSize = 13f
            setTextColor(Color.rgb(90, 90, 90))
            setPadding(0, 24, 0, 0)
        }

        card.addView(titleText)
        card.addView(subtitleText)

        card.addView(
            emailInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
        )

        card.addView(
            passwordInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 20)
            }
        )

        card.addView(
            loginButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
        )

        card.addView(progressBar)
        card.addView(demoText)

        root.addView(card)

        setContentView(root)
    }

    private fun setupPasswordEyeIcon() {
        val eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye_off)

        passwordInput.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            eyeIcon,
            null
        )

        passwordInput.compoundDrawablePadding = 16

        passwordInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = passwordInput.compoundDrawables[2]

                if (drawableEnd != null) {
                    val iconStartX = passwordInput.width -
                            passwordInput.paddingEnd -
                            drawableEnd.bounds.width()

                    if (event.x >= iconStartX) {
                        togglePasswordVisibility()
                        return@setOnTouchListener true
                    }
                }
            }

            false
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()

            val eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye)

            passwordInput.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                eyeIcon,
                null
            )
        } else {
            passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()

            val eyeOffIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye_off)

            passwordInput.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                eyeOffIcon,
                null
            )
        }

        passwordInput.setSelection(passwordInput.text.length)
        passwordInput.compoundDrawablePadding = 16
    }

    private fun login() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isBlank()) {
            emailInput.error = "Nhập email"
            return
        }

        if (password.isBlank()) {
            passwordInput.error = "Nhập mật khẩu"
            return
        }

        setLoading(true)

        var androidId = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ).orEmpty()

        if (androidId.isBlank()) {
            androidId = "emulator-${System.currentTimeMillis()}"
        }

        val request = LoginRequest(
            email = email,
            password = password,
            androidId = androidId,
            fcmToken = null
        )

        ApiClient.create().loginCall(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                setLoading(false)

                val body = response.body()

                if (response.isSuccessful && body != null && body.success) {

                    AuthManager.saveLogin(
                        context = this@LoginActivity,
                        token = body.token,
                        user = body.user
                    )

                    ApiClient.token = body.token

                    Toast.makeText(
                        this@LoginActivity,
                        "Đăng nhập thành công",
                        Toast.LENGTH_SHORT
                    ).show()

                    openHome()
                } else {
                    val errorText = response.errorBody()?.string().orEmpty()

                    Toast.makeText(
                        this@LoginActivity,
                        body?.message ?: "Đăng nhập thất bại ${response.code()}: $errorText",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                setLoading(false)

                Toast.makeText(
                    this@LoginActivity,
                    "Không kết nối được backend: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        loginButton.isEnabled = !isLoading
        emailInput.isEnabled = !isLoading
        passwordInput.isEnabled = !isLoading
    }

    private fun openHome() {
        val role = AuthManager.getRole(this)

        val target = when (role) {
            "ADMIN" -> AdminHomeActivity::class.java
            "MANAGER" -> ManagerHomeActivity::class.java
            "STAFF" -> StaffHomeActivity::class.java
            else -> LoginActivity::class.java
        }

        val intent = Intent(this, target)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}