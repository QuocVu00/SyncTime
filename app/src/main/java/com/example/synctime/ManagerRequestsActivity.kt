package com.example.synctime

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.synctime.api.ApiClient
import com.example.synctime.data.model.MessageResponse
import com.example.synctime.data.model.RequestDto
import com.example.synctime.utils.AuthManager
import com.example.synctime.utils.RoleGuard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerRequestsActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!RoleGuard.requireRole(this, "MANAGER")) return

        title = "Duyệt yêu cầu nhân viên"

        val scroll = ScrollView(this)

        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        scroll.addView(container)
        setContentView(scroll)

        loadRequests()
    }

    private fun loadRequests() {
        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            RoleGuard.goLogin(this)
            return
        }

        ApiClient.token = token
        val api = ApiClient.create(token)

        container.removeAllViews()
        container.addView(titleText("Đang tải yêu cầu..."))

        api.getRequestsCall().enqueue(object : Callback<List<RequestDto>> {
            override fun onResponse(call: Call<List<RequestDto>>, response: Response<List<RequestDto>>) {
                if (response.code() == 401) {
                    RoleGuard.goLogin(this@ManagerRequestsActivity)
                    return
                }

                if (!response.isSuccessful) {
                    showMessage("Không tải được yêu cầu. Mã lỗi: ${response.code()}")
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
        container.addView(titleText("Danh sách yêu cầu"))

        if (items.isEmpty()) {
            container.addView(normalText("Chưa có yêu cầu nào từ nhân viên"))
            return
        }

        items.forEach { item ->
            val box = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24, 20, 24, 20)
                background = android.graphics.drawable.ColorDrawable(0x11_000000)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 12, 0, 12)
                layoutParams = params
            }

            val typeName = when (item.type) {
                "LEAVE" -> "Xin nghỉ"
                "CHANGE_SHIFT" -> "Đổi ca"
                else -> item.type
            }

            box.addView(
                normalText(
                    """
                    #${item.id} - ${item.fullName}
                    Loại đơn: $typeName
                    Lý do: ${item.reason}
                    Ngày tạo: ${item.createdAt}
                    Trạng thái: ${item.status}
                    """.trimIndent()
                )
            )

            if (item.status == "PENDING") {
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                }

                val approveButton = Button(this).apply {
                    text = "Duyệt"
                    setOnClickListener { approveRequest(item.id) }
                }

                val rejectButton = Button(this).apply {
                    text = "Từ chối"
                    setOnClickListener { rejectRequest(item.id) }
                }

                row.addView(
                    approveButton,
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                )

                row.addView(
                    rejectButton,
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                )

                box.addView(row)
            }

            container.addView(box)
        }
    }

    private fun approveRequest(id: Int) {
        val token = AuthManager.getToken(this)
        ApiClient.create(token).approveRequestCall(id)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ManagerRequestsActivity, "Đã duyệt yêu cầu", Toast.LENGTH_SHORT).show()
                        loadRequests()
                    } else {
                        Toast.makeText(this@ManagerRequestsActivity, "Duyệt thất bại: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Toast.makeText(this@ManagerRequestsActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun rejectRequest(id: Int) {
        val token = AuthManager.getToken(this)
        ApiClient.create(token).rejectRequestCall(id)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ManagerRequestsActivity, "Đã từ chối yêu cầu", Toast.LENGTH_SHORT).show()
                        loadRequests()
                    } else {
                        Toast.makeText(this@ManagerRequestsActivity, "Từ chối thất bại: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Toast.makeText(this@ManagerRequestsActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
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
            textSize = 15f
            setPadding(0, 8, 0, 8)
        }
    }
}