package com.example.synctime

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.synctime.api.ApiClient
import com.example.synctime.data.model.AttendanceRequest
import com.example.synctime.data.model.AttendanceStatusResponse
import com.example.synctime.data.model.MessageResponse
import com.example.synctime.utils.AuthManager
import com.example.synctime.utils.RoleGuard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AttendanceActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var wifiText: TextView
    private lateinit var checkInButton: Button
    private lateinit var checkOutButton: Button

    companion object {
        private const val REQ_LOCATION = 2001

        // BSSID test trùng với seed backend của Chi nhánh Quận 12
        private const val DEV_TEST_BSSID = "A1:B2:C3:D4:E5:F6"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!RoleGuard.requireRole(this, "STAFF")) return

        title = "Chấm công"

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
            text = "Chấm công nhân viên"
            textSize = 24f
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 24)
        }

        statusText = TextView(this).apply {
            text = "Đang tải trạng thái chấm công..."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        }

        wifiText = TextView(this).apply {
            text = "Wi-Fi BSSID: ..."
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 28)
        }

        checkInButton = Button(this).apply {
            text = "Chấm công vào"
            isEnabled = false
            setOnClickListener {
                doAttendance(isCheckIn = true)
            }
        }

        checkOutButton = Button(this).apply {
            text = "Chấm công ra"
            isEnabled = false
            setOnClickListener {
                doAttendance(isCheckIn = false)
            }
        }

        val backButton = Button(this).apply {
            text = "Quay lại"
            setOnClickListener {
                finish()
            }
        }

        root.addView(titleText)
        root.addView(statusText)
        root.addView(wifiText)
        root.addView(checkInButton)
        root.addView(checkOutButton)
        root.addView(backButton)

        setContentView(root)

        requestLocationPermissionIfNeeded()
        updateWifiText()

        // Khi mở màn hình chấm công, gọi API để biết staff đang check-in hay chưa
        loadAttendanceStatus()
    }

    private fun requestLocationPermissionIfNeeded() {
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQ_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_LOCATION) {
            updateWifiText()
            loadAttendanceStatus()
        }
    }

    private fun loadAttendanceStatus() {
        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            RoleGuard.goLogin(this)
            return
        }

        statusText.text = "Đang tải trạng thái chấm công..."
        checkInButton.isEnabled = false
        checkOutButton.isEnabled = false

        ApiClient.create(token).getAttendanceStatusCall()
            .enqueue(object : Callback<AttendanceStatusResponse> {
                override fun onResponse(
                    call: Call<AttendanceStatusResponse>,
                    response: Response<AttendanceStatusResponse>
                ) {
                    if (response.code() == 401) {
                        RoleGuard.goLogin(this@AttendanceActivity)
                        return
                    }

                    if (!response.isSuccessful || response.body() == null) {
                        statusText.text = "Không tải được trạng thái chấm công"
                        wifiText.text = "Wi-Fi BSSID: ${getWifiBssid()}"

                        checkInButton.isEnabled = true
                        checkOutButton.isEnabled = false
                        return
                    }

                    val status = response.body()!!

                    if (status.checkedIn) {
                        statusText.text = "Đang làm việc"
                        wifiText.text = "Check-in lúc: ${status.checkInTime ?: "--"}"

                        checkInButton.isEnabled = false
                        checkOutButton.isEnabled = true
                    } else {
                        if (!status.checkInTime.isNullOrBlank() && !status.checkOutTime.isNullOrBlank()) {
                            statusText.text = "Đã hoàn tất chấm công hôm nay"
                            wifiText.text =
                                "Vào: ${status.checkInTime}\nRa: ${status.checkOutTime}"

                            checkInButton.isEnabled = false
                            checkOutButton.isEnabled = false
                        } else {
                            statusText.text = "Chưa chấm công"
                            wifiText.text = "Bạn chưa check-in hôm nay"

                            checkInButton.isEnabled = true
                            checkOutButton.isEnabled = false
                        }
                    }
                }

                override fun onFailure(call: Call<AttendanceStatusResponse>, t: Throwable) {
                    statusText.text = "Lỗi kết nối backend: ${t.message}"
                    wifiText.text = "Wi-Fi BSSID: ${getWifiBssid()}"

                    checkInButton.isEnabled = true
                    checkOutButton.isEnabled = false
                }
            })
    }

    private fun doAttendance(isCheckIn: Boolean) {
        val token = AuthManager.getToken(this)

        if (token.isBlank()) {
            RoleGuard.goLogin(this)
            return
        }

        val deviceId = getAndroidId()
        val bssid = getWifiBssid()

        if (deviceId.isBlank()) {
            Toast.makeText(
                this,
                "Không đọc được Android ID",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (bssid.isBlank()) {
            Toast.makeText(
                this,
                "Không đọc được Wi-Fi BSSID. Hãy bật Wi-Fi, GPS và cấp quyền vị trí.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val request = AttendanceRequest(
            deviceId = deviceId,
            currentBssid = bssid
        )

        checkInButton.isEnabled = false
        checkOutButton.isEnabled = false

        val api = ApiClient.create(token)

        val call = if (isCheckIn) {
            api.checkInCall(request)
        } else {
            api.checkOutCall(request)
        }

        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.code() == 401) {
                    RoleGuard.goLogin(this@AttendanceActivity)
                    return
                }

                val body = response.body()
                val errorText = response.errorBody()?.string().orEmpty()

                if (response.isSuccessful && body != null) {
                    Toast.makeText(
                        this@AttendanceActivity,
                        body.message,
                        Toast.LENGTH_LONG
                    ).show()

                    // Quan trọng:
                    // Sau khi check-in hoặc check-out thành công,
                    // gọi lại trạng thái để cập nhật nút và thông tin trên màn hình.
                    loadAttendanceStatus()
                } else {
                    Toast.makeText(
                        this@AttendanceActivity,
                        body?.message ?: "Chấm công thất bại ${response.code()}: $errorText",
                        Toast.LENGTH_LONG
                    ).show()

                    // Gọi lại trạng thái để tránh nút bị sai
                    loadAttendanceStatus()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(
                    this@AttendanceActivity,
                    "Lỗi kết nối backend: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                // Gọi lại trạng thái khi lỗi kết nối
                loadAttendanceStatus()
            }
        })
    }

    private fun updateWifiText() {
        wifiText.text = "Wi-Fi BSSID: ${getWifiBssid()}"
    }

    private fun getAndroidId(): String {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ).orEmpty()
    }

    private fun getWifiBssid(): String {
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            return DEV_TEST_BSSID
        }

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val bssid = wifiManager.connectionInfo?.bssid.orEmpty().uppercase()

        /*
            Emulator thường trả rỗng hoặc 02:00:00:00:00:00.
            Dùng BSSID test trùng với chi nhánh Quận 12 trong backend seed data.
            Khi chạy điện thoại thật thì có thể xóa fallback này.
        */
        return if (
            bssid.isBlank() ||
            bssid == "02:00:00:00:00:00" ||
            bssid == "<UNKNOWN SSID>"
        ) {
            DEV_TEST_BSSID
        } else {
            bssid
        }
    }
}