package com.example.synctime

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.synctime.api.ApiClient
import com.example.synctime.data.model.BranchDto
import com.example.synctime.data.model.SalaryDto
import com.example.synctime.data.model.ShiftDto
import com.example.synctime.data.model.StaffDto
import com.example.synctime.utils.AuthManager
import com.example.synctime.utils.RoleGuard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoleFeatureActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val feature = intent.getStringExtra("feature").orEmpty()

        val allowed = when (feature) {
            "ADMIN_BRANCHES", "ADMIN_STAFF", "ADMIN_SHIFTS" -> arrayOf("ADMIN")
            "MANAGER_STAFF" -> arrayOf("MANAGER")
            "SALARY" -> arrayOf("ADMIN", "MANAGER")
            else -> arrayOf("ADMIN", "MANAGER")
        }

        if (!RoleGuard.requireRole(this, *allowed)) return

        title = when (feature) {
            "ADMIN_BRANCHES" -> "Quản lý chi nhánh"
            "ADMIN_STAFF" -> "Quản lý nhân viên"
            "ADMIN_SHIFTS" -> "Quản lý ca làm"
            "MANAGER_STAFF" -> "Nhân viên chi nhánh"
            "SALARY" -> "Báo cáo lương"
            else -> "Chức năng"
        }

        val scroll = ScrollView(this)

        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        scroll.addView(container)
        setContentView(scroll)

        loadFeature(feature)
    }

    private fun loadFeature(feature: String) {
        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            RoleGuard.goLogin(this)
            return
        }

        ApiClient.token = token
        val api = ApiClient.create(token)

        showLoading()

        when (feature) {
            "ADMIN_BRANCHES" -> {
                api.getBranchesCall().enqueue(object : Callback<List<BranchDto>> {
                    override fun onResponse(call: Call<List<BranchDto>>, response: Response<List<BranchDto>>) {
                        if (handleUnauthorized(response.code())) return
                        if (!response.isSuccessful) {
                            showError("Không tải được chi nhánh. Mã lỗi: ${response.code()}")
                            return
                        }

                        showBranches(response.body().orEmpty())
                    }

                    override fun onFailure(call: Call<List<BranchDto>>, t: Throwable) {
                        showError("Lỗi kết nối: ${t.message}")
                    }
                })
            }

            "ADMIN_STAFF", "MANAGER_STAFF" -> {
                api.getStaffCall().enqueue(object : Callback<List<StaffDto>> {
                    override fun onResponse(call: Call<List<StaffDto>>, response: Response<List<StaffDto>>) {
                        if (handleUnauthorized(response.code())) return
                        if (!response.isSuccessful) {
                            showError("Không tải được nhân viên. Mã lỗi: ${response.code()}")
                            return
                        }

                        showStaff(response.body().orEmpty())
                    }

                    override fun onFailure(call: Call<List<StaffDto>>, t: Throwable) {
                        showError("Lỗi kết nối: ${t.message}")
                    }
                })
            }

            "ADMIN_SHIFTS" -> {
                api.getShiftsCall().enqueue(object : Callback<List<ShiftDto>> {
                    override fun onResponse(call: Call<List<ShiftDto>>, response: Response<List<ShiftDto>>) {
                        if (handleUnauthorized(response.code())) return
                        if (!response.isSuccessful) {
                            showError("Không tải được ca làm. Mã lỗi: ${response.code()}")
                            return
                        }

                        showShifts(response.body().orEmpty())
                    }

                    override fun onFailure(call: Call<List<ShiftDto>>, t: Throwable) {
                        showError("Lỗi kết nối: ${t.message}")
                    }
                })
            }

            "SALARY" -> {
                api.getSalaryCall().enqueue(object : Callback<List<SalaryDto>> {
                    override fun onResponse(call: Call<List<SalaryDto>>, response: Response<List<SalaryDto>>) {
                        if (handleUnauthorized(response.code())) return
                        if (!response.isSuccessful) {
                            showError("Không tải được báo cáo lương. Mã lỗi: ${response.code()}")
                            return
                        }

                        showSalary(response.body().orEmpty())
                    }

                    override fun onFailure(call: Call<List<SalaryDto>>, t: Throwable) {
                        showError("Lỗi kết nối: ${t.message}")
                    }
                })
            }

            else -> showError("Chức năng chưa được khai báo")
        }
    }

    private fun showLoading() {
        container.removeAllViews()
        container.addView(text("Đang tải dữ liệu...", 18f, true))
    }

    private fun showError(message: String) {
        container.removeAllViews()
        container.addView(text(message, 16f, false))
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showBranches(items: List<BranchDto>) {
        container.removeAllViews()
        container.addView(text("Danh sách chi nhánh", 22f, true))

        if (items.isEmpty()) {
            container.addView(text("Chưa có chi nhánh", 16f, false))
            return
        }

        items.forEach {
            container.addView(
                cardText(
                    """
                    #${it.id} - ${it.name}
                    Địa chỉ: ${it.address}
                    Wi-Fi BSSID: ${it.wifiBssid}
                    Hệ số thưởng: ${it.rewardRate}
                    """.trimIndent()
                )
            )
        }
    }

    private fun showStaff(items: List<StaffDto>) {
        container.removeAllViews()
        container.addView(text("Danh sách nhân viên", 22f, true))

        if (items.isEmpty()) {
            container.addView(text("Chưa có nhân viên", 16f, false))
            return
        }

        items.forEach {
            container.addView(
                cardText(
                    """
                    #${it.id} - ${it.fullName}
                    Email: ${it.email}
                    Vai trò: ${it.role}
                    Vị trí: ${it.positionName}
                    Chi nhánh: ${it.branchName}
                    """.trimIndent()
                )
            )
        }
    }

    private fun showShifts(items: List<ShiftDto>) {
        container.removeAllViews()
        container.addView(text("Danh sách ca làm", 22f, true))

        if (items.isEmpty()) {
            container.addView(text("Chưa có ca làm", 16f, false))
            return
        }

        items.forEach {
            container.addView(
                cardText(
                    """
                    #${it.id} - ${it.name}
                    Bắt đầu: ${it.startTime}
                    Kết thúc: ${it.endTime}
                    """.trimIndent()
                )
            )
        }
    }

    private fun showSalary(items: List<SalaryDto>) {
        container.removeAllViews()
        container.addView(text("Báo cáo lương", 22f, true))

        if (items.isEmpty()) {
            container.addView(text("Chưa có dữ liệu lương", 16f, false))
            return
        }

        items.forEach {
            container.addView(
                cardText(
                    """
                    #${it.userId} - ${it.fullName}
                    Số công: ${it.attendanceCount}
                    Lương tạm tính: ${formatMoney(it.estimatedSalary)}
                    """.trimIndent()
                )
            )
        }
    }

    private fun handleUnauthorized(code: Int): Boolean {
        if (code == 401) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show()
            RoleGuard.goLogin(this)
            return true
        }

        return false
    }

    private fun text(value: String, size: Float, bold: Boolean): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            gravity = Gravity.START
            setPadding(0, 12, 0, 12)
            if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }

    private fun cardText(value: String): TextView {
        return TextView(this).apply {
            text = value
            textSize = 15f
            setPadding(24, 20, 24, 20)
            background = android.graphics.drawable.ColorDrawable(0x11_000000)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 12, 0, 12)
            layoutParams = params
        }
    }

    private fun formatMoney(value: Double): String {
        return "%,.0f đ".format(value)
    }
}