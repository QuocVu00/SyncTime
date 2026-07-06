package com.example.synctime

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.synctime.api.ApiClient
import com.example.synctime.utils.AuthManager
import com.example.synctime.utils.RoleGuard
import com.example.synctime.utils.SessionManager

class StaffHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!RoleGuard.requireRole(this, "STAFF")) return

        title = "Nhân viên"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(48, 48, 48, 48)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val titleText = TextView(this).apply {
            text = "Xin chào, ${AuthManager.getFullName(this@StaffHomeActivity)}"
            textSize = 24f
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subText = TextView(this).apply {
            text = "Khu vực nhân viên"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 32)
        }

        root.addView(titleText)
        root.addView(subText)

        root.addView(menuButton("Lịch làm việc của tôi") {
            openScreen("com.example.synctime.MyScheduleActivity")
        })

        root.addView(menuButton("Gửi yêu cầu nghỉ / đổi ca") {
            openScreen("com.example.synctime.CreateRequestActivity")
        })

        root.addView(menuButton("Đơn yêu cầu của tôi") {
            startActivity(Intent(this, StaffMyRequestsActivity::class.java))
        })

        root.addView(menuButton("Chấm công") {
            openScreen("com.example.synctime.AttendanceActivity")
        })

        root.addView(menuButton("Đăng xuất") {
            logout()
        })

        setContentView(root)
    }

    private fun menuButton(text: String, action: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setOnClickListener { action() }

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }
    }

    private fun openScreen(className: String) {
        try {
            val intent = Intent()
            intent.setClassName(packageName, className)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Chưa tìm thấy màn hình: $className",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun logout() {
        // Xóa thông tin đăng nhập đã lưu
        AuthManager.logout(this)

        // Xóa session/token đã lưu để không tự đăng nhập lại
        SessionManager.clearSession(this)

        // Xóa token đang giữ trong RAM
        ApiClient.token = null

        // Quay về màn hình đăng nhập và xóa toàn bộ màn hình cũ
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
    }
}