package com.example.synctime

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.synctime.api.ApiClient
import com.example.synctime.utils.AuthManager
import com.example.synctime.utils.RoleGuard

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!RoleGuard.requireRole(this, "ADMIN")) return

        title = "Admin"

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
            text = "Xin chào, ${AuthManager.getFullName(this@AdminHomeActivity)}"
            textSize = 24f
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subText = TextView(this).apply {
            text = "Khu vực quản trị hệ thống"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 32)
        }

        root.addView(titleText)
        root.addView(subText)

        root.addView(menuButton("Quản lý chi nhánh") {
            openFeature("ADMIN_BRANCHES")
        })

        root.addView(menuButton("Quản lý nhân viên") {
            openFeature("ADMIN_STAFF")
        })

        root.addView(menuButton("Quản lý ca làm") {
            openFeature("ADMIN_SHIFTS")
        })

        root.addView(menuButton("Báo cáo lương toàn hệ thống") {
            openFeature("SALARY")
        })

        root.addView(menuButton("Cài đặt lương") {
            openFeature("SALARY_SETTING")
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

    private fun openFeature(feature: String) {
        val intent = Intent(this, RoleFeatureActivity::class.java)
        intent.putExtra("feature", feature)
        startActivity(intent)
    }

    private fun logout() {
        // Xóa session đã lưu trong SharedPreferences
        AuthManager.logout(this)

        // Xóa token đang giữ trong RAM
        ApiClient.token = null

        // Quay về LoginActivity và xóa toàn bộ màn hình cũ
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
    }
}