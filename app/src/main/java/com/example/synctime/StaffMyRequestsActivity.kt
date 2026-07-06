package com.example.synctime

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.synctime.api.ApiClient
import com.example.synctime.data.model.RequestDto
import com.example.synctime.utils.AuthManager
import com.example.synctime.utils.RoleGuard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaffMyRequestsActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!RoleGuard.requireRole(this, "STAFF")) return

        title = "Đơn yêu cầu của tôi"

        val scroll = ScrollView(this)

        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        scroll.addView(container)
        setContentView(scroll)

        loadMyRequests()
    }

    private fun loadMyRequests() {
        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            RoleGuard.goLogin(this)
            return
        }

        ApiClient.token = token

        container.removeAllViews()
        container.addView(titleText("Đang tải đơn yêu cầu..."))

        ApiClient.create(token)
            .getMyRequestsCall()
            .enqueue(object : Callback<List<RequestDto>> {
                override fun onResponse(
                    call: Call<List<RequestDto>>,
                    response: Response<List<RequestDto>>
                ) {
                    if (response.code() == 401) {
                        RoleGuard.goLogin(this@StaffMyRequestsActivity)
                        return
                    }

                    if (!response.isSuccessful) {
                        val errorText = response.errorBody()?.string().orEmpty()
                        showMessage("Không tải được đơn. Mã lỗi: ${response.code()} $errorText")
                        return
                    }

                    showRequests(response.body().orEmpty())
                }

                override fun onFailure(call: Call<List<RequestDto>>, t: Throwable) {
                    showMessage("Lỗi kết nối backend: ${t.message}")
                }
            })
    }

    private fun showRequests(items: List<RequestDto>) {
        container.removeAllViews()
        container.addView(titleText("Đơn yêu cầu của tôi"))

        if (items.isEmpty()) {
            container.addView(normalText("Bạn chưa gửi đơn nào"))
            return
        }

        items.forEach { item ->
            container.addView(cardText(formatRequest(item)))
        }
    }

    private fun formatRequest(item: RequestDto): String {
        val typeName = when (item.type) {
            "LEAVE" -> "Xin nghỉ"
            "CHANGE_SHIFT" -> "Đổi ca"
            else -> item.type
        }

        val statusName = when (item.status) {
            "PENDING" -> "Đang chờ duyệt"
            "APPROVED" -> "Đã duyệt"
            "REJECTED" -> "Đã từ chối"
            else -> item.status
        }

        return """
            Mã đơn: #${item.id}
            Loại đơn: $typeName
            Lý do: ${item.reason}
            Trạng thái: $statusName
            Ngày tạo: ${item.createdAt}
        """.trimIndent()
    }

    private fun showMessage(message: String) {
        container.removeAllViews()
        container.addView(normalText(message))
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun titleText(value: String): TextView {
        return TextView(this).apply {
            text = value
            textSize = 22f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 12, 0, 20)
        }
    }

    private fun normalText(value: String): TextView {
        return TextView(this).apply {
            text = value
            textSize = 16f
            setPadding(0, 12, 0, 12)
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
}