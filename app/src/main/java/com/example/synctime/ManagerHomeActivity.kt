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
import com.example.synctime.utils.SessionManager

class ManagerHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!RoleGuard.requireRole(this, "MANAGER")) return

        title = "Quản lý"

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
            text = "Xin chào, ${AuthManager.getFullName(this@ManagerHomeActivity)}"
            textSize = 24f
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subText = TextView(this).apply {
            text = "Khu vực quản lý chi nhánh"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 32)
        }

        root.addView(titleText)
        root.addView(subText)

        root.addView(menuButton("Nhân viên chi nhánh") {
            openFeature("MANAGER_STAFF")
        })

        root.addView(menuButton("Tạo nhân viên") {
            startActivity(Intent(this, ManagerCreateStaffActivity::class.java))
        })

        root.addView(menuButton("Tạo lịch làm") {
            startActivity(Intent(this, ManagerCreateScheduleActivity::class.java))
        })

        root.addView(menuButton("Duyệt yêu cầu nhân viên") {
            startActivity(Intent(this, ManagerRequestsActivity::class.java))
        })

        root.addView(menuButton("Báo cáo lương chi nhánh") {
            openFeature("SALARY")
        })

        root.addView(menuButton("Đăng xuất") {
            logout()
        })

        setContentView(root)
    }

    private fun menuButton(text: String, action: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setAllCaps(false)
            textSize = 16f

            setOnClickListener {
                action()
            }

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
        AuthManager.logout(this)

        SessionManager.clearSession(this)

        ApiClient.token = null

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
    }
}